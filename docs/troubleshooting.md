# Troubleshooting

Common issues and solutions when using ARCDevTools-Android.

---

## Table of Contents

- [Setup Issues](#setup-issues)
- [ktlint Issues](#ktlint-issues)
- [detekt Issues](#detekt-issues)
- [Gradle Scaffolding Issues](#gradle-scaffolding-issues)
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

### "no-unused-imports rule not running"

**Cause:** Since ktlint 1.7.0, the `no-unused-imports` rule is disabled by default due to recurring issues.

**Solution:** ARCDevTools-Android explicitly enables this rule in `.editorconfig`. If you see unused imports not being flagged, verify your `.editorconfig` contains:

```editorconfig
[*.{kt,kts}]
ktlint_standard_no-unused-imports = enabled
```

### "context-receiver-wrapping errors"

**Cause:** ktlint 1.7.0+ added context receiver/parameter wrapping rules that may flag code using Kotlin context parameters.

**Solution:** Ensure you're using ktlint 1.7.1+ which includes compatibility fixes. If issues persist, you can disable the rule:

```editorconfig
[*.{kt,kts}]
ktlint_standard_context-receiver-wrapping = disabled
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
detekt = "1.23.8"
composeRulesDetekt = "0.4.28"  # Last compose-rules line built for detekt 1.23.x
```

### detekt-compose rules not working

**Cause:** The detekt-compose plugin dependency is missing.

**Solution:** Apply `arc.android.compose` (or `arc.android.feature`), which adds the
`io.nlopez.compose.rules:detekt` rule set and `config/detekt/detekt-compose.yml`. The rule set id
in the config is `Compose:` (capital C).

### detekt fails with only a Java version as the message (e.g. "25.0.3")

**Cause:** detekt 1.23.8 cannot run on JDK 25, the JBR bundled with current Android Studio.

**Solution:** Keep `gradle/gradle-daemon-jvm.properties` (installed by `arc-setup --with-gradle`),
which runs the Gradle daemon on JDK 21 regardless of `JAVA_HOME`.

### "Deprecated Gradle features were used in this build" with detekt applied

**Cause:** detekt 1.23.8 calls `ReportingExtension.file(String)` and `Configuration.setVisible`,
both deprecated in Gradle 9 (visible with `--warning-mode all`). AGP 9.4.0 also calls
`Configuration.setVisible` on Gradle 9.8.2. Neither can be fixed from a template.

**Decision (2026-10-21):** accepted. detekt 1.23.8 is the current stable release and ARC does not
build on pre-release tooling (detekt 2.0 is still alpha). The notice is a Gradle-level deprecation,
not a compiler, Lint, detekt or ktlint warning, so the zero-warnings gate is unaffected. Revisit
when detekt 2.0 ships stable (compose-rules 0.5+ requires it).

---

## Gradle Scaffolding Issues

### "Dependency ... requires libraries and applications that depend on it to compile against version 37"

**Cause:** The 2026 AndroidX/Compose versions in the catalog declare `minCompileSdk` 37.

**Solution:** Keep `compileSdk = "37"` in `gradle/libs.versions.toml` and install the SDK
platform: `android sdk install platforms/android-37.0`.

### `validateDebugScreenshotTest` fails: "the test task did not discover any tests"

**Cause:** A module applies `arc.android.compose` but has no `@PreviewTest` in
`src/screenshotTest/kotlin`. Gradle 9 fails test tasks that find no tests.

**Solution:** Add a screenshot test for the module's previews and record references with
`./gradlew updateDebugScreenshotTest`.

### Coverage verification fails on a module with previews

**Cause:** `@Preview` functions are never run by unit tests.

**Solution:** Move previews to a `<Screen>Previews.kt` file; `*PreviewsKt` classes are excluded
from coverage (they are covered by screenshot tests).

### "WARNING: A restricted method in java.lang.System has been called"

**Cause:** The Gradle launcher runs on JDK 24+ (for example Android Studio's JBR 25), which warns
about Gradle's native library loading.

**Solution:** `export GRADLE_OPTS="--enable-native-access=ALL-UNNAMED"` in your shell profile,
or point `JAVA_HOME` at JDK 21.

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
