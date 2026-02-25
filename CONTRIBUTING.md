# Contributing to ARCDevTools-Android

Thank you for your interest in contributing to ARCDevTools-Android! This document provides guidelines and workflows for contributing.

## Table of Contents

- [Git Flow Workflow](#git-flow-workflow)
- [Branch Naming](#branch-naming)
- [Conventional Commits](#conventional-commits)
- [Pull Request Process](#pull-request-process)
- [Code Quality Standards](#code-quality-standards)
- [Development Setup](#development-setup)

---

## Git Flow Workflow

We follow the **Git Flow** branching model:

```
main ─────────────────────────────────────── (production)
  │
  └── develop ────────────────────────────── (integration)
       │
       ├── feature/add-detekt-rule ───────── (feature work)
       ├── bugfix/fix-hook-path ──────────── (bug fixes)
       └── hotfix/critical-fix ───────────── (emergency fixes → main)
```

### Branch Rules

| Source Branch | Target Branch | Purpose |
|--------------|---------------|---------|
| `feature/*` | `develop` | New features and enhancements |
| `bugfix/*` | `develop` | Bug fixes |
| `hotfix/*` | `main` | Critical production fixes |
| `develop` | `main` | Release merges |

### Creating a Feature

```bash
# Start from develop
git checkout develop
git pull origin develop

# Create feature branch
git checkout -b feature/your-feature-name

# Work on your changes...
# Commit using conventional commits

# Push and create PR
git push -u origin feature/your-feature-name
```

---

## Branch Naming

Use the following naming convention:

```
feature/<description>     # New features
bugfix/<description>      # Bug fixes
hotfix/<description>      # Critical fixes
docs/<description>        # Documentation only
release/<version>         # Release preparation
```

**Examples:**
- `feature/add-compose-detekt-rules`
- `bugfix/fix-pre-commit-path`
- `hotfix/critical-config-fix`
- `docs/update-getting-started`

---

## Conventional Commits

All commit messages **must** follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```
<type>(<scope>): <subject>
```

### Types

| Type | Description |
|------|-------------|
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation changes |
| `style` | Formatting, no code change |
| `refactor` | Code restructuring |
| `perf` | Performance improvement |
| `test` | Adding or updating tests |
| `chore` | Maintenance tasks |
| `ci` | CI/CD changes |
| `build` | Build system changes |
| `revert` | Revert a previous commit |

### Examples

```
feat(hooks): add pre-commit hook for ktlint
fix(setup): resolve path issue in arc-setup script
docs(readme): update installation instructions
chore(deps): update detekt to 1.23.7
ci(quality): add ktlint check to quality workflow
```

---

## Pull Request Process

1. **Create your branch** following the naming convention
2. **Make your changes** with conventional commits
3. **Run quality checks** locally:
   ```bash
   make lint
   make format
   ```
4. **Push your branch** and create a PR targeting `develop`
5. **Fill out the PR template** completely
6. **Request review** from a team member
7. **Address feedback** with additional commits
8. **Merge** once approved

### PR Requirements

- [ ] Follows conventional commit format
- [ ] Passes all CI checks
- [ ] Updated CHANGELOG.md (if applicable)
- [ ] Updated documentation (if applicable)
- [ ] Self-reviewed

---

## Code Quality Standards

### Shell Scripts

- Use `#!/bin/bash` shebang
- Include `set -e` for error handling
- Add descriptive comments
- Use meaningful variable names
- Quote all variables (`"$VAR"` not `$VAR`)

### YAML Files

- Use 2-space indentation
- Validate with a YAML linter before committing
- Add comments for non-obvious configurations

### Configuration Files

- Document all non-default settings
- Include links to documentation for rules
- Follow the existing format and structure

---

## Development Setup

### Prerequisites

- Git 2.30+
- Bash 4.0+
- GitHub CLI (`gh`) for label/protection scripts

### Local Testing

```bash
# Clone the repository
git clone https://github.com/arclabs-studio/ARCDevTools-Android
cd ARCDevTools-Android

# Verify shell scripts
bash -n arc-setup
bash -n hooks/pre-commit
bash -n hooks/pre-push
bash -n hooks/install-hooks.sh

# Verify YAML files
python3 -c "import yaml; yaml.safe_load(open('configs/detekt.yml'))"
python3 -c "import yaml; yaml.safe_load(open('workflows/quality.yml'))"
```

### Testing with a Project

```bash
# Create a test Android project
mkdir /tmp/test-android-project
cd /tmp/test-android-project
git init

# Add ARCDevTools-Android as submodule (local path for testing)
git submodule add /path/to/ARCDevTools-Android
./ARCDevTools-Android/arc-setup
```

---

## Questions?

- Check the [documentation](docs/README.md)
- Open an [issue](https://github.com/arclabs-studio/ARCDevTools-Android/issues)
- Review [ARCKnowledge-Android](https://github.com/arclabs-studio/ARCKnowledge-Android) for development standards

---

**ARC Labs Studio** - Made with care
