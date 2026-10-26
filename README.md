# ARCDevTools-Android

[![Quality](https://github.com/arclabs-studio/ARCDevTools-Android/actions/workflows/quality.yml/badge.svg)](https://github.com/arclabs-studio/ARCDevTools-Android/actions/workflows/quality.yml)
[![Tests](https://github.com/arclabs-studio/ARCDevTools-Android/actions/workflows/tests.yml/badge.svg)](https://github.com/arclabs-studio/ARCDevTools-Android/actions/workflows/tests.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.21-blue.svg)](https://kotlinlang.org)

Centralized quality tooling, configuration, and automation for **ARC Labs Studio** Android projects.

---

## Overview

ARCDevTools-Android is the Android counterpart to [ARCDevTools](https://github.com/arclabs-studio/ARCDevTools) (iOS). It provides standardized development tooling integrated as a **Git submodule**, offering:

| Feature | Tool | Description |
|---------|------|-------------|
| **Build Scaffolding** | Gradle | Version catalog, `build-logic` convention plugins (`arc.*`), root build files |
| **Code Style** | ktlint | Enforced via `.editorconfig` with Compose rules |
| **Static Analysis** | detekt | Full rule set + Compose-specific checks |
| **Git Hooks** | Bash | Pre-commit (format + lint) and pre-push (tests) |
| **Claude Code Hooks** | Bash | Auto-format, commit validation, git safety, session context |
| **AI Skills** | Claude Code | Symlinked from ARCKnowledge-Android |
| **MCP Setup** | CLI | Automated MCP server registration (Context7, Android Skills) |
| **CI/CD** | GitHub Actions | 8 workflow templates for build, quality, tests, docs, releases |
| **Setup** | `arc-setup` | One-command project configuration |
| **Makefile** | Make | Convenient commands for common tasks |

---

## Quick Start

```bash
# 1. Add as submodule to your Android project, pinned to a release tag
git submodule add https://github.com/arclabs-studio/ARCDevTools-Android.git
git -C ARCDevTools-Android checkout v0.5.0
git -C ARCDevTools-Android submodule update --init --recursive
git add .gitmodules ARCDevTools-Android

# 2. Run setup (configs, git + Claude Code hooks, Makefile, Gradle scaffolding)
./ARCDevTools-Android/arc-setup --with-gradle    # flavored projects: --variant DevDebug

# 3. Verify
./gradlew build
make lint
make test

# 4. Commit integration
git add .gitmodules ARCDevTools-Android/ .editorconfig config/ Makefile \
  gradle/ build-logic/ settings.gradle.kts build.gradle.kts gradle.properties \
  .gitignore claude-hooks/ .claude/settings.json
git commit -m "chore: integrate ARCDevTools-Android v0.5.0"
```

`git submodule add` checks out the default branch (`main`); the `checkout` pins a release tag,
the nested `submodule update` initialises ARCKnowledge-Android at the commit that tag pins, and
`git add` records the pin. Do not run `git submodule update --init --recursive` from the project
root before that `git add`: it resets the submodule to the commit recorded at add time.

---

## Features

### Gradle Scaffolding (`--with-gradle`)

| File | Location | Purpose |
|------|----------|---------|
| `libs.versions.toml` | `gradle/` | ARC Android stack 2026: AGP 9.4.0, Kotlin 2.4.21, Compose BOM 2026.09.00, Navigation 3, Hilt, Room 3, Firebase, RevenueCat, JUnit 5... |
| `gradle-daemon-jvm.properties` | `gradle/` | Runs the Gradle daemon on JDK 21 |
| Convention plugins | `build-logic/` | `arc.android.application`, `arc.android.library`, `arc.android.feature`, `arc.android.compose`, `arc.android.hilt`, `arc.android.room`, `arc.jvm.library`, `arc.android.lint` |
| Root build files | project root | `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`, `.gitignore` |

The convention plugins enforce compileSdk 37 / targetSdk 36 / minSdk 26, Java/Kotlin 17,
Kotlin `allWarningsAsErrors`, Android Lint `warningsAsErrors` + `abortOnError`, KSP (never
kapt), JUnit 5 on the JUnit Platform, JaCoCo line coverage >= `arc.coverage.minimum` (default
80%) on `check`, detekt and ktlint.
See the [Integration Guide](docs/integration.md).

### Configuration Files

| File | Location | Purpose |
|------|----------|---------|
| `.editorconfig` | Project root | ktlint rules (120 char, intellij_idea style, Compose rules) |
| `detekt.yml` | `config/detekt/` | Full detekt configuration with all rule categories |
| `detekt-compose.yml` | `config/detekt/` | Jetpack Compose-specific detekt rules |

### Git Hooks

| Hook | Trigger | Actions |
|------|---------|---------|
| `pre-commit` | `git commit` | ktlint format + check, detekt analysis |
| `pre-push` | `git push` | Run unit tests |

### GitHub Actions Workflows

| Workflow | Runner | Purpose |
|----------|--------|---------|
| `android-ci.yml` | ubuntu-latest | Build + Lint + unit tests + coverage, screenshot validation, detekt + ktlint |
| `quality.yml` | ubuntu-latest | ktlint + detekt + Markdown lint |
| `tests.yml` | ubuntu-latest | Unit tests with Java 17 |
| `docs.yml` | ubuntu-latest | Dokka documentation to GitHub Pages |
| `enforce-gitflow.yml` | ubuntu-latest | Branch rules validation |
| `sync-develop.yml` | ubuntu-latest | Auto-sync main to develop |
| `validate-release.yml` | ubuntu-latest | Tag validation + GitHub Release creation |
| `release-drafter.yml` | ubuntu-latest | Auto-draft release notes from PRs |

### Makefile Commands

```bash
make help      # Show all available commands
make lint      # Run detekt and ktlint
make format    # Check formatting (dry-run)
make fix       # Apply ktlint format
make test      # Run unit tests
make android-lint       # Run Android Lint (variant)
make coverage           # Run the JaCoCo line-coverage gate (variant)
make screenshot         # Validate screenshot tests (variant)
make screenshot-update  # Record screenshot references (intended visual changes only)
make setup     # Re-run ARCDevTools setup
make hooks     # Re-install git hooks
make skills    # Re-link Claude Code skills
make mcp       # Configure MCP servers
make clean     # Clean build artifacts
```

---

## Claude Code Integration

ARCDevTools-Android includes full Claude Code agentic AI tooling:

### Claude Hooks

| Hook | Trigger | Action |
|------|---------|--------|
| `format-on-save.sh` | Edit/Write `.kt`/`.kts` | Auto-format with ktlint |
| `validate-commit.sh` | `git commit` | Enforce Conventional Commits |
| `block-dangerous-git.sh` | Bash commands | Block force push, reset --hard, clean -f |
| `session-start.sh` | Session start | Load branch, changes, last release |

### MCP Servers

Configure recommended MCP servers for AI-assisted development:

```bash
make mcp
# Or during setup:
./ARCDevTools-Android/arc-setup --with-mcp
```

A template configuration is available at `configs/mcp/claude-mcp.json`.

### Skills

Skills from ARCKnowledge-Android are symlinked to `.claude/skills/` during setup, making them available via `/skill-name` in Claude Code sessions.

---

## Project Structure

```
ARCDevTools-Android/
├── arc-setup                           # Bash setup script
├── .claude/
│   └── settings.json                   # Claude Code hooks configuration
├── claude-hooks/
│   ├── session-start.sh                # Session context on startup
│   ├── validate-commit.sh              # Conventional Commits enforcement
│   ├── block-dangerous-git.sh          # Git safety guard
│   └── format-on-save.sh              # Auto-format .kt/.kts with ktlint
├── configs/
│   ├── .editorconfig                   # ktlint configuration
│   ├── detekt.yml                      # Full detekt rules
│   ├── detekt-compose.yml              # Compose-specific rules
│   └── mcp/
│       └── claude-mcp.json             # MCP servers template
├── hooks/
│   ├── install-hooks.sh                # Hook installer
│   ├── pre-commit                      # Format + lint on commit
│   └── pre-push                        # Tests before push
├── scripts/
│   ├── lint.sh                         # Lint runner
│   ├── format.sh                       # Format runner
│   ├── setup-skills.sh                 # Claude Code skills setup
│   ├── setup-mcp.sh                    # MCP servers registration
│   ├── setup-github-labels.sh          # GitHub labels setup
│   └── setup-branch-protection.sh      # Branch protection setup
├── workflows/
│   ├── android-ci.yml                  # Build, Lint, tests, screenshots
│   ├── quality.yml                     # Code quality checks
│   ├── tests.yml                       # Unit tests
│   ├── docs.yml                        # Documentation generation
│   ├── enforce-gitflow.yml             # Git Flow validation
│   ├── sync-develop.yml                # Branch sync
│   ├── validate-release.yml            # Release validation
│   └── release-drafter.yml             # Release notes drafting
├── templates/
│   ├── gradle/libs.versions.toml       # Version catalog
│   ├── build-logic/                    # arc.* convention plugins
│   ├── root/                           # settings, build, gradle.properties, .gitignore, daemon JVM
│   ├── PULL_REQUEST_TEMPLATE.md        # PR template
│   └── ISSUE_TEMPLATE/
│       ├── bug_report.md               # Bug report template
│       ├── feature_request.md          # Feature request template
│       └── improvement.md              # Improvement template
├── docs/
│   ├── README.md                       # Documentation index
│   ├── getting-started.md              # Setup walkthrough
│   ├── integration.md                  # build.gradle.kts setup
│   ├── configuration.md                # Customization guide
│   ├── ci-cd.md                        # GitHub Actions guide
│   ├── troubleshooting.md              # Common issues
│   └── MIGRATION_V1_TO_V2.md           # Future migration guide
├── ARCKnowledge-Android/               # Development standards (submodule)
├── CHANGELOG.md
├── CONTRIBUTING.md
├── LICENSE
└── README.md
```

---

## Requirements

- **Gradle** 9.8.2, **Android Gradle Plugin** 9.4.0, **Kotlin** 2.4.21 (pinned by the scaffolding)
- **JDK** 21 to run Gradle (bytecode target 17)
- **Android SDK** platform `android-37`
- **Git** 2.30+

---

## Documentation

- [Getting Started](docs/getting-started.md) - Installation and setup
- [Integration Guide](docs/integration.md) - Version catalog, convention plugins, coverage, screenshot tests
- [Configuration](docs/configuration.md) - Customizing detekt and ktlint
- [CI/CD Guide](docs/ci-cd.md) - GitHub Actions setup
- [Troubleshooting](docs/troubleshooting.md) - Common issues and solutions
- [Contributing](CONTRIBUTING.md) - How to contribute
- [Changelog](CHANGELOG.md) - Version history

---

## Updating

```bash
git -C ARCDevTools-Android fetch --tags
git -C ARCDevTools-Android checkout vX.Y.Z
git -C ARCDevTools-Android submodule update --init --recursive
./ARCDevTools-Android/arc-setup --variant <YourVariant>
git add ARCDevTools-Android
git commit -m "chore: update ARCDevTools-Android to vX.Y.Z"
```

Releases are a release PR `develop → main` plus an annotated tag on `main`; consumers pin tags
(see [Contributing](CONTRIBUTING.md#releases)).

---

## Related Projects

- [ARCDevTools](https://github.com/arclabs-studio/ARCDevTools) - iOS equivalent
- [ARCKnowledge-Android](https://github.com/arclabs-studio/ARCKnowledge-Android) - Android development standards

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**ARC Labs Studio** - Made with care
