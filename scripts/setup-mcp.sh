#!/bin/bash
# =============================================================================
# setup-mcp.sh - Register recommended MCP servers for Claude Code
# =============================================================================
#
# Idempotent script that registers MCP servers needed for Android development.
# Auto-registers servers that don't require API keys (Context7, Android Skills).
# Prints instructions for servers that need credentials (Linear, GitHub).
#
# Usage:
#   ./ARCDevTools-Android/scripts/setup-mcp.sh
#   ./scripts/setup-mcp.sh (from ARCDevTools-Android directory)
#
# =============================================================================

set -e

VERSION="1.0.0"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Logging functions
log_info() { echo -e "${BLUE}ℹ${NC} $1"; }
log_success() { echo -e "${GREEN}✓${NC} $1"; }
log_warning() { echo -e "${YELLOW}⚠${NC} $1"; }
log_error() { echo -e "${RED}✗${NC} $1"; }

# --- Banner ---

echo ""
echo -e "${CYAN}MCP Server Setup v${VERSION}${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# --- Check claude CLI ---

if ! command -v claude &>/dev/null; then
    log_error "claude CLI not found"
    log_info "Install Claude Code: https://docs.anthropic.com/en/docs/claude-code"
    exit 1
fi

# --- Get current MCP list ---

CURRENT_MCPS=$(claude mcp list 2>/dev/null || echo "")

ADDED=0
SKIPPED=0

# --- Register Context7 ---

log_info "Checking Context7 (library documentation)..."
if echo "$CURRENT_MCPS" | grep -q "context7"; then
    log_success "context7 already configured"
    ((SKIPPED++))
else
    if claude mcp add --scope project context7 -- npx -y @upstash/context7-mcp@latest 2>/dev/null; then
        log_success "context7 registered"
        ((ADDED++))
    else
        log_warning "Failed to register context7 — add manually:"
        echo "  claude mcp add context7 -- npx -y @upstash/context7-mcp@latest"
    fi
fi

# --- Register Android Skills MCP ---

echo ""
log_info "Checking Android Skills (Google official skills)..."
if echo "$CURRENT_MCPS" | grep -q "android-skills"; then
    log_success "android-skills already configured"
    ((SKIPPED++))
else
    if claude mcp add --scope project android-skills -- npx -y android-skills-mcp 2>/dev/null; then
        log_success "android-skills registered"
        ((ADDED++))
    else
        log_warning "Failed to register android-skills — add manually:"
        echo "  claude mcp add android-skills -- npx -y android-skills-mcp"
    fi
fi

# --- Summary ---

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
log_success "MCP setup complete"
echo ""
echo "  Added:   $ADDED"
echo "  Skipped: $SKIPPED (already configured)"
echo ""

# --- Manual setup instructions ---

echo -e "${CYAN}Servers requiring API keys (manual setup):${NC}"
echo ""
echo "  GitHub:"
echo "    claude mcp add github -- npx -y @anthropic/github-mcp@latest"
echo "    Requires: GITHUB_TOKEN environment variable"
echo ""
echo "  Linear:"
echo "    claude mcp add linear -- npx -y @anthropic/linear-mcp@latest"
echo "    Requires: LINEAR_API_KEY environment variable"
echo ""
echo -e "${BLUE}See ARCKnowledge-Android/Tools/mcp-setup.md for full configuration.${NC}"
echo ""
