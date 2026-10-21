# Getting Started with ARCDevTools-Android

Set up quality automation and the Gradle build scaffolding for your ARC Labs Android project in minutes.

## Overview

ARCDevTools-Android provides a streamlined setup process that configures ktlint, detekt, git hooks, automation scripts and (since v0.2.0) the Gradle scaffolding — version catalog, `build-logic` convention plugins and root build files — for your Android project. This guide walks you through the initial setup and basic usage.

## Requirements

Before you begin, ensure you have:

- **Gradle 9.8.2**, **Android Gradle Plugin 9.4.0**, **Kotlin 2.4.21** (installed by the Gradle scaffolding)
- **JDK 21** to run Gradle (pinned by `gradle/gradle-daemon-jvm.properties`; Gradle downloads it if missing)
- **Android SDK** with platform `android-37` (compileSdk) and build-tools 36+
- **Android Studio** (optional; `./gradlew` and the `android` CLI are enough)
- **Git 2.30+** for submodule support

## Installation Steps

### Step 1: Add ARCDevTools-Android as Submodule

Navigate to your project root and add ARCDevTools-Android as a Git submodule:

```bash
cd /path/to/your/project
git submodule add https://github.com/arclabs-studio/ARCDevTools-Android
git submodule update --init --recursive
```

This creates an `ARCDevTools-Android/` directory in your project with all configuration files and scripts.

**For existing clones:**

If someone clones your project, they need to initialize the submodules:

```bash
git clone --recurse-submodules https://github.com/your-org/your-project
# Or if already cloned:
git submodule update --init --recursive
```

### Step 2: Run Setup

Navigate to your project root directory and run the setup script:

```bash
cd /path/to/your/project
./ARCDevTools-Android/arc-setup
```

The setup tool will:

1. Copy `.editorconfig` configuration to your project root
2. Copy `detekt.yml` and `detekt-compose.yml` to `config/detekt/`
3. Install pre-commit and pre-push git hooks
4. Optionally install the Gradle scaffolding (`--with-gradle`): `gradle/libs.versions.toml`,
   `build-logic/` convention plugins, root `settings.gradle.kts` / `build.gradle.kts` /
   `gradle.properties` / `.gitignore`, and the Gradle 9.8.2 wrapper version
5. Generate a `Makefile` with useful commands
6. Optionally copy GitHub Actions workflows

### Step 3: Apply the Convention Plugins

Replace each module's hand-written `android {}` configuration with the `arc.*` plugins
(`arc.android.application`, `arc.android.library`, `arc.android.feature`, `arc.android.compose`,
`arc.android.hilt`, `arc.android.room`, `arc.jvm.library`). See the
[Integration Guide](integration.md) for examples.

## Claude Code Setup

The setup script automatically installs Claude Code integration:

- **`claude-hooks/`** — Auto-format on save, commit validation, git safety checks
- **`.claude/settings.json`** — Hook registrations for Claude Code sessions
- **`.claude/skills/`** — Symlinked from ARCKnowledge-Android (when available)

To also configure MCP servers (Context7, Android Skills):

```bash
make mcp
```

Or run setup with the `--with-mcp` flag:

```bash
./ARCDevTools-Android/arc-setup --with-mcp
```

## Verify Installation

After setup completes, verify everything is working:

### Build

```bash
./gradlew build
```

Compiles every module with Kotlin and Android Lint warnings as errors, runs unit tests, enforces
80% line coverage, and runs detekt and ktlint.

### Test Lint

```bash
make lint
```

This should run detekt and ktlint on your codebase and report any issues.

### Test Format

```bash
make format
```

This runs ktlint in check mode (dry-run) to show what would change.

### Apply Formatting

```bash
make fix
```

This applies ktlint formatting changes to your code.

## Next Steps

Now that ARCDevTools-Android is installed, you can:

- Read [Integration Guide](integration.md) to configure build.gradle.kts
- Check [Configuration](configuration.md) to adjust rules for your project
- See [CI/CD Guide](ci-cd.md) to set up GitHub Actions workflows
- See [Troubleshooting](troubleshooting.md) if you encounter any issues

## Updating ARCDevTools-Android

To get the latest configurations and scripts:

```bash
cd ARCDevTools-Android
git pull origin main
cd ..
./ARCDevTools-Android/arc-setup  # Re-run setup to update configs
git add ARCDevTools-Android
git commit -m "chore: update ARCDevTools-Android to latest version"
```

## See Also

- [Integration Guide](integration.md) - Detailed integration instructions
- [Configuration](configuration.md) - Customization options
- [CI/CD Guide](ci-cd.md) - GitHub Actions setup
- [Troubleshooting](troubleshooting.md) - Common issues and solutions
