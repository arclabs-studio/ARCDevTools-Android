# Getting Started with ARCDevTools-Android

Set up quality automation for your ARC Labs Android project in minutes.

## Overview

ARCDevTools-Android provides a streamlined setup process that configures ktlint, detekt, git hooks, and automation scripts for your Android project. This guide walks you through the initial setup and basic usage.

## Requirements

Before you begin, ensure you have:

- **Kotlin 2.0+**
- **Android Gradle Plugin 8.0+**
- **Java 17** or later
- **Android Studio** (Ladybug or later recommended)
- **Git 2.30+** for submodule support

## Installation Steps

### Step 1: Configure Gradle Plugins

Add ktlint and detekt plugins to your project. See [Integration Guide](integration.md) for detailed `build.gradle.kts` and `libs.versions.toml` setup.

### Step 2: Add ARCDevTools-Android as Submodule

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

### Step 3: Run Setup

Navigate to your project root directory and run the setup script:

```bash
cd /path/to/your/project
./ARCDevTools-Android/arc-setup
```

The setup tool will:

1. Copy `.editorconfig` configuration to your project root
2. Copy `detekt.yml` and `detekt-compose.yml` to `config/detekt/`
3. Install pre-commit and pre-push git hooks
4. Generate a `Makefile` with useful commands
5. Optionally copy GitHub Actions workflows

## Verify Installation

After setup completes, verify everything is working:

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
