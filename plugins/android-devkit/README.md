# android-devkit

A Claude Code marketplace containing one plugin: an opinionated **Android + GitHub + software-engineering** development environment.

It gives Claude Code the context to build Android apps to a shipping standard — accessibility treated as correctness, Play Store compliance checked before it costs a review cycle, and a GitHub CI/CD setup that runs on events rather than on a clock.

## Install

```sh
# From a local checkout
/plugin marketplace add /path/to/android-devkit
/plugin install android-devkit@android-devkit

# Or from a git remote
/plugin marketplace add <owner>/android-devkit
/plugin install android-devkit@android-devkit
```

Then, in an Android project, ask Claude to run the `init-android-project` skill.

## What you get

### Subagents

| Agent | Purpose |
|---|---|
| `a11y-reviewer` | Accessibility review — labels, 48dp touch targets, contrast, font scaling, focus order, gesture alternatives. Treats accessibility defects as correctness defects. |
| `codebase-sanity` | Whole-repo longitudinal audit targeting AI-development rot: duplication, dead code, complexity creep, pattern inconsistency, architectural drift, test health. |
| `doc-curator` | Documentation health — KDoc on public API, README and setup drift, diagram drift, and which release documents a diff just invalidated. |

### Skills

| Skill | Purpose |
|---|---|
| `init-android-project` | One-shot provisioning: preflight permission checks, Gradle quality tooling, workflows, branch protection, auto-merge, labels, board, then verifies CI actually runs. |
| `run` | Build, install and drive the app on a device or emulator. Includes components with no launcher entry, log capture, and screenshots. |
| `play-release` | Google Play release: build config, signing, Data Safety, privacy policy, listing, and staged rollout. The part CI cannot check. |
| `refactor` | Prototype to production — pick a seam, characterize with tests, change in reversible steps. Keeps behavior-preserving and behavior-changing work in separate commits. |
| `compose-ui` | Jetpack Compose conventions: state hoisting, theming, sizing, accessibility, performance, previews. |
| `i18n` | String resources across locales — drift audits, hardcoded-string extraction, plurals, placeholders. |
| `feature-request` | Plain-language request into a labeled, board-ready GitHub issue. |
| `progress-report` | On-demand status summary from git, PRs, issues and CI. |

### Hooks

| Hook | Event | Behavior |
|---|---|---|
| `format-on-edit` | PostToolUse (Edit/Write) | Formats touched `.kt`/`.kts` with ktlint, or Gradle spotless if configured. |
| `strings-sync` | PostToolUse (Edit/Write) | Warns when `values/` and `values-*/` string keys drift. |
| `remind-board-issue` | PreToolUse (Bash) | On branch creation or `gh pr create`, requires the change to trace to an issue. |

All hooks are no-ops outside their target situation and never block a tool call.

### Templates

`templates/` holds what `init-android-project` installs into a repository:

- **Workflows** — `ci` (format, static analysis, lint, build, unit tests), `security` (gitleaks, CodeQL, OSV), `instrumented` (path-filtered emulator matrix), `release` (tag-driven signed AAB/APK), `playstore-check`, `metrics`, `board`, `automerge`.
- **`scripts/play-compliance.sh`** — static Play configuration checks: targetSdk floor, signing, versionCode, debuggable/cleartext flags, permission inventory.
- **Issue templates** (bug, feature, accessibility), PR template, `dependabot.yml`.
- **`gradle/quality.gradle.kts`** — Spotless, detekt with baseline, Lint config.
- **`claude/settings.json`** — the companion plugin set.

## Design decisions

**No `on: schedule` anywhere.** Every workflow runs on a push, a PR, a tag, or a manual dispatch. Nothing runs unattended on a calendar. The one exception is Dependabot, which has no event-driven mode by design — it answers "did the outside world change?", so it is set to `daily` and its PRs auto-merge once CI passes.

**Squash-only, auto-merge.** `init-android-project` configures squash as the only merge method with branch deletion on merge, and `automerge.yml` enables GitHub's native auto-merge on PRs labeled `automerge` and on Dependabot PRs. Branch protection stays authoritative — the workflow never merges anything itself.

**detekt with a baseline.** Existing debt is recorded so it does not block; new debt fails the build. This is what makes the kit usable on an existing prototype rather than only on greenfield.

**Accessibility is not optional.** It has a dedicated reviewer, its own issue template, checklist items in the PR template, and Lint's accessibility category is never disabled.

## Caveats

- **Verify action versions before first use.** Pinned `uses:` versions age; `init-android-project` prompts for this, but check them.
- **`board.yml` needs a `PROJECT_TOKEN`** secret and a `PROJECT_URL` variable — the default `GITHUB_TOKEN` cannot write to a Projects v2 board.
- **`release.yml` needs keystore secrets** (`KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`) and a matching `signingConfig`. Without them it builds unsigned.
- **The Play target-SDK floor rises annually.** `PLAY_TARGET_SDK_FLOOR` is a template variable for that reason — check the current requirement rather than trusting the default.

## License

MIT.
