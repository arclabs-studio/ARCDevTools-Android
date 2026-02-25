# CI/CD Guide

This guide covers GitHub Actions configuration for ARC Labs Studio Android projects.

---

## Table of Contents

- [Overview](#overview)
- [Available Workflows](#available-workflows)
- [Setup Instructions](#setup-instructions)
- [Runner and Billing](#runner-and-billing)
- [Configuration Reference](#configuration-reference)
- [Best Practices](#best-practices)

---

## Overview

ARCDevTools-Android provides 7 workflow templates optimized for Android projects. All quality checks run on `ubuntu-latest` (1x billing multiplier), keeping costs minimal.

---

## Available Workflows

| Workflow | Runner | Purpose |
|----------|--------|---------|
| `quality.yml` | ubuntu-latest | ktlint, detekt, Markdown link check |
| `tests.yml` | ubuntu-latest | Unit tests with Java 17 |
| `docs.yml` | ubuntu-latest | Dokka documentation to GitHub Pages |
| `enforce-gitflow.yml` | ubuntu-latest | Branch rules validation |
| `sync-develop.yml` | ubuntu-latest | Auto-sync main to develop |
| `validate-release.yml` | ubuntu-latest | Tag validation and GitHub Release creation |
| `release-drafter.yml` | ubuntu-latest | Auto-draft release notes from PRs |

---

## Setup Instructions

### 1. Copy Workflows

During `arc-setup`, choose to copy workflows when prompted:

```bash
./ARCDevTools-Android/arc-setup --with-workflows
```

This copies all workflow files to `.github/workflows/`.

### 2. Configure Repository

Set up the following in your GitHub repository:

**Settings > Actions > General:**
- Allow all actions and reusable workflows
- Read and write permissions for GITHUB_TOKEN

**Settings > Pages (for docs.yml):**
- Source: GitHub Actions

### 3. Create Labels

Run the label setup script:

```bash
./ARCDevTools-Android/scripts/setup-github-labels.sh
```

### 4. Configure Branch Protection

```bash
./ARCDevTools-Android/scripts/setup-branch-protection.sh
```

---

## Runner and Billing

### All Ubuntu Advantage

Unlike iOS projects that require macOS runners (10x billing multiplier), Android workflows run entirely on `ubuntu-latest`:

| Runner | Multiplier | Android Workflows |
|--------|------------|-------------------|
| `ubuntu-latest` | 1x | All 7 workflows |

This means Android CI/CD uses **10x fewer billing minutes** than iOS for the same operations.

### Estimated Monthly Usage (Private Repo)

For a typical project with ~30 PRs/month:

| Workflow | Runs | Minutes Each | Total |
|----------|------|--------------|-------|
| Quality | 60 | 3 min | 180 min |
| Tests | 30 | 5 min | 150 min |
| Git Flow | 30 | 1 min | 30 min |

**Total:** ~360 minutes (well within the 2,000 free tier)

---

## Configuration Reference

### Repository Variables

Configure in **Settings > Secrets and variables > Actions > Variables**:

| Variable | Description | Example |
|----------|-------------|---------|
| `JAVA_VERSION` | Override Java version | `17` |

### Workflow Customization

Each workflow can be customized after copying. Common modifications:

**Adding Android instrumented tests:**

```yaml
# Add to tests.yml
instrumented-tests:
  name: Instrumented Tests
  runs-on: ubuntu-latest
  steps:
    - uses: actions/checkout@v4
    - uses: actions/setup-java@v4
      with:
        distribution: 'temurin'
        java-version: '17'
    - uses: reactivecircus/android-emulator-runner@v2
      with:
        api-level: 34
        script: ./gradlew connectedAndroidTest
```

**Adding build verification:**

```yaml
# Add to quality.yml
build-check:
  name: Build Check
  runs-on: ubuntu-latest
  steps:
    - uses: actions/checkout@v4
    - uses: actions/setup-java@v4
      with:
        distribution: 'temurin'
        java-version: '17'
    - run: ./gradlew assembleDebug --daemon
```

---

## Best Practices

1. **Run all checks on Ubuntu** - Android tooling works natively on Linux
2. **Use Gradle caching** - The `gradle/actions/setup-gradle@v4` action handles this
3. **Use `--daemon`** - Keeps Gradle daemon alive for faster subsequent tasks
4. **Combine related tasks** - Run detekt and ktlint in the same job to reduce overhead
5. **Cache dependencies** - Gradle caches are handled automatically by the setup-gradle action
6. **Skip on docs changes** - Add path filters to skip builds when only docs change

---

## Troubleshooting

### "Permission denied" on gradlew

```yaml
- name: Make gradlew executable
  run: chmod +x ./gradlew
```

### Gradle daemon issues in CI

```yaml
- name: Run with no-daemon
  run: ./gradlew test --no-daemon
```

### Out of memory

```yaml
- name: Configure Gradle memory
  run: |
    echo "org.gradle.jvmargs=-Xmx2g -XX:+HeapDumpOnOutOfMemoryError" >> gradle.properties
```

---

## See Also

- [Getting Started](getting-started.md) - Installation walkthrough
- [Integration Guide](integration.md) - build.gradle.kts setup
- [Troubleshooting](troubleshooting.md) - Common issues
