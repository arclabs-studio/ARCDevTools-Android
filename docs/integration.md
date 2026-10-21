# Integration Guide

This guide covers the Gradle scaffolding ARCDevTools-Android installs (since v0.2.0) and how
modules consume it.

## Table of Contents

- [What arc-setup installs](#what-arc-setup-installs)
- [Toolchain](#toolchain)
- [Version Catalog](#version-catalog)
- [Convention Plugins](#convention-plugins)
- [Module build files](#module-build-files)
- [Coverage](#coverage)
- [Screenshot Tests](#screenshot-tests)
- [Detekt Configuration](#detekt-configuration)
- [ktlint Configuration](#ktlint-configuration)

---

## What arc-setup installs

```bash
./ARCDevTools-Android/arc-setup --with-gradle    # or answer "y" to the Gradle prompt
```

| Source in ARCDevTools-Android | Installed as | Overwritten on re-run |
|-------------------------------|--------------|------------------------|
| `templates/gradle/libs.versions.toml` | `gradle/libs.versions.toml` | Only with `--force-gradle` |
| `templates/root/gradle-daemon-jvm.properties` | `gradle/gradle-daemon-jvm.properties` | Only with `--force-gradle` |
| `templates/build-logic/` | `build-logic/` | Always (ARC-managed; extra files are kept) |
| `templates/root/settings.gradle.kts` | `settings.gradle.kts` | Only with `--force-gradle` |
| `templates/root/build.gradle.kts` | `build.gradle.kts` | Only with `--force-gradle` |
| `templates/root/gradle.properties` | `gradle.properties` | Only with `--force-gradle` |
| `templates/root/gitignore` | `.gitignore` | Only with `--force-gradle` |
| — | `gradle/wrapper/gradle-wrapper.properties` | Always: Gradle 9.8.2 + checksum |

After the first run, refresh the wrapper JAR once: `./gradlew wrapper`.

If your project already had a `settings.gradle.kts`, add `includeBuild("build-logic")` to its
`pluginManagement {}` block (arc-setup warns when it is missing).

---

## Toolchain

| Tool | Version |
|------|---------|
| Gradle | 9.8.2 |
| Android Gradle Plugin | 9.4.0 (built-in Kotlin, new DSL) |
| Kotlin | 2.4.21 |
| KSP | 2.3.12 |
| JDK running Gradle | 21 (pinned by `gradle/gradle-daemon-jvm.properties`) |
| Bytecode / `jvmTarget` | 17 |
| compileSdk / targetSdk / minSdk | 37 / 36 / 26 |

- **JDK 21 for the daemon.** detekt 1.23.8 cannot run on JDK 25 (the JBR bundled with current
  Android Studio). The daemon JVM criteria file makes Gradle — and Android Studio — use JDK 21;
  the foojay resolver plugin in `settings.gradle.kts` downloads it when it is not installed.
- **compileSdk 37.** Compose BOM 2026.09.00, material3 1.5.0-beta01, Navigation 3 1.2.0,
  Lifecycle 2.11.0 and core 1.19.1 require it (`minCompileSdk` in their AAR metadata).
  targetSdk stays at 36.
- **AGP 9 built-in Kotlin.** Never apply `org.jetbrains.kotlin.android` or `kapt`; use KSP.

---

## Version Catalog

`gradle/libs.versions.toml` is the single place for versions. Module build files reference
`libs.*` aliases only — never literal coordinates. It contains the ARC Android stack:
Compose BOM + Material 3 Expressive + adaptive, Navigation 3, Hilt, Room 3, DataStore,
kotlinx.serialization, coroutines, Coil 3, Firebase BoM, Credential Manager + Google ID,
RevenueCat, maps-compose, JUnit 5, Turbine, Robolectric, Compose UI test, Compose Preview
Screenshot Testing, detekt and ktlint.

SDK levels also live in the catalog (`compileSdk`, `targetSdk`, `minSdk`) and are read by the
convention plugins.

---

## Convention Plugins

| Plugin id | Apply to | What it configures |
|-----------|----------|--------------------|
| `arc.android.application` | `app` | `com.android.application`, SDK levels, Java/Kotlin 17, Kotlin `allWarningsAsErrors`, `arc.android.lint`, JUnit 5, JaCoCo gate, detekt + ktlint, core-ktx |
| `arc.android.library` | `core:*` | Same baseline for `com.android.library` |
| `arc.android.feature` | `feature:*` | `arc.android.library` + `arc.android.compose` + `arc.android.hilt` + kotlinx.serialization, Navigation 3 runtime, lifecycle ViewModel (Compose + Navigation 3) |
| `arc.android.compose` | Any Compose module (after app/library) | Compose compiler, Compose BOM, Material 3, activity-compose, lifecycle-runtime-compose, Robolectric + Compose UI tests, screenshot testing, detekt Compose rules |
| `arc.android.hilt` | Modules with DI (after app/library) | KSP + Hilt plugin, `hilt-android`, `ksp(hilt-compiler)` |
| `arc.android.room` | `core:database` (after library) | KSP + Room 3 plugin, schemas exported to `<module>/schemas`, `room3-runtime`, `ksp(room3-compiler)`, `room3-testing` |
| `arc.jvm.library` | `domain` (pure Kotlin) | `org.jetbrains.kotlin.jvm`, Java/Kotlin 17, `allWarningsAsErrors`, standalone Android Lint, JUnit 5, JaCoCo gate, detekt + ktlint |
| `arc.android.lint` | Applied by the others | Lint `warningsAsErrors`, `abortOnError`, `checkReleaseBuilds` |

Android Lint disables only the version-freshness checks (`AndroidGradlePluginVersion`,
`GradleDependency`, `NewerVersionAvailable`, `OldTargetApi`): versions and SDK levels are pinned
in the catalog and bumped deliberately, and these checks would otherwise fail an unchanged build
whenever something is released upstream.

---

## Module build files

```kotlin
// app/build.gradle.kts
plugins {
    alias(libs.plugins.arc.android.application)
    alias(libs.plugins.arc.android.compose)
    alias(libs.plugins.arc.android.hilt)
}

android {
    namespace = "com.arclabs.example"
    defaultConfig {
        applicationId = "com.arclabs.example"
        versionCode = 1
        versionName = "1.0"
    }
}
```

```kotlin
// feature/library/build.gradle.kts
plugins {
    alias(libs.plugins.arc.android.feature)
}

android {
    namespace = "com.arclabs.example.feature.library"
}
```

```kotlin
// domain/build.gradle.kts
plugins {
    alias(libs.plugins.arc.jvm.library)
}
```

---

## Coverage

Android modules enable AGP's native unit-test coverage for the `debug` build type and get:

- `jacocoDebugUnitTestReport` — XML + HTML report.
- `jacocoDebugUnitTestCoverageVerification` — fails below **80% line coverage**; `check` (and
  therefore `build`) depends on it.

JVM modules use Gradle's `jacocoTestReport` / `jacocoTestCoverageVerification` with the same
threshold. Generated code (R, BuildConfig, Hilt/Dagger, Room `_Impl`, Compose singletons) is
excluded, as are `@Preview` functions **kept in `*Previews.kt` files** — previews are covered by
the screenshot tests, so keep them out of the screen's own file.

---

## Screenshot Tests

`arc.android.compose` applies the Compose Preview Screenshot Testing plugin
(`com.android.compose.screenshot`, the setup documented for AGP < 9.5.0-alpha03). Put
`@PreviewTest` previews in `src/screenshotTest/kotlin`, then:

```bash
./gradlew updateDebugScreenshotTest     # record reference images (commit them)
./gradlew validateDebugScreenshotTest   # compare against the references
```

Every module that applies `arc.android.compose` needs at least one `@PreviewTest`: Gradle 9
fails `validateDebugScreenshotTest` when a test task discovers no tests.

---

## Detekt Configuration

The `arc-setup` script copies detekt configuration files to `config/detekt/`:

```
project-root/
├── config/
│   └── detekt/
│       ├── detekt.yml            # Main detekt rules
│       └── detekt-compose.yml    # Compose-specific rules
```

The convention plugins wire them up: every module uses `detekt.yml`; modules with
`arc.android.compose` also load `detekt-compose.yml` and the `io.nlopez.compose.rules:detekt`
rule set (0.4.28, the last line built for detekt 1.23.x).

```bash
make lint          # detekt + ktlint
./gradlew detekt
```

---

## ktlint Configuration

ktlint 1.8.0 runs through the `org.jlleitschuh.gradle.ktlint` Gradle plugin (14.2.0) and is
configured via `.editorconfig` at the project root.

### Key Settings

- **Code style:** `intellij_idea`
- **Max line length:** 120 characters
- **Trailing commas:** Enabled
- **`@Composable` functions:** exempt from the lowercase function-naming rule

```bash
make format        # check (dry-run)
make fix           # auto-fix
./gradlew ktlintCheck
./gradlew ktlintFormat
```

---

## See Also

- [Getting Started](getting-started.md) - Installation walkthrough
- [Configuration](configuration.md) - Customizing rules
- [CI/CD Guide](ci-cd.md) - GitHub Actions setup
