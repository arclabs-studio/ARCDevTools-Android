# ARCDevTools-Android

[![Quality](https://github.com/arclabs-studio/ARCDevTools-Android/actions/workflows/quality.yml/badge.svg)](https://github.com/arclabs-studio/ARCDevTools-Android/actions/workflows/quality.yml)
[![Tests](https://github.com/arclabs-studio/ARCDevTools-Android/actions/workflows/tests.yml/badge.svg)](https://github.com/arclabs-studio/ARCDevTools-Android/actions/workflows/tests.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org)

Centralized quality tooling, configuration, and automation for **ARC Labs Studio** Android projects.

---

## Overview

ARCDevTools-Android is the Android counterpart to [ARCDevTools](https://github.com/arclabs-studio/ARCDevTools) (iOS). It provides standardized development tooling integrated as a **Git submodule**, offering:

| Feature | Tool | Description |
|---------|------|-------------|
| **Code Style** | ktlint | Enforced via `.editorconfig` with Compose rules |
| **Static Analysis** | detekt | Full rule set + Compose-specific checks |
| **Git Hooks** | Bash | Pre-commit (format + lint) and pre-push (tests) |
| **Claude Code Hooks** | Bash | Auto-format, commit validation, git safety, session context |
| **AI Skills** | Claude Code | Symlinked from ARCKnowledge-Android |
| **MCP Setup** | CLI | Automated MCP server registration (Context7, Android Skills) |
| **CI/CD** | GitHub Actions | 7 workflow templates for quality, tests, docs, releases |
| **Setup** | `arc-setup` | One-command project configuration |
| **Makefile** | Make | Convenient commands for common tasks |

---

## Quick Start

```bash
# 1. Add as submodule to your Android project
git submodule add https://github.com/arclabs-studio/ARCDevTools-Android
git submodule update --init --recursive

# 2. Run setup
./ARCDevTools-Android/arc-setup

# 3. Verify
make lint
make test

# 4. Commit integration
git add .gitmodules ARCDevTools-Android/ .editorconfig config/ Makefile
git commit -m "chore: integrate ARCDevTools-Android v1.0"
```

---

## Features

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
│   ├── quality.yml                     # Code quality checks
│   ├── tests.yml                       # Unit tests
│   ├── docs.yml                        # Documentation generation
│   ├── enforce-gitflow.yml             # Git Flow validation
│   ├── sync-develop.yml                # Branch sync
│   ├── validate-release.yml            # Release validation
│   └── release-drafter.yml             # Release notes drafting
├── templates/
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
│   └── MIGRATION_V1_TO_V2.md          # Future migration guide
├── ARCKnowledge-Android/               # Development standards (submodule)
├── CHANGELOG.md
├── CONTRIBUTING.md
├── LICENSE
└── README.md
```

---

## Requirements

- **Kotlin** 2.0.21+
- **Android Gradle Plugin** 8.0+
- **Java** 17+
- **Git** 2.30+

---

## Documentation

- [Getting Started](docs/getting-started.md) - Installation and setup
- [Integration Guide](docs/integration.md) - build.gradle.kts and libs.versions.toml
- [Configuration](docs/configuration.md) - Customizing detekt and ktlint
- [CI/CD Guide](docs/ci-cd.md) - GitHub Actions setup
- [Troubleshooting](docs/troubleshooting.md) - Common issues and solutions
- [Contributing](CONTRIBUTING.md) - How to contribute
- [Changelog](CHANGELOG.md) - Version history

---

## Updating

```bash
cd ARCDevTools-Android
git pull origin main
cd ..
./ARCDevTools-Android/arc-setup
git add ARCDevTools-Android
git commit -m "chore: update ARCDevTools-Android"
```

---

## Related Projects

- [ARCDevTools](https://github.com/arclabs-studio/ARCDevTools) - iOS equivalent
- [ARCKnowledge-Android](https://github.com/arclabs-studio/ARCKnowledge-Android) - Android development standards

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**ARC Labs Studio** - Made with care
