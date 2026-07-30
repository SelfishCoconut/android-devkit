---
name: init-android-project
description: Provision a complete Android development environment on a GitHub repository — CI/CD workflows, branch protection, auto-merge, labels, board, issue templates, and Gradle quality tooling. Use for "set up the project", "initialize CI", "wire up GitHub", "bootstrap this repo".
---

# Initialize an Android project environment

One-shot provisioning of the whole GitHub side: workflows, repository settings, board, templates, and the Gradle quality tooling those workflows depend on.

**This is destructive in places** (it changes repository settings and can add branch protection). Work through the phases in order, and confirm with the user before phase 3.

## Phase 0 — Preflight

Verify everything before writing anything. A half-provisioned repo is worse than an unprovisioned one.

```sh
gh auth status                                   # authenticated?
gh api user --jq .login                          # who
gh repo view --json nameWithOwner,visibility,defaultBranchRef,isFork
gh auth status --show-token 2>/dev/null | grep -i scopes
```

Check and report:

- **Authentication** — if `gh auth status` fails, stop. Tell the user to run `gh auth login` themselves; do not attempt an interactive login from a tool call.
- **Scopes** — provisioning needs `repo` and `workflow`. Creating a Projects v2 board additionally needs `project` (or `read:project` + write). If a scope is missing, say exactly which, and give the command: `gh auth refresh -s workflow,project`.
- **Admin rights** — branch protection and merge settings require admin on the repo. Check `gh api repos/{owner}/{repo} --jq .permissions`. Without admin, skip phase 3 and say so rather than failing silently.
- **Repo exists?** If not, offer to create it: `gh repo create <name> --private --source=. --remote=origin`. Ask public vs private; do not decide this for the user.
- **Project shape** — confirm it is an Android Gradle project (`settings.gradle.kts`, `gradlew`, an application module). Read `compileSdk`, `targetSdk`, `minSdk`, `applicationId`, the JDK level, and whether a version catalog exists. Everything downstream is generated from these, so read them, do not assume.
- **Gradle wrapper** — check `git ls-files gradle/wrapper/gradle-wrapper.jar` actually returns the jar. Some repos gitignore it, and then `./gradlew` does not exist after a CI checkout and *every* workflow fails on its first step.

  If it is untracked, **recommend committing it** (Gradle's own guidance) and explain why, because the alternative has a failure mode that is not obvious: pinning `gradle-version` on `setup-gradle` and invoking `gradle` instead means CI no longer reads the wrapper at all, so a Dependabot wrapper bump *passes CI while breaking every local build* — CI cannot validate the one file it is not allowed to see. If the wrapper stays untracked, also add a Dependabot `ignore` for `gradle-wrapper`, or that exact bump will land.

  The supply-chain worry that motivates ignoring the binary is already handled: `gradle/actions/setup-gradle` verifies every wrapper jar's checksum on each run, and you can verify a jar by hand against `https://services.gradle.org/distributions/gradle-<version>-wrapper.jar.sha256` (follow redirects — `curl -sL`). Still ask before un-ignoring a file the project deliberately excluded; present the evidence and let the user decide.
- **Existing setup** — list `.github/workflows/` and `.claude/`. If files already exist, show what would be overwritten and ask. Never clobber existing CI without explicit consent.

Report the preflight result as a short table and stop for confirmation if anything is missing.

## Phase 1 — Gradle quality tooling

The workflows call these tasks; without them CI fails on the first run. Add, if absent:

- **Spotless** (with ktlint) — `spotlessCheck` / `spotlessApply`.
- **detekt** — with a **baseline generated from the current code** (`detektBaseline`) when the project is an existing prototype. This is the point: existing debt is recorded and does not block, while new debt fails the build.
- **Android Lint** — `lintDebug`, with `abortOnError = true` for the release variant. Do not disable the Accessibility category.
- **Unit test scaffolding** — if `app/src/test/` does not exist, create it with one real test of an actual pure function in the codebase. A test job with no tests reports green and means nothing; say so if you have to create a placeholder.
- **Dependency locking** so vulnerability scanning has a resolved graph to read.

Add these to the version catalog rather than inline, if the project uses one.

## Phase 2 — Files

Copy from `${CLAUDE_PLUGIN_ROOT}/templates/` into the repo, substituting the values read in phase 0:

```
templates/github/workflows/*.yml   -> .github/workflows/
templates/github/dependabot.yml    -> .github/dependabot.yml
templates/github/ISSUE_TEMPLATE/   -> .github/ISSUE_TEMPLATE/
templates/github/PULL_REQUEST_TEMPLATE.md
templates/github/scripts/          -> .github/scripts/
```

Substitute: `__APPLICATION_ID__`, `__MODULE__` (usually `app`), `__JAVA_VERSION__`, `__MIN_SDK__`, `__DEFAULT_BRANCH__`, `__PLAY_TARGET_SDK_FLOOR__`.

**Verify action versions before committing.** Pinned versions in the templates age; check each `uses:` against the action's current release and update. A workflow referencing a nonexistent action version fails on the first run and is a confusing first impression.

Make scripts executable (`chmod +x .github/scripts/*.sh`).

## Phase 3 — Repository settings (confirm first)

```sh
# Squash-only merges, auto-delete branches, allow auto-merge
gh api -X PATCH repos/{owner}/{repo} \
  -F allow_squash_merge=true -F allow_merge_commit=false -F allow_rebase_merge=false \
  -F delete_branch_on_merge=true -F allow_auto_merge=true \
  -f squash_merge_commit_title=PR_TITLE -f squash_merge_commit_message=PR_BODY
```

Branch protection on the default branch — required status checks matching the CI job names, and no force pushes:

```sh
gh api -X PUT repos/{owner}/{repo}/branches/{branch}/protection \
  --input - <<'JSON'
{
  "required_status_checks": {"strict": true, "contexts": ["quality", "build", "unit"]},
  "enforce_admins": false,
  "required_pull_request_reviews": null,
  "restrictions": null,
  "allow_force_pushes": false,
  "allow_deletions": false
}
JSON
```

Note for solo development: `required_pull_request_reviews` is null on purpose — requiring a reviewer when there is only one developer blocks every PR forever. Required *status checks* still apply, so CI must pass. If the user works with others, ask before setting this.

Warn clearly: branch protection means direct pushes to the default branch stop working. Confirm the user wants that.

## Phase 4 — Labels, milestones, board

Create the label set the templates and workflows reference:

`bug`, `feature`, `accessibility`, `infra`, `docs`, `automerge`, `dependencies`

```sh
gh label create accessibility --color 0E8A16 --description "Barrier for assistive-technology users" --force
```

Board (Projects v2): create if absent, with columns `Backlog`, `In Progress`, `Verify`, `Done`, and link the repo. If the `project` scope is missing, skip and tell the user which command to run — do not fail the whole init over the board.

## Phase 5 — Verify

Provisioning is not done until something has run:

```sh
gh workflow list
git switch -c chore/verify-ci && git commit --allow-empty -m "chore: verify CI" && git push -u origin HEAD
gh pr create --title "chore: verify CI" --body "Verifies the provisioned workflows run." --label infra
gh run watch
```

Watch the first run to completion. Report which jobs passed, which failed, and why. Then clean up the verification branch.

## Report

Finish with: what was created, what was skipped and why, what needs a human (keystore secrets, Play console setup, board scope), and the first thing to do next. Be explicit about anything you could not verify.
