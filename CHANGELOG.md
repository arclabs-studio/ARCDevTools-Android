# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [0.5.2] - 2026-10-29

Upstreams the local deviations found while building FavRes-Android Fase 3 (auth + Firebase).

### Added
- `arc.coverage.excludes`: Android modules can exclude extra classes from the JaCoCo gate with a
  list of Ant patterns in `extra["arc.coverage.excludes"]` (for SDK glue no local test can
  exercise). Documented in `docs/configuration.md`.
- Opt-in Firebase Local Emulator Suite in `android-ci.yml` and `tests.yml`:
  `ARC_FIREBASE_EMULATORS` (comma-separated, empty = off) and `ARC_FIREBASE_PROJECT` (`demo-*`).
  When set, the jobs install Node.js + `firebase-tools` 15.17.0 and run Gradle inside
  `firebase emulators:exec`. Documented in `docs/ci-cd.md`.
- `arc-setup --firebase-emulators <list> --firebase-project <id>` writes both values into the
  installed workflows (validated; the project id must start with `demo-`).

### Changed
- `tests.yml` installs JDK 17 and 21 like `android-ci.yml` (the Gradle daemon and the Firebase
  CLI run on 21).
- ARCKnowledge-Android submodule → v0.3.1 (state-driven error alerts are not navigation keys).
- arc-setup 1.5.1 → 1.5.2.

## [0.5.1] - 2026-10-27

Upstreams the local deviations and gaps found while building FavRes-Android (Fases 1–2).

**Upgrading:** `arc-setup` keeps an existing `gradle/libs.versions.toml`, and the synced
`build-logic/` now references two new catalog entries. Add them before building (or copy them from
`templates/gradle/libs.versions.toml`):

```toml
[libraries]
compose-screenshot-gradlePlugin = { module = "com.android.compose.screenshot:screenshot-test-gradle-plugin", version.ref = "composeScreenshot" }

[plugins]
arc-android-robolectric = { id = "arc.android.robolectric" }
```

An existing `.claude/settings.json` is kept, so its Stop hook still runs `./gradlew assembleDebug`:
flavored projects edit it to `assemble<Variant>` by hand (e.g. `assembleDevDebug`).

Re-running `arc-setup --with-workflows` replaces the release-drafter workflow and other workflow
files; pass the project's `--variant`.

### Added

- **`arc.android.robolectric`** convention plugin (`AndroidRobolectricConventionPlugin`,
  `RobolectricTestConfiguration.kt`; catalog id `arc-android-robolectric`): Android resources on
  the unit-test classpath, Robolectric + JUnit 4 on the JUnit Vintage engine, JDK 17+
  `--add-exports`. For Android modules that test on Robolectric without Compose or Room
- **`templates/github/release-drafter.yml`**: generic Release Drafter config (ARC categories and
  version resolver). `arc-setup --with-workflows` installs it to `.github/release-drafter.yml` when
  absent; Release Drafter reads it from `main`, so the check is red until the first release PR
- **`arc-setup --with-docs`**: opt-in for `docs.yml`
- Git hooks: Android environment preflight. Without `JAVA_HOME` and a working `java` on `PATH`,
  the hooks look for a JDK (`/usr/libexec/java_home -v <toolchainVersion>`, then Gradle-provisioned
  JDKs in `~/.gradle/jdks`); without `ANDROID_HOME`/`ANDROID_SDK_ROOT` and `local.properties`
  `sdk.dir`, they stop with an explicit message
- Catalog: `compose-screenshot-gradlePlugin` (build-logic `compileOnly`)

### Changed

- `arc.android.compose` applies `arc.android.robolectric` instead of wiring Robolectric inline
- `arc.android.room` applies `arc.android.robolectric` and adds `testImplementation`
  `androidx.test:core` (`ApplicationProvider` for in-memory database tests)
- **GitHub Actions on Node 24**: `actions/checkout@v7`, `actions/setup-java@v6`,
  `gradle/actions/setup-gradle@v6`, `actions/upload-artifact@v7`, `actions/upload-pages-artifact@v5`,
  `actions/deploy-pages@v5`, `actions/github-script@v9`, `softprops/action-gh-release@v3`,
  `release-drafter/release-drafter@v7` (workflow templates and this repo's workflows; release
  notes checked for breaking inputs — none used here)
- `arc-setup` no longer installs `docs.yml` by default: it runs Dokka, which the Gradle
  scaffolding does not apply (`--with-docs` to install it)
- `arc-setup --variant` also sets `ARC_VARIANT` in the installed `android-ci.yml` / `tests.yml`
  and the Claude Code Stop hook (`assemble<Variant>`, template placeholder `__ARC_VARIANT__`)
  when `.claude/settings.json` is first installed
