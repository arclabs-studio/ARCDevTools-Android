# Configuration Guide

This guide covers how to customize ARCDevTools-Android configurations for your project.

## Table of Contents

- [Customizing detekt](#customizing-detekt)
- [Customizing ktlint](#customizing-ktlint)
- [Git Hooks](#git-hooks)
- [Makefile](#makefile)

---

## Customizing detekt

### Override Rules

You can override specific detekt rules by creating a local configuration file:

```yaml
# config/detekt/detekt-overrides.yml
complexity:
  LongMethod:
    threshold: 80  # Override from 60 to 80
  LargeClass:
    threshold: 800  # Override from 600 to 800
```

Then reference both files in your `build.gradle.kts`:

```kotlin
detekt {
    config.setFrom(
        "${rootProject.projectDir}/config/detekt/detekt.yml",
        "${rootProject.projectDir}/config/detekt/detekt-overrides.yml"
    )
}
```

Later files override earlier ones, so your overrides take precedence.

### Disable Rules

To disable a specific rule:

```yaml
# In your override file
style:
  ForbiddenComment:
    active: false
```

### Exclude Paths

To exclude specific paths from detekt analysis:

```kotlin
detekt {
    source.setFrom(
        "src/main/kotlin",
        "src/main/java"
    )
    // Automatically excludes build directories
}
```

### Baseline

To create a baseline for existing issues:

```bash
./gradlew detektBaseline
```

Then reference it:

```kotlin
detekt {
    baseline = file("config/detekt/baseline.xml")
}
```

---

## Customizing ktlint

### EditorConfig Overrides

ktlint is configured via `.editorconfig`. To customize rules, edit the `.editorconfig` at your project root:

```editorconfig
[*.{kt,kts}]
# Disable a rule
ktlint_standard_no-wildcard-imports = disabled

# Change max line length
max_line_length = 140

# Disable Compose rules for non-Compose modules
ktlint_compose_modifier-missing-check = disabled
```

### Per-Directory Configuration

You can create additional `.editorconfig` files in subdirectories:

```
project-root/
├── .editorconfig              # Global rules
├── app/
│   └── .editorconfig          # App-specific overrides
└── library/
    └── .editorconfig          # Library-specific overrides
```

### Suppress Rules Inline

Use `@Suppress` annotations for specific cases:

```kotlin
@Suppress("ktlint:standard:max-line-length")
val longString = "This is a very long string that exceeds the line length limit for a good reason"
```

---

## Git Hooks

### Customizing Pre-commit

The pre-commit hook runs ktlint format, ktlint check, and detekt. To customize:

1. Copy the hook to your project: `cp ARCDevTools-Android/hooks/pre-commit .git/hooks/pre-commit`
2. Edit `.git/hooks/pre-commit` to your needs
3. Keep it executable: `chmod +x .git/hooks/pre-commit`

### Skipping Hooks

For emergency commits:

```bash
SKIP_HOOKS=1 git commit -m "hotfix: emergency fix"
```

Or bypass entirely:

```bash
git commit --no-verify -m "hotfix: emergency fix"
```

**Note:** CI will still run all checks, so skipped hooks will be caught.

### Disabling Pre-push Tests

If pre-push tests are too slow for your workflow, you can remove just the pre-push hook:

```bash
rm .git/hooks/pre-push
```

---

## Makefile

### Adding Custom Targets

You can add custom targets to the generated Makefile:

```makefile
# Custom target
build-release:
	@./gradlew assembleRelease --daemon

# Custom lint with specific module
lint-app:
	@./gradlew :app:detekt :app:ktlintCheck --daemon
```

### Overriding Default Targets

To override a default target, simply redefine it in your Makefile:

```makefile
# Override the test target to run all tests
test:
	@./gradlew test --daemon
```

---

## See Also

- [Getting Started](getting-started.md) - Installation walkthrough
- [Integration Guide](integration.md) - build.gradle.kts setup
- [Troubleshooting](troubleshooting.md) - Common issues
