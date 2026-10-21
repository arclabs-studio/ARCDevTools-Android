# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

---

## [0.2.0] - 2026-10-21

### Added

- **Version catalog template** (`templates/gradle/libs.versions.toml`) with the ARC Android stack
  2026, every coordinate verified on Google Maven / Maven Central / Gradle Plugin Portal:
  AGP 9.4.0, Kotlin 2.4.21, KSP 2.3.12, Hilt 2.60.1, Compose BOM 2026.09.00, material3
  1.5.0-beta01, material3-adaptive 1.3.0 (+ adaptive-navigation3), Navigation 3 1.2.0,
  lifecycle-viewmodel-navigation3 2.11.0, Room 3 3.0.3, DataStore 1.2.1, kotlinx.serialization
  1.12.0, coroutines 1.11.0, Coil 3.6.3, Firebase BoM 35.0.2, Credential Manager 1.6.0 + googleid
  1.2.1, RevenueCat 10.28.1, maps-compose 9.0.1, JUnit 5.14.4 + android-junit5 2.0.2, Turbine,
  Robolectric 4.17, Compose Preview Screenshot Testing 0.0.1-alpha16, detekt 1.23.8 +
  compose-rules 0.4.28, ktlint 1.8.0 (org.jlleitschuh.gradle.ktlint 14.2.0)
- **Convention plugins** (`templates/build-logic/`): `arc.android.application`,
  `arc.android.library`, `arc.android.feature`, `arc.android.compose`, `arc.android.hilt`,
  `arc.android.room`, `arc.jvm.library`, `arc.android.lint` — AGP 9 built-in Kotlin and new DSL,
  compileSdk 37 / targetSdk 36 / minSdk 26, Java/Kotlin 17, Kotlin `allWarningsAsErrors`, Android
  Lint `warningsAsErrors` + `abortOnError`, KSP only, JUnit 5 platform, JaCoCo (AGP native
  coverage) with an 80% line-coverage gate on `check`, detekt + ktlint on every module
- **Root templates** (`templates/root/`): `settings.gradle.kts` (includes `build-logic`,
  repositories, foojay toolchain resolver), `build.gradle.kts`, `gradle.properties`,
  `gradle-daemon-jvm.properties` (Gradle daemon on JDK 21) and an Android `.gitignore`
- **`android-ci.yml` workflow**: `./gradlew build`, `validateDebugScreenshotTest`, detekt +
  ktlint, JDK 17/21, Gradle cache
- **arc-setup**: `--with-gradle`, `--no-gradle` and `--force-gradle` to install the Gradle
  scaffolding idempotently and set the Gradle wrapper to 9.8.2

### Changed

- `arc-setup` version aligned with the repository release (0.2.0)
- Documentation updated for the Gradle scaffolding (`integration.md` rewritten,
  `getting-started.md`, `ci-cd.md`, `troubleshooting.md`, `README.md`)

### Fixed

- `configs/detekt.yml`: removed `MultilineRawStringIndentation.trimmingMethod` (not a detekt
  1.23.x property) and renamed `ForbiddenComment.values` to `comments` (deprecated key);
  private `@Preview`/`@PreviewTest` functions no longer reported by `UnusedPrivateMember`
- `configs/detekt-compose.yml`: rule set id is `Compose` (was `compose`, rejected by detekt
  config validation)
- `configs/.editorconfig`: `@Composable` functions are exempt from ktlint's function-naming rule
- Docs: `dependencyResolution` typo in the settings example, outdated detekt / compose-rules
  versions, version references to a non-existent v1.0

---

## [0.1.0] - 2026-02-25

### Initial Release

ARCDevTools-Android v0.1.0 is the first release, providing **centralized quality tooling and standards for ARC Labs Studio Android projects**.

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
git commit -m "chore: integrate ARCDevTools-Android v0.1.0"
```

---

## Links

- **Repository**: https://github.com/arclabs-studio/ARCDevTools-Android
- **ARCKnowledge-Android**: https://github.com/arclabs-studio/ARCKnowledge-Android
- **Issues**: https://github.com/arclabs-studio/ARCDevTools-Android/issues

---

[Unreleased]: https://github.com/arclabs-studio/ARCDevTools-Android/compare/v0.2.0...HEAD
[0.2.0]: https://github.com/arclabs-studio/ARCDevTools-Android/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/arclabs-studio/ARCDevTools-Android/releases/tag/v0.1.0