- `arc-setup --with-gradle` (or `--force-gradle`) proceeds in an empty repository instead of
  failing project detection
- arc-setup 1.5.1, pre-commit hook 1.2.0, pre-push hook 1.3.0

### Fixed

- Screenshot validation is no longer pixel-exact: `arc.android.compose` sets a 0.05 % threshold on
  `PreviewScreenshotValidationTask` (plugin 0.0.1-alpha16 removed the `imageDifferenceThreshold`
  DSL), so references recorded on macOS pass on the Linux CI renderer
- The Stop hook ran `assembleDebug`, which does not exist in flavored projects (new installs;
  see Upgrading for existing ones)
- The pre-commit hook discarded Gradle's output (`2>/dev/null`) and reported any failure, including
  a missing JDK or SDK, as "ktlint found violations"; it now prints the end of the output
- PR template no longer mentions Dokka

---

## [0.5.0] - 2026-10-25

Audit hardening (independent audit of 2026-10-22).

### Added

- **`androidx.hilt:hilt-lifecycle-viewmodel-compose` 1.4.0** in the version catalog
  (`androidxHilt` version, `androidx-hilt-lifecycle-viewmodel-compose` library; verified on Google
  Maven 2026-10-22) and added by `arc.android.feature`, so feature modules can call
  `hiltViewModel()` from Compose / Navigation 3 entries
- **Variant parameter** for flavored projects: `android-ci.yml` reads a top-level
  `ARC_VARIANT` (default `Debug`, e.g. `DevDebug`) and runs `lint${ARC_VARIANT}`,
  `test${ARC_VARIANT}UnitTest`, `jacoco${ARC_VARIANT}UnitTestCoverageVerification` and
  `validate${ARC_VARIANT}ScreenshotTest`; `arc-setup --variant <Name>` (validated: alphanumeric,
  leading uppercase) writes the same task names into the Makefile
- **Makefile targets** `android-lint`, `coverage`, `screenshot` and `screenshot-update` (variant
  tasks); `make setup` re-runs arc-setup with the same `--variant`
- **`arc.coverage.minimum` Gradle property**: the JaCoCo line-coverage minimum is no longer
  hardcoded. Default `0.80` (set in the root `gradle.properties` template); extracted
  ARC*-Android packages set `1.00`. Values outside (0, 1] fail configuration with a clear message
- **`--allow-missing-skills`** for `scripts/setup-skills.sh` and `arc-setup`
- `.claude/skills/android-*` and `.claude/skills/arc-*` in the root `.gitignore` template;
  `arc-setup` also appends them, once, to an existing `.gitignore`
- **Gitflow workflows on this repo** (dogfooding): `enforce-gitflow.yml`, `release-drafter.yml`,
  `sync-develop.yml` and `validate-release.yml` copied unchanged into `.github/workflows/`, plus a
  `.github/release-drafter.yml` config (ARC standard categories, Android submodule install
  snippet), which `release-drafter.yml` requires
- Docs: flavored projects / `ARC_VARIANT` (`ci-cd.md`, `getting-started.md`), coverage threshold
  (`configuration.md`, `integration.md`), troubleshooting entries for "Task 'testDebugUnitTest'
  not found / ambiguous" and "Claude Code skills setup failed", release model in
  `CONTRIBUTING.md` (release PR `develop → main` + annotated tag on `main`; consumers pin tags)
- **Consumer fixture** `fixtures/consumer/`: flavored (`dev`/`pro`) ARC app with `:app`, `:domain`,
  `:core:ui` and `:feature:sample` (Hilt, `hiltViewModel()` from a Navigation 3 entry, Room 3 with
  `AndroidSQLiteDriver`, M3 Expressive theme, JUnit 5 + Turbine and Robolectric tests, one
  `@PreviewTest` screenshot). Its Gradle scaffolding is generated by `arc-setup` from `templates/`
  through the symlink `fixtures/consumer/ARCDevTools-Android -> ../..`; nothing generated is committed
- **Repository CI** `.github/workflows/ci.yml`: runs `arc-setup` on the fixture twice (idempotency
  check), then `./gradlew build` and the `DevDebug` lint, unit test, coverage, screenshot, detekt and
  ktlint tasks, uploading reports; a `shell` job runs `bash -n` and `shellcheck -S warning`
- `docs/ci-cd.md`: in flavored projects every Android module declares the same flavors

### Changed

- `scripts/setup-skills.sh` fails closed: missing ARCKnowledge-Android, a missing
  `.claude/skills/` directory, or any of `android-doctrine`, `android-testing-doctrine`,
  `android-verify`, `arc-cli-android` not linked with a `SKILL.md` exits 1 (previously exit 0);
  unknown options exit 1. `arc-setup` stops with an error when skills setup fails
