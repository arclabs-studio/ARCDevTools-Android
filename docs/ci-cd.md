# CI/CD Guide

This guide covers GitHub Actions configuration for ARC Labs Studio Android projects.

---

## Table of Contents

- [Overview](#overview)
- [Available Workflows](#available-workflows)
- [Flavored Projects (ARC_VARIANT)](#flavored-projects-arc_variant)
- [Firebase Emulators (ARC_FIREBASE_EMULATORS)](#firebase-emulators-arc_firebase_emulators)
- [Setup Instructions](#setup-instructions)
- [Runner and Billing](#runner-and-billing)
- [Configuration Reference](#configuration-reference)
- [Best Practices](#best-practices)

---

## Overview

ARCDevTools-Android provides 8 workflow templates optimized for Android projects. All quality checks run on `ubuntu-latest` (1x billing multiplier), keeping costs minimal.

---

## Available Workflows

| Workflow | Runner | Purpose |
|----------|--------|---------|
| `android-ci.yml` | ubuntu-latest | `./gradlew build` (assemble, Lint, unit tests, coverage gate, detekt, ktlint), the `${ARC_VARIANT}` Lint / unit-test / coverage / screenshot tasks, detekt + ktlint |
| `quality.yml` | ubuntu-latest | ktlint, detekt, Markdown link check |
| `tests.yml` | ubuntu-latest | `test${ARC_VARIANT}UnitTest` (JDK 17 + 21) |
| `docs.yml` | ubuntu-latest | Dokka documentation to GitHub Pages (opt-in: `--with-docs`, needs the Dokka plugin) |
| `enforce-gitflow.yml` | ubuntu-latest | Branch rules validation |
| `sync-develop.yml` | ubuntu-latest | Auto-sync main to develop |
| `validate-release.yml` | ubuntu-latest | Tag validation and GitHub Release creation |
| `release-drafter.yml` | ubuntu-latest | Auto-draft release notes from PRs |

### android-ci.yml

Requires the Gradle scaffolding (`arc-setup --with-gradle`). Every job installs JDK 17 and 21
(`actions/setup-java`, Temurin; the daemon runs on 21 as pinned by
`gradle/gradle-daemon-jvm.properties`) and caches Gradle with `gradle/actions/setup-gradle`.

| Job | Command | Fails when |
|-----|---------|------------|
| Build, Lint, Unit Tests | `./gradlew build`, then `lint<Variant>`, `test<Variant>UnitTest`, `jacoco<Variant>UnitTestCoverageVerification` | Kotlin or Lint warning, failing test, coverage below `arc.coverage.minimum` (default 80%), detekt or ktlint finding, or an `ARC_VARIANT` the project does not have |
| Screenshot Tests | `./gradlew validate<Variant>ScreenshotTest` | A `@PreviewTest` differs from its committed reference image |
| detekt + ktlint | `./gradlew detekt ktlintCheck` | Any static-analysis finding |

`<Variant>` is the workflow's `ARC_VARIANT` (default `Debug`). Reports (tests, coverage, Lint,
screenshots) are uploaded as artifacts.

---

## Flavored Projects (ARC_VARIANT)

AGP names variant tasks after flavor + build type. An unflavored project has `testDebugUnitTest`;
a project with flavors `dev`/`pro` has `testDevDebugUnitTest` and `testProDebugUnitTest` instead,
and `testDebugUnitTest` no longer resolves. `android-ci.yml` therefore reads the variant from a
top-level environment variable:

```yaml
env:
  ARC_VARIANT: Debug   # flavored projects: flavor + build type, e.g. DevDebug
```

FavRes (flavors `dev`/`pro`) sets `ARC_VARIANT: DevDebug`, which makes CI run `lintDevDebug`,
`testDevDebugUnitTest`, `jacocoDevDebugUnitTestCoverageVerification` and
`validateDevDebugScreenshotTest`. Screenshot references then live in
`<module>/src/screenshotTestDevDebug/reference`.

Every Android module must declare the same flavors (same dimension and names). Gradle runs a task
name such as `testDevDebugUnitTest` only in the modules that have it, so an unflavored library
(variant `debug`) is silently skipped by the CI and Makefile tasks. Pure Kotlin/JVM modules have no
variants; `build` covers them (`test`, `jacocoTestCoverageVerification`). `fixtures/consumer`
shows the setup.

`./ARCDevTools-Android/arc-setup --variant DevDebug` uses the same value everywhere: the
Makefile, `ARC_VARIANT` in the installed `android-ci.yml` and `tests.yml` (`--with-workflows`), and
the Claude Code Stop hook (`assembleDevDebug`) when `.claude/settings.json` is first installed.

---

## Firebase Emulators (ARC_FIREBASE_EMULATORS)

Tests that run the Firebase SDK against the Firebase Local Emulator Suite (e.g. Auth and Firestore
repositories on Robolectric) need the emulators up while Gradle runs. `android-ci.yml` and
`tests.yml` support it as an opt-in:

```yaml
env:
  ARC_FIREBASE_EMULATORS: ""            # e.g. auth,firestore; empty = off
  ARC_FIREBASE_PROJECT: demo-project    # demo-* id: never reaches a real project
```

When `ARC_FIREBASE_EMULATORS` is set, the jobs install Node.js and a pinned `firebase-tools`
(it needs JDK 21, the default the jobs install) and wrap the Gradle steps in
`firebase emulators:exec --only <list> --project <id> "./gradlew …"`. The repository needs a
`firebase.json` that declares those emulators (ports) — the same file developers use locally with
`firebase emulators:start`.

`arc-setup --with-workflows --firebase-emulators auth,firestore --firebase-project demo-myapp`
writes both values. Like `--variant`, pass them again whenever the workflows are re-copied: a run
without `--firebase-emulators` installs them switched off. FavRes uses
`--variant DevDebug --firebase-emulators auth,firestore --firebase-project demo-favres`.
Always pass the same `--variant` when re-running `arc-setup --with-workflows`.

---

## Setup Instructions

### 1. Copy Workflows

During `arc-setup`, choose to copy workflows when prompted:

```bash
./ARCDevTools-Android/arc-setup --with-workflows
```

This copies the workflow files to `.github/workflows/` (with `ARC_VARIANT` set from `--variant`),
the issue/PR templates, and `.github/release-drafter.yml` when absent. `docs.yml` runs Dokka, which
the Gradle scaffolding does not apply, so it is only installed with `--with-docs`:

```bash
./ARCDevTools-Android/arc-setup --with-workflows --with-docs
```

Release Drafter reads `.github/release-drafter.yml` from the default branch (`main`), so the
`release-drafter.yml` check fails on pull requests until the first release PR merges the config
into `main`.

### 2. Configure Repository

Set up the following in your GitHub repository:

**Settings > Actions > General:**
- Allow all actions and reusable workflows
- Read and write permissions for GITHUB_TOKEN

**Settings > Pages (only with `--with-docs`):**
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
| `ubuntu-latest` | 1x | All 8 workflows |

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
    - uses: actions/checkout@v7
    - uses: actions/setup-java@v6
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
    - uses: actions/checkout@v7
    - uses: actions/setup-java@v6
      with:
        distribution: 'temurin'
        java-version: '17'
    - run: ./gradlew assemble${{ env.ARC_VARIANT }} --daemon
```

---

## Best Practices

1. **Run all checks on Ubuntu** - Android tooling works natively on Linux
2. **Use Gradle caching** - The `gradle/actions/setup-gradle@v6` action handles this
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
