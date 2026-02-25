#!/bin/bash
# ARCDevTools-Android - Lint Runner
# Runs detekt and ktlint checks
# Version: 1.0.0

set -e

echo "Running lint checks..."

if [ ! -f "./gradlew" ]; then
  echo "Error: gradlew not found"
  echo "   Run this script from an Android project root"
  exit 1
fi

echo ""
echo "  Running detekt..."
./gradlew detekt --daemon

echo ""
echo "  Running ktlint check..."
./gradlew ktlintCheck --daemon

echo ""
echo "Lint checks completed"
