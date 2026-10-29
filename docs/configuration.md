# Configuration Guide

This guide covers how to customize ARCDevTools-Android configurations for your project.

## Table of Contents

- [Customizing detekt](#customizing-detekt)
- [Customizing ktlint](#customizing-ktlint)
- [Coverage Threshold](#coverage-threshold)
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

### Baselines

Baselines are forbidden in ARC projects: detekt runs with `maxIssues: 0` and a finding is fixed,
not recorded. A baseline needs explicit user approval and a Linear ticket that tracks its removal
(see `android-doctrine`, rule 9 "Zero warnings").

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

### Suppressing Findings

Fix the finding instead. `@Suppress` is allowed only for a documented false positive, with a
comment stating why and the user's approval (see `android-doctrine`, rule 9 "Zero warnings"):

```kotlin
// False positive: <why the rule does not apply here>. Approved by <who>, <ticket>.
@Suppress("ktlint:standard:max-line-length")
val longString = "..."
```

---

## Coverage Threshold

The JaCoCo line-coverage gate on `check` reads its minimum from the Gradle property
`arc.coverage.minimum`, a ratio in (0, 1]. The root `gradle.properties` template sets the ARC
default:

```properties
# ARC rule: 0.80 for every module inside an app repo; 1.00 for extracted ARC*-Android packages.
arc.coverage.minimum=0.80
```

- **App repos** (e.g. FavRes): keep `0.80`; it applies to every module, including `domain` and
  `core:*`.
- **Extracted ARC*-Android packages**: set `arc.coverage.minimum=1.00`.
- A one-off run can override it: `./gradlew check -Parc.coverage.minimum=1.00`.

When the property is absent the plugins use `0.80`. Any value that is not a number in (0, 1]
fails the build at configuration time with a message naming the property.

### Coverage Exclusions

Android modules already exclude generated code (R, BuildConfig, Hilt/Dagger, Room, Compose
singletons, `*Previews*`). A module can exclude more classes through the extra property
`arc.coverage.excludes`, a list of Ant patterns set in its build file with a comment saying why:

```kotlin
// Remote Config and Analytics have no local emulator; Auth/Firestore adapters stay covered
// by emulator tests.
extra["arc.coverage.excludes"] = listOf(
    "**/RemoteConfigFeatureFlagRepository*",
    "**/FirebaseAnalyticsTracker*",
)
```

Reserve it for SDK glue no local test can exercise. It is not a way to reach the minimum.

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

`arc-setup --variant <Name>` (default `Debug`) writes the variant into the variant-specific
targets: `make test` (`test<Name>UnitTest`), `make android-lint` (`lint<Name>`),
`make coverage` (`jacoco<Name>UnitTestCoverageVerification`), `make screenshot`
(`validate<Name>ScreenshotTest`) and `make screenshot-update` (`update<Name>ScreenshotTest`).
`make setup` re-runs arc-setup with the same variant.

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