- Install docs (`README.md`, `CLAUDE.md`, `docs/README.md`, `docs/getting-started.md`,
  `docs/integration.md`) pin a release tag: `git submodule add` (lands on `main`), then
  `git -C ARCDevTools-Android checkout v0.5.0`, the nested `submodule update`, and
  `git add .gitmodules ARCDevTools-Android`; updating moves to a newer tag instead of
  `git pull origin main`
- `docs/configuration.md` / `docs/troubleshooting.md`: detekt baselines are forbidden and
  `@Suppress` needs a documented false positive with approval (matches `android-doctrine`)
- `arc-setup` bumped to 1.5.0; template headers to 0.5.0

### Fixed

- `android-ci.yml`, `tests.yml`, the Makefile and the `pre-push` hook hardcoded unflavored task
  names (`testDebugUnitTest`, `validateDebugScreenshotTest`), which are ambiguous in flavored
  projects; `tests.yml` reads `ARC_VARIANT` and `pre-push` runs `make test` when the generated
  Makefile exists
- `arc.android.compose`: `validate<Variant>ScreenshotTest` / `update<Variant>ScreenshotTest` failed
  under Gradle 9 (`failOnNoDiscoveredTests`) in Compose modules without a `src/screenshotTest`
  source set (e.g. `app`); those modules' screenshot tasks now pass with nothing to compare, while
  a module that has screenshot sources but discovers no test still fails (found by the fixture)
- `release-drafter.yml` (template and this repo) grants `contents: write`, which release-drafter
  needs to create the draft release; with `contents: read` the push-to-`main` run fails. Note: the
  action reads `.github/release-drafter.yml` from the default branch, so PR runs fail until the
  config has landed on `main`
- Convention plugin KDoc: SDK levels are 37/36/26 (compile/target/min), not 36; coverage wording
  follows `arc.coverage.minimum`
