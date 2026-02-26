# Troubleshooting

Common issues and solutions when using ARCDevTools-Android.

---

## Table of Contents

- [Setup Issues](#setup-issues)
- [ktlint Issues](#ktlint-issues)
- [detekt Issues](#detekt-issues)
- [Git Hook Issues](#git-hook-issues)
- [CI/CD Issues](#cicd-issues)

---

## Setup Issues

### "No gradlew or build.gradle.kts detected"

**Cause:** The `arc-setup` script cannot find your Android project files.

**Solution:** Run `arc-setup` from your Android project root directory (where `gradlew` and `build.gradle.kts` are located):

```bash
cd /path/to/your/android/project
./ARCDevTools-Android/arc-setup
```

### "Permission denied" running arc-setup

**Cause:** The script doesn't have execute permission.

**Solution:**
```bash
chmod +x ARCDevTools-Android/arc-setup
./ARCDevTools-Android/arc-setup
```

### Submodule not initialized

**Cause:** Git submodules weren't initialized after cloning.

**Solution:**
```bash
git submodule update --init --recursive
```

---

## ktlint Issues

### "Wildcard imports detected"

**Cause:** ktlint enforces explicit imports by default.

**Solution:** Replace wildcard imports with explicit ones, or disable the rule in `.editorconfig`:

```editorconfig
[*.{kt,kts}]
ktlint_standard_no-wildcard-imports = disabled
```

### "ktlintFormat changes files but ktlintCheck still fails"

**Cause:** Some ktlint violations cannot be auto-fixed (e.g., naming conventions).

**Solution:** Fix the remaining issues manually. Run `./gradlew ktlintCheck` to see which violations remain.

### "Compose function naming error"

**Cause:** ktlint Compose rules require `@Composable` functions returning `Unit` to be PascalCase.

**Solution:** Ensure your Composable functions follow the naming convention:

```kotlin
// Correct
@Composable
fun MyScreen() { }

// Incorrect
@Composable
fun myScreen() { }
```

---

## detekt Issues

### "Too many issues found"

**Cause:** The `maxIssues: 0` setting in detekt.yml means any issue fails the build.

**Solution:** Either fix the issues or create a baseline:

```bash
./gradlew detektBaseline
```

Then reference the baseline in your `build.gradle.kts`:

```kotlin
detekt {
    baseline = file("config/detekt/baseline.xml")
}
```

### "Unknown rule" or "Invalid configuration"

**Cause:** Using a detekt version that doesn't support all rules in the config.

**Solution:** Ensure your detekt version matches or is newer than the one ARCDevTools-Android was built for. Check `gradle/libs.versions.toml`:

```toml
[versions]
detekt = "1.23.7"  # Minimum required version
```

### detekt-compose rules not working

**Cause:** The detekt-compose plugin dependency is missing.

**Solution:** Add it to your module's `build.gradle.kts`:

```kotlin
dependencies {
    detektPlugins("io.nlopez.compose.rules:detekt:0.4.22")
}
```

---

## Git Hook Issues

### "Pre-commit hook takes too long"

**Cause:** Running full Gradle tasks on every commit can be slow.

**Solution:** The hook uses `--daemon` for faster execution. If still too slow:

1. Skip hooks temporarily: `SKIP_HOOKS=1 git commit -m "message"`
2. Consider removing the detekt step from pre-commit (keep it in CI only)

### "Hook not running after setup"

**Cause:** Hooks may not have been installed correctly.

**Solution:**
```bash
# Reinstall hooks
./ARCDevTools-Android/hooks/install-hooks.sh

# Verify hooks are executable
ls -la .git/hooks/pre-commit
ls -la .git/hooks/pre-push
```

### "Pre-push tests fail but they pass manually"

**Cause:** The pre-push hook runs `testDebugUnitTest`, which may differ from your manual test command.

**Solution:** Ensure you're running the same test target:

```bash
./gradlew testDebugUnitTest
```

---

## CI/CD Issues

### "Gradle wrapper not found" in GitHub Actions

**Cause:** `gradlew` is not in the repository or not executable.

**Solution:** Ensure `gradlew` is committed to your repository:

```bash
git add gradlew gradle/wrapper/
git commit -m "chore: add Gradle wrapper"
```

And add a step to make it executable:

```yaml
- name: Make gradlew executable
  run: chmod +x ./gradlew
```

### "Out of disk space" in CI

**Cause:** Large Gradle caches or build outputs.

**Solution:** Add cleanup steps:

```yaml
- name: Clean up Gradle cache
  run: |
    rm -rf ~/.gradle/caches/build-cache-*
    rm -rf ~/.gradle/caches/transforms-*
```

---

## Getting Help

If your issue isn't listed here:

1. Check the [GitHub Issues](https://github.com/arclabs-studio/ARCDevTools-Android/issues)
2. Review [ARCKnowledge-Android](https://github.com/arclabs-studio/ARCKnowledge-Android) standards
3. Open a new issue with:
   - Your environment (OS, Kotlin version, AGP version)
   - Steps to reproduce
   - Expected vs actual behavior
   - Relevant error logs

---

**ARC Labs Studio** - Made with care
