# ARCDevTools-Android Documentation

Centralized quality automation and development tooling for ARC Labs Studio Android projects.

## Quick Links

- [Getting Started](getting-started.md) - Installation and setup walkthrough
- [Integration Guide](integration.md) - How to integrate with build.gradle.kts and libs.versions.toml
- [Configuration](configuration.md) - Customize detekt and ktlint rules for your project
- [CI/CD Guide](ci-cd.md) - Set up GitHub Actions workflows
- [Troubleshooting](troubleshooting.md) - Common issues and solutions
- [Migration Guide](MIGRATION_V1_TO_V2.md) - Future upgrade guide (placeholder)

## Overview

ARCDevTools-Android provides standardized development tooling for ARC Labs Android projects, including:

- **ktlint Configuration** - Code style enforcement via .editorconfig with Compose rules
- **detekt Configuration** - Static analysis with all rule categories and Compose-specific checks
- **Git Hooks** - Pre-commit and pre-push hooks for automated quality checks
- **GitHub Actions Workflows** - CI/CD templates for quality, testing, documentation, and releases
- **Project Setup** - One-command installation script
- **Makefile Generation** - Convenient commands for common tasks

## Installation

```bash
# Add as submodule
git submodule add https://github.com/arclabs-studio/ARCDevTools-Android
git submodule update --init --recursive

# Run setup
./ARCDevTools-Android/arc-setup
```

See [Getting Started](getting-started.md) for detailed installation instructions.

## Usage

After installation, use the generated Makefile:

```bash
make help      # Show all available commands
make lint      # Run detekt and ktlint
make format    # Check formatting (dry-run)
make fix       # Apply ktlint format
make test      # Run unit tests
make setup     # Re-run ARCDevTools setup
make hooks     # Re-install git hooks
make clean     # Clean build artifacts
```

## Documentation Structure

- **[getting-started.md](getting-started.md)** - Installation and initial setup
- **[integration.md](integration.md)** - Detailed integration guide for build.gradle.kts
- **[configuration.md](configuration.md)** - How to customize detekt, ktlint, and git hooks
- **[ci-cd.md](ci-cd.md)** - GitHub Actions workflow templates and setup
- **[troubleshooting.md](troubleshooting.md)** - Solutions to common problems
- **[MIGRATION_V1_TO_V2.md](MIGRATION_V1_TO_V2.md)** - Future upgrade guide

## Related Resources

- **[ARCKnowledge-Android](https://github.com/arclabs-studio/ARCKnowledge-Android)** - Complete development standards and guidelines (included as submodule)
- **[ktlint](https://pinterest.github.io/ktlint/)** - Kotlin linter and formatter
- **[detekt](https://detekt.dev/)** - Static code analysis for Kotlin

## Support

- Check [Troubleshooting](troubleshooting.md) for common issues
- Review [ARCKnowledge-Android](https://github.com/arclabs-studio/ARCKnowledge-Android) for development standards
- Open an issue on [GitHub](https://github.com/arclabs-studio/ARCDevTools-Android/issues)

---

**ARC Labs Studio** - Made with care
