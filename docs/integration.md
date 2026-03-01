# Integration Guide

This guide covers how to integrate ARCDevTools-Android tooling into your Android project's build system.

## Table of Contents

- [Version Catalog Setup](#version-catalog-setup)
- [Root build.gradle.kts](#root-buildgradlekts)
- [Module build.gradle.kts](#module-buildgradlekts)
- [Detekt Configuration](#detekt-configuration)
- [ktlint Configuration](#ktlint-configuration)

---

## Version Catalog Setup

Add the following to your `gradle/libs.versions.toml`:

```toml
[versions]
detekt = "1.23.8"
ktlint = "14.0.1"

[plugins]
detekt = { id = "io.gitlab.arturbosch.detekt", version.ref = "detekt" }
ktlint = { id = "org.jlleitschuh.gradle.ktlint", version.ref = "ktlint" }
```

---

## Root build.gradle.kts

Apply the plugins at the project level:

```kotlin
// build.gradle.kts (project root)
plugins {
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
}

// Apply to all subprojects
subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    // detekt configuration
    detekt {
        config.setFrom("${rootProject.projectDir}/config/detekt/detekt.yml")
        buildUponDefaultConfig = true
        parallel = true
    }

    // ktlint configuration
    ktlint {
        version.set("1.7.1")
        android.set(true)
        outputToConsole.set(true)
        ignoreFailures.set(false)
    }
}
```

---

## Module build.gradle.kts

For module-specific configuration (optional):

```kotlin
// app/build.gradle.kts
plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
}

detekt {
    config.setFrom("${rootProject.projectDir}/config/detekt/detekt.yml")
    buildUponDefaultConfig = true
    parallel = true
}

// Add detekt-compose plugin for Compose modules
dependencies {
    detektPlugins("io.nlopez.compose.rules:detekt:0.4.22")
}

// Note: compose-rules 0.5.x targets detekt 2.x and is NOT compatible with detekt 1.23.x.
// Stay on 0.4.22 until you migrate to detekt 2.x.
```

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

### Using Compose Rules

To use the Compose-specific rules, add the detekt-compose plugin dependency:

```kotlin
dependencies {
    detektPlugins("io.nlopez.compose.rules:detekt:0.4.22")
}
// Note: compose-rules 0.5.x targets detekt 2.x - stay on 0.4.22 for detekt 1.23.x
```

And reference both config files:

```kotlin
detekt {
    config.setFrom(
        "${rootProject.projectDir}/config/detekt/detekt.yml",
        "${rootProject.projectDir}/config/detekt/detekt-compose.yml"
    )
}
```

### Running Detekt

```bash
# Via Makefile
make lint

# Via Gradle directly
./gradlew detekt
```

---

## ktlint Configuration

ktlint is configured via `.editorconfig` at the project root. The `arc-setup` script copies this file automatically.

### Key Settings

- **Code style:** `intellij_idea`
- **Max line length:** 120 characters
- **Trailing commas:** Enabled
- **Compose rules:** Enabled (requires compose ruleset)

### Running ktlint

```bash
# Check (dry-run)
make format

# Auto-fix
make fix

# Via Gradle directly
./gradlew ktlintCheck
./gradlew ktlintFormat
```

---

## Complete Example

Here is a minimal `build.gradle.kts` integrating all ARCDevTools-Android tooling:

```kotlin
// settings.gradle.kts
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolution {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "YourProject"
include(":app")
```

```kotlin
// build.gradle.kts (root)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    detekt {
        config.setFrom("${rootProject.projectDir}/config/detekt/detekt.yml")
        buildUponDefaultConfig = true
        parallel = true
    }

    ktlint {
        version.set("1.7.1")
        android.set(true)
        outputToConsole.set(true)
    }
}
```

---

## See Also

- [Getting Started](getting-started.md) - Installation walkthrough
- [Configuration](configuration.md) - Customizing rules
- [CI/CD Guide](ci-cd.md) - GitHub Actions setup
