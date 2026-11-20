# Corrections log

A running record of what the AI got wrong and Yushir corrected. One entry is added per correction, in the PR where it happened, so the list in IMPROVEMENTS.md is taken from here rather than written from memory at the end.

Each entry says what the AI did, what Yushir changed or asked for, and why.

## Before v2: hardening v1

The one-shot port in #1 left some gaps that Yushir closed in follow-up PRs before v2 started. They predate v2 and belong to the same story.

| PR | What the port left | What Yushir changed |
|---|---|---|
| #2 | CI installed Temurin 17 through `setup-java@v4` on `ubuntu-latest`, with no check for a known security fix | Corretto 17 with `check-latest`, a CI step that fails below 17.0.21.1, `setup-java` pinned by SHA, and a fixed Ubuntu 24.04 runner |
| #3 | `checkout` and `upload-artifact` referenced by floating major tag, no Dependabot, no licence file | Both pinned by SHA, Dependabot for Actions, a rights notice in `LICENSE` |
| #7 | Workflow token had default permissions | Read-only `contents` permission |
| #8 | README carried a one-shot disclaimer | Removed after Yushir reviewed the suite |

## v2

| PR | What the AI did | Correction | Why |
|---|---|---|---|

No v2 corrections yet. PR 1 is awaiting review.
