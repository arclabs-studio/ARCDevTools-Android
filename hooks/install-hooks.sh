#!/bin/bash
# ARCDevTools-Android - Git Hooks Installer
# Version: 1.1.0

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
GIT_HOOKS_DIR=".git/hooks"

echo "Installing git hooks from ARCDevTools-Android..."

# Verify we're in a git repo
if [ ! -d "$GIT_HOOKS_DIR" ]; then
  echo "Error: .git/hooks directory not found"
  echo "   Run this script from the root of a git repository"
  exit 1
fi

# Copy pre-commit hook
if [ -f "$SCRIPT_DIR/pre-commit" ]; then
  cp "$SCRIPT_DIR/pre-commit" "$GIT_HOOKS_DIR/pre-commit"
  chmod +x "$GIT_HOOKS_DIR/pre-commit"
  echo "  pre-commit hook installed"
else
  echo "  Warning: pre-commit script not found"
fi

# Copy pre-push hook
if [ -f "$SCRIPT_DIR/pre-push" ]; then
  cp "$SCRIPT_DIR/pre-push" "$GIT_HOOKS_DIR/pre-push"
  chmod +x "$GIT_HOOKS_DIR/pre-push"
  echo "  pre-push hook installed"
else
  echo "  Warning: pre-push script not found"
fi

echo "Git hooks installed successfully"
