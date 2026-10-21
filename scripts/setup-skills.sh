#!/bin/bash
# =============================================================================
# setup-skills.sh - Setup ARCKnowledge-Android skills for Claude Code
# =============================================================================
#
# This script creates symlinks from .claude/skills/ to ARCKnowledge-Android
# skills, making them accessible from the project root.
#
# Usage:
#   ./ARCDevTools-Android/scripts/setup-skills.sh
#   ./scripts/setup-skills.sh (from ARCDevTools-Android directory)
#
# Why this is needed:
#   Claude Code only discovers skills in .claude/skills/ at the project root.
#   Skills in subdirectories (like submodules) are not automatically accessible.
#   This script creates symlinks to maintain a single source of truth while
#   making skills available at the project level.
#
# =============================================================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Logging functions
log_info() { echo -e "${BLUE}ℹ${NC} $1"; }
log_success() { echo -e "${GREEN}✓${NC} $1"; }
log_warning() { echo -e "${YELLOW}⚠${NC} $1"; }
log_error() { echo -e "${RED}✗${NC} $1"; }

# Find project root (look for gradlew, build.gradle.kts, or .git)
find_project_root() {
    local dir="$PWD"
    while [[ "$dir" != "/" ]]; do
        if [[ -f "$dir/gradlew" ]] || [[ -f "$dir/build.gradle.kts" ]] || [[ -d "$dir/.git" ]]; then
            echo "$dir"
            return 0
        fi
        dir=$(dirname "$dir")
    done
    echo "$PWD"
}

# Find ARCKnowledge-Android directory
find_arcknowledge() {
    local project_root="$1"
    local possible_paths=(
        # Direct submodule
        "$project_root/ARCKnowledge-Android"
        # Inside ARCDevTools-Android submodule
        "$project_root/ARCDevTools-Android/ARCKnowledge-Android"
        # Inside nested path
        "$project_root/Submodules/ARCDevTools-Android/ARCKnowledge-Android"
        "$project_root/Dependencies/ARCDevTools-Android/ARCKnowledge-Android"
    )

    for path in "${possible_paths[@]}"; do
        if [[ -d "$path/.claude/skills" ]]; then
            echo "$path"
            return 0
        fi
    done

    return 1
}

# Get relative path from source to target
relative_path() {
    local source="$1"
    local target="$2"
    python3 -c "import os.path; print(os.path.relpath('$target', '$source'))"
}

# Main function
main() {
    echo ""
    echo "╔════════════════════════════════════════════════════════════════╗"
    echo "║      ARCKnowledge-Android Skills Setup for Claude Code        ║"
    echo "╚════════════════════════════════════════════════════════════════╝"
    echo ""

    # Find project root
    PROJECT_ROOT=$(find_project_root)
    log_info "Project root: $PROJECT_ROOT"

    # Find ARCKnowledge-Android
    ARCKNOWLEDGE_PATH=$(find_arcknowledge "$PROJECT_ROOT") || {
        log_warning "No .claude/skills/ directory found in ARCKnowledge-Android"
        log_info "Skills will be available after ARCKnowledge-Android is updated with skill definitions."
        log_info "Expected locations:"
        log_info "  - $PROJECT_ROOT/ARCKnowledge-Android/.claude/skills/"
        log_info "  - $PROJECT_ROOT/ARCDevTools-Android/ARCKnowledge-Android/.claude/skills/"
        echo ""
        log_info "Make sure ARCDevTools-Android is properly set up as a submodule:"
        log_info "  git submodule add https://github.com/arclabs-studio/ARCDevTools-Android.git ARCDevTools-Android"
        log_info "  git submodule update --init --recursive"
        exit 0
    }
    log_success "Found ARCKnowledge-Android: $ARCKNOWLEDGE_PATH"

    # Source skills directory
    SKILLS_SOURCE="$ARCKNOWLEDGE_PATH/.claude/skills"
    if [[ ! -d "$SKILLS_SOURCE" ]]; then
        log_warning "Skills directory not found: $SKILLS_SOURCE"
        log_info "Skills will be available after ARCKnowledge-Android is updated."
        exit 0
    fi

    # Target directory
    SKILLS_TARGET="$PROJECT_ROOT/.claude/skills"

    # Create .claude/skills directory if it doesn't exist
    if [[ ! -d "$SKILLS_TARGET" ]]; then
        log_info "Creating $SKILLS_TARGET"
        mkdir -p "$SKILLS_TARGET"
    fi

    # Count skills
    local skills_linked=0
    local skills_skipped=0
    local skills_updated=0

    echo ""
    log_info "Linking skills..."
    echo ""

    # Create symlinks for each skill
    for skill_dir in "$SKILLS_SOURCE"/*/; do
        if [[ -d "$skill_dir" ]]; then
            skill_name=$(basename "$skill_dir")
            target_link="$SKILLS_TARGET/$skill_name"

            # Calculate relative path for symlink
            rel_path=$(relative_path "$SKILLS_TARGET" "$skill_dir")
            # Remove trailing slash
            rel_path="${rel_path%/}"

            if [[ -L "$target_link" ]]; then
                # Symlink exists, check if it points to the right place
                current_target=$(readlink "$target_link")
                if [[ "$current_target" == "$rel_path" ]]; then
                    log_info "  $skill_name → already linked"
                    ((skills_skipped++))
                else
                    # Update symlink
                    rm "$target_link"
                    ln -s "$rel_path" "$target_link"
                    log_success "  $skill_name → updated"
                    ((skills_updated++))
                fi
            elif [[ -e "$target_link" ]]; then
                # Something exists but it's not a symlink
                log_warning "  $skill_name → skipped (file/directory exists)"
                ((skills_skipped++))
            else
                # Create new symlink
                ln -s "$rel_path" "$target_link"
                log_success "  $skill_name → linked"
                ((skills_linked++))
            fi
        fi
    done

    echo ""
    echo "════════════════════════════════════════════════════════════════"
    echo ""
    log_success "Skills setup complete!"
    echo ""
    echo "  New links:     $skills_linked"
    echo "  Updated:       $skills_updated"
    echo "  Skipped:       $skills_skipped"
    echo ""

    # Verify skills are accessible
    echo "Linked skills:"
    for skill_link in "$SKILLS_TARGET"/*/; do
        if [[ -d "$skill_link" ]]; then
            skill_name=$(basename "$skill_link")
            if [[ -f "$skill_link/SKILL.md" ]]; then
                echo "  ✓ $skill_name"
            fi
        fi
    done

    echo ""
    log_info "Skills are now available in Claude Code via /skill-name"
    echo ""

    # Git ignore recommendation
    if [[ -f "$PROJECT_ROOT/.gitignore" ]]; then
        if ! grep -q ".claude/skills" "$PROJECT_ROOT/.gitignore" 2>/dev/null; then
            echo ""
            log_warning "Consider adding symlinked skills to .gitignore:"
            echo ""
            echo "  # ARCKnowledge-Android skills (symlinks)"
            echo "  .claude/skills/arc-*"
            echo ""
        fi
    fi
}

# Run main
main "$@"
