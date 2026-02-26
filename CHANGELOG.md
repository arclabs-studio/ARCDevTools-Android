# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

---

## [1.0.0] - 2026-02-25

### Initial Release

ARCDevTools-Android v1.0.0 is the first production-ready release, providing **centralized quality tooling and standards for ARC Labs Studio Android projects**.

ARCDevTools-Android is a **configuration repository** integrated as a **Git submodule**, offering standardized ktlint and detekt configurations, git hooks, GitHub Actions workflow templates, and automation scripts.

### Features

#### Core Functionality

- **arc-setup Script** - Bash script for one-command project setup
  - Copies .editorconfig and detekt configurations
  - Installs git hooks (pre-commit, pre-push)
  - Generates Makefile with convenient commands
  - Optionally copies GitHub Actions workflow templates

#### Configuration Files

- **EditorConfig** (`configs/.editorconfig`)
  - ktlint configuration with 120-character line width
  - IntelliJ IDEA code style
  - Compose-specific rules enabled

- **Detekt Configuration** (`configs/detekt.yml`)
  - Full rule set covering all detekt categories
  - Aligned with ARCKnowledge-Android standards

- **Detekt Compose Rules** (`configs/detekt-compose.yml`)
  - Jetpack Compose-specific static analysis rules

#### Git Hooks

- **Pre-commit Hook** (`hooks/pre-commit`)
  - Automatically formats Kotlin files with ktlint
  - Runs detekt on staged files
  - Blocks commit if issues found

- **Pre-push Hook** (`hooks/pre-push`)
  - Runs unit tests before pushing
  - Prevents broken code from reaching remote

- **Hook Installation Script** (`hooks/install-hooks.sh`)

#### GitHub Actions Workflows

- **quality.yml** - Code quality checks (ktlint, detekt, Markdown lint)
- **tests.yml** - Automated unit testing with Java 17
- **docs.yml** - Dokka documentation generation and deployment
- **enforce-gitflow.yml** - Git Flow branch validation
- **sync-develop.yml** - Auto-sync main to develop
- **validate-release.yml** - Release validation and creation
- **release-drafter.yml** - Auto-draft release notes from PRs

#### Utility Scripts

- **lint.sh** - Run detekt and ktlint
- **format.sh** - Run ktlint format
- **setup-github-labels.sh** - Configure GitHub labels
- **setup-branch-protection.sh** - Configure branch protection rules

#### GitHub Templates

- **PULL_REQUEST_TEMPLATE.md** - PR template with comprehensive checklist
- **Bug Report Template** - Structured bug reporting
- **Feature Request Template** - Feature proposal format
- **Improvement Template** - Enhancement proposal format

#### Documentation

- Complete Markdown documentation in `docs/` directory:
  - `getting-started.md` - Installation and setup walkthrough
  - `integration.md` - build.gradle.kts and libs.versions.toml setup
  - `configuration.md` - Customizing detekt and ktlint
  - `ci-cd.md` - GitHub Actions guide
  - `troubleshooting.md` - Common issues and solutions

- **README.md** - Comprehensive project overview and usage guide
- **CONTRIBUTING.md** - Contribution guidelines with Git Flow workflow
- **CHANGELOG.md** - This file

#### Standards Compliance

- **ARCKnowledge-Android Integration** - Development standards included as submodule
- **All code and documentation in English**
- **100% aligned with ARCKnowledge-Android standards**

### Architecture

ARCDevTools-Android follows a **clean directory structure**:

```
ARCDevTools-Android/
├── arc-setup                       # Bash setup script
├── configs/                        # ktlint and detekt configs
├── hooks/                          # Git hooks
├── scripts/                        # Utility scripts
├── workflows/                      # GitHub Actions templates
├── templates/                      # GitHub templates
├── docs/                           # Markdown documentation
├── ARCKnowledge-Android/           # Development standards (submodule)
├── README.md
├── CHANGELOG.md
├── CONTRIBUTING.md
└── LICENSE
```

### Installation

Projects integrate ARCDevTools-Android as a **Git submodule**:

```bash
# Add submodule
git submodule add https://github.com/arclabs-studio/ARCDevTools-Android
git submodule update --init --recursive

# Run setup
./ARCDevTools-Android/arc-setup

# Commit integration
git add .gitmodules ARCDevTools-Android/ .editorconfig config/ Makefile
git commit -m "chore: integrate ARCDevTools-Android v1.0"
```

---

## Links

- **Repository**: https://github.com/arclabs-studio/ARCDevTools-Android
- **ARCKnowledge-Android**: https://github.com/arclabs-studio/ARCKnowledge-Android
- **Issues**: https://github.com/arclabs-studio/ARCDevTools-Android/issues

---

[Unreleased]: https://github.com/arclabs-studio/ARCDevTools-Android/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/arclabs-studio/ARCDevTools-Android/releases/tag/v1.0.0
