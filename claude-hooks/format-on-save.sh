#!/bin/bash
# =============================================================================
# ARC Labs Studio - Format on Save Hook (PostToolUse)
# =============================================================================
# Auto-formats .kt/.kts files after Edit/Write operations using ktlint.
# Runs in ~50-100ms per file. Imperceptible to the user.
#
# Input: PostToolUse JSON on stdin (tool_name, tool_input, tool_response)
# Output: JSON feedback if formatting was applied
# =============================================================================

set -euo pipefail

INPUT=$(cat)

# Extract the file path from tool_input
FILE_PATH=$(echo "$INPUT" | jq -r '.tool_input.file_path // empty')

# Skip if no file path or not a Kotlin file
if [[ -z "$FILE_PATH" || ( "$FILE_PATH" != *.kt && "$FILE_PATH" != *.kts ) ]]; then
    exit 0
fi

# Skip if file doesn't exist (e.g., was deleted)
if [[ ! -f "$FILE_PATH" ]]; then
    exit 0
fi

# Find ktlint binary
KTLINT=""
if command -v ktlint &>/dev/null; then
    KTLINT="ktlint"
elif [[ -x /opt/homebrew/bin/ktlint ]]; then
    KTLINT="/opt/homebrew/bin/ktlint"
elif [[ -x /usr/local/bin/ktlint ]]; then
    KTLINT="/usr/local/bin/ktlint"
elif [[ -x "$HOME/.local/bin/ktlint" ]]; then
    KTLINT="$HOME/.local/bin/ktlint"
fi

if [[ -z "$KTLINT" ]]; then
    exit 0
fi

# Run ktlint format on the file
if $KTLINT --format "$FILE_PATH" --log-level=none 2>/dev/null; then
    echo '{"hookSpecificOutput":{"hookEventName":"PostToolUse","additionalContext":"ktlint format applied to '"$FILE_PATH"'"}}'
fi

exit 0