- `enforce-gitflow.yml` and `validate-release.yml` (templates and this repo's copies) interpolated
  `github.head_ref` / `github.base_ref` and tag-derived outputs straight into `run:` shell, a
  script-injection vector through a crafted branch or tag name. Those values now reach the shell
  through `env:` and are read as quoted variables (found by the constitution review of 0.5.0)

---

## [0.4.0] - 2026-10-21

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

- `arc-setup` bumped to 1.4.0 (1.3.0 behaviour plus the Gradle scaffolding flags)
- `ARCKnowledge-Android` submodule bumped to the 2026 stack refresh (PR #7, `1a51b85`): the
  four Android skills (`android-doctrine`, `android-testing-doctrine`, `android-verify`,
  `arc-cli-android`) now ship inside the submodule, so `setup-skills.sh` links them into
  `<project>/.claude/skills/`
- Documentation updated for the Gradle scaffolding (`integration.md` rewritten,
  `getting-started.md`, `ci-cd.md`, `troubleshooting.md`, `README.md`)

### Fixed

- `configs/detekt.yml`: removed `MultilineRawStringIndentation.trimmingMethod` (not a detekt
  1.23.x property) and renamed `ForbiddenComment.values` to `comments` (deprecated key);
  private `@Preview`/`@PreviewTest` functions no longer reported by `UnusedPrivateMember`;
  removed `MatchingDeclarationName.multiplatformTargetSuffixes` (added in 0.2.0, but detekt
  1.23.8 rejects it: "is misspelled or does not exist")
- `configs/.editorconfig`: `@Composable` functions are exempt from ktlint's function-naming rule
- Docs: `dependencyResolution` typo in the settings example, outdated detekt / compose-rules
  versions, version references to a non-existent v1.0

---

## [0.3.0] - 2026-04-19

### Added

#### Claude Code Integration

- **Claude Hooks** (`claude-hooks/`) — 4 shell scripts for Claude Code automation
  - `session-start.sh` — Shows branch, changes, last release on session start
  - `validate-commit.sh` — Enforces Conventional Commits format
  - `block-dangerous-git.sh` — Blocks force push, git clean -f, reset --hard, branch -D
  - `format-on-save.sh` — Auto-formats .kt/.kts files with ktlint after Edit/Write
- **Claude Settings** (`.claude/settings.json`) — Shared hooks configuration
  - PostToolUse: format-on-save on Edit/Write
  - PreToolUse: validate commits + block dangerous git on Bash
  - Stop: verify compilation via `./gradlew assembleDebug`
  - SessionStart: load project context
- **Skills Setup** (`scripts/setup-skills.sh`) — Symlinks skills from ARCKnowledge-Android
- **MCP Setup** (`scripts/setup-mcp.sh`) — Registers recommended MCP servers (Context7, Android Skills)
- **MCP Template** (`configs/mcp/claude-mcp.json`) — Reference configuration for downstream projects

#### arc-setup Enhancements

- Claude hooks are now copied to downstream projects during setup
- `.claude/settings.json` is installed if not already present
- Skills are symlinked automatically during setup
- New `--with-mcp` / `--no-mcp` flags for MCP server configuration
- Makefile now includes `make skills` and `make mcp` targets
- arc-setup version bumped to 1.3.0

---

## [0.2.0] - 2026-03-01

### Updated

#### Tool Version Alignment

- **detekt**: Updated recommended version from 1.23.7 to **1.23.8** (Kotlin 2.0.21, bug fixes)
- **ktlint Gradle plugin**: Updated recommended version from 12.1.2 to **14.0.1**
- **ktlint**: Updated recommended version from 1.4.1 to **1.7.1** (Kotlin 2.2.0 support, context parameters)
- **compose-rules**: Added compatibility note for 0.5.x (targets detekt 2.x, incompatible with 1.23.x)

#### EditorConfig (`.editorconfig`) — Multiline, Wrapping & Parameters

- **Class signature wrapping**: Added `class-signature` rule with multiline forced at >= 3 parameters (aligned with function signatures and iOS SwiftFormat conventions)
- **Function signature wrapping**: Explicitly enabled `function-signature` rule
- **Argument list wrapping**: Configured `argument_list_wrapping_ignore_when_parameter_count_greater_or_equal_than = unset` (wrap based on `max_line_length` only)
- **Binary expression wrapping**: Enabled `binary-expression-wrapping` for arithmetic/logical operator multiline alignment
- **Chain method continuation**: Enabled with threshold of 4 chained calls (aligned with iOS)
- **Annotation handling**: Added `ktlint_annotation_handle_annotations_with_parameters_same_as_annotations_without_parameters = true` (ktlint 1.6.0+) — annotations with parameters get their own line, just like annotations without
- Added `property-naming` rule (ktlint 1.5.0+)
- Added explicit `no-unused-imports = enabled` (disabled by default since ktlint 1.7.0)
- Added `context-receiver-wrapping` rule (ktlint 1.7.0+)
- Added `modifier-order` rule with context receiver support (ktlint 1.7.0+)
- Added `[*.json]` section with `indent_size = 2`
- Reorganized config into logical sections (signatures, wrapping, annotations, naming, imports, etc.)

#### Detekt Configuration (`detekt.yml`)

- Aligned `MaxChainedCallsOnSameLine` threshold from 5 to **4** (consistent with ktlint chain-method-continuation)
- Updated `UndocumentedPublicClass` with new configurable options (`searchInInnerClass`, `searchInInnerInterface`, `searchInInnerObject`, `searchInProtectedClass`)
- Updated `MatchingDeclarationName` with `multiplatformTargetSuffixes` support (detekt 1.23.8)
- Documented `InjectDispatcher` false positive fix (detekt 1.23.8)
- Documented `UnnecessaryParentheses` floating-point fix (detekt 1.23.8)
- Documented `UseDataClass` expect class fix (detekt 1.23.8)

#### Detekt Compose Rules (`detekt-compose.yml`)

- Fixed rule set name from `compose` to `Compose` (correct casing for detekt)
- Updated documentation URL to point to compose-rules project
- Added configuration hints for `ViewModelForwarding` and `ViewModelInjection`
- Added configuration hints for `ComposableFunctionName` and `ComposableNaming`

#### Documentation

- Updated `integration.md`: detekt 1.23.8, ktlint plugin 14.0.1, ktlint 1.7.1
- Added compose-rules 0.5.x/detekt 2.x compatibility warnings
- Updated `troubleshooting.md`: new sections for ktlint 1.7.x issues (`no-unused-imports`, `context-receiver-wrapping`)
- Updated minimum detekt version reference to 1.23.8

#### Scripts & Hooks

- Bumped all script and hook versions to 1.1.0

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

[Unreleased]: https://github.com/arclabs-studio/ARCDevTools-Android/compare/v0.5.1...HEAD
[0.5.1]: https://github.com/arclabs-studio/ARCDevTools-Android/compare/v0.5.0...v0.5.1
[0.5.0]: https://github.com/arclabs-studio/ARCDevTools-Android/compare/v0.4.0...v0.5.0
[0.4.0]: https://github.com/arclabs-studio/ARCDevTools-Android/compare/v0.3.0...v0.4.0
[0.3.0]: https://github.com/arclabs-studio/ARCDevTools-Android/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/arclabs-studio/ARCDevTools-Android/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/arclabs-studio/ARCDevTools-Android/releases/tag/v0.1.0
