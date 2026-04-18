#!/bin/bash
# ARCDevTools-Android - Format Runner
# Runs ktlint format to auto-fix code style
# Version: 1.1.0

set -e

DRY_RUN=false

# Parse arguments
while [[ $# -gt 0 ]]; do
  case $1 in
    --dry-run)
      DRY_RUN=true
      shift
      ;;
    *)
      echo "Usage: $0 [--dry-run]"
      exit 1
      ;;
  esac
done

echo "Running code formatter..."

if [ ! -f "./gradlew" ]; then
  echo "Error: gradlew not found"
  echo "   Run this script from an Android project root"
  exit 1
fi

if [ "$DRY_RUN" = true ]; then
  echo "  Mode: dry-run (no changes)"
  ./gradlew ktlintCheck --daemon
else
  echo "  Mode: apply changes"
  ./gradlew ktlintFormat --daemon
fi

echo ""
echo "Formatting completed"
