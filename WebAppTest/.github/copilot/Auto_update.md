# CR Autonomous Agent Runbook (No-Git Mode)

 

This runbook describes how to invoke and use the Autonomous Automation Update Agent when `git` is NOT available and the operator will not review/approve PRs. The agent produces a package containing updated files, backups, diffs and safe PowerShell scripts to apply or restore changes. It also performs deterministic checks (ambiguity detection) and produces machine-readable artifacts to aid traceability.

 

## Expected inputs

- `jira_cr_text` (string): JIRA Change Request body or summary in plain text or markdown.

- `repo_path` (path): Local path to the automation repository copy you want to update.

- `jira_id` (string, optional): Identifier for the CR, e.g. `CR-1234`.

- `operator_username` (string, optional): Person invoking the apply script.

- `--framework` (optional): override auto-detected language/UI framework (e.g., `playwright`, `selenium`, `express`, `spring-boot`, `django`).

- `--start-from` (optional): start pipeline at a specific phase (`detection|analysis|patch|package`).

- `--dry-run` (optional): generate package and reports but do not produce `updated_files` (or mark them as preview).

 

## Pre-flight: Framework & UI Detection

When the agent inspects `repo_path` it attempts to auto-detect the dominant language, test runner and UI framework using heuristics (e.g., `pom.xml`, `package.json`, `requirements.txt`, `playwright`/`selenium` dependencies). Detection results are stored in `detection_report.json` inside the package and influence how the agent validates and packages changes. Use `--framework` to override when auto-detection is ambiguous.

 

## Ambiguity Analysis (mandatory)

Before generating patches the agent runs deterministic ambiguity checks on the `jira_cr_text`. If any of these are true the agent halts and emits `ambiguity_report.md` listing issues and recommended clarifications:

- Missing acceptance criteria or observable outcomes

- Compound or conflicting requirements

- Unbounded terms ("fast", "large", "recent") without quantification

- Missing or unclear locators/IDs where required by the CR

 

The agent will not apply changes for any item flagged as ambiguous; an operator response is required to continue.

 

## Outputs produced by the agent

- `CR-<JIRA-ID>-package.zip` containing:

  - `updated_files/` — new file contents for each changed file (mirror tree). In `--dry-run` mode these are marked as previews.

  - `backups/` — original file contents (`.orig`) and `.sha256` manifest

  - `patches/` — unified diffs for human review

  - `apply_patch.ps1` — safe apply script (interactive by default)

  - `restore_backups.ps1` — safe restore script

  - `change_report.md` — human summary and instructions

  - `impact_map.json` — detailed change map (file → location → old→new)

  - `regression_report.json` — sanity tests, risk, and recommended test order

  - `detection_report.json` — framework and UI detection results

  - `ambiguity_report.md` — present only if issues were detected

  - `questions_for_clarification.md` — blocked items requiring answers

 

## How to run the agent (high level)

1. Provide the agent with `jira_cr_text`, `repo_path`, and optional flags (`--framework`, `--start-from`, `--dry-run`).

2. The agent performs detection, ambiguity analysis and impact discovery; if ambiguous, it halts and writes `ambiguity_report.md`.

3. On clear inputs, agent produces the package in a specified output directory.

4. Operator reviews `change_report.md`, `impact_map.json` and `questions_for_clarification.md`.

5. Operator runs `apply_patch.ps1` in a staging environment to apply updates (or inspects diffs and copies files manually).

6. Operator runs the `sanity_tests` listed in `regression_report.json`.

7. If tests pass, operator promotes changes to the target environment using their usual VCS/CI/CD process or copies files manually.

8. If tests fail, operator runs `restore_backups.ps1` to revert changes.

 


## Agentic Prompt (for Autonomous Agent)

> **Agentic Prompt:**
> "Scan the automation codebase at `<repo_path>` and update all relevant scripts according to the requirements in `<jira_cr_text>` (CR ID: `<jira_id>`).  
> Package the changes as described in this runbook, and provide all required artifacts for review, application, and rollback.  
> Use the detected framework or override with `--framework` if specified.  
> Ensure ambiguity analysis, patch generation, and test validation are performed as per the workflow below."

---

## Workflow Steps

1. **Input Preparation**
   - Provide the agent with:
     - `jira_cr_text`: The change request text (plain text or markdown)
     - `repo_path`: Path to the automation codebase
     - `jira_id`: Change request ID (e.g., CR-1234)
     - Optional: `operator_username`, `--framework`, `--start-from`, `--dry-run`

2. **Framework & UI Detection**
   - Auto-detect the dominant language and UI framework in the codebase.

3. **Ambiguity Analysis**
   - Analyze the change request for missing or unclear requirements.
   - If ambiguous, halt and generate `ambiguity_report.md`.

4. **Patch Generation & Packaging**
   - Scan the codebase and update all relevant scripts.
   - Generate a package containing:
     - `updated_files/`, `backups/`, `patches/`
     - `apply_patch.ps1`, `restore_backups.ps1`
     - `change_report.md`, `impact_map.json`, `regression_report.json`, `detection_report.json`
     - `ambiguity_report.md`, `questions_for_clarification.md` (if needed)

5. **Review and Apply**
   - Extract and inspect the package:
     ```powershell
     Expand-Archive -LiteralPath .\CR-1234-package.zip -DestinationPath .\CR-1234-package
     Get-ChildItem .\CR-1234-package -Recurse | Format-List FullName
     notepad .\CR-1234-package\change_report.md
     notepad .\CR-1234-package\impact_map.json
     ```
   - Apply changes interactively:
     ```powershell
     Set-Location -Path .\CR-1234-package
     .\apply_patch.ps1 -Confirm
     ```
   - Or apply non-interactively (CI):
     ```powershell
     Set-Location -Path .\CR-1234-package
     .\apply_patch.ps1 -AutoApply -ExpectedHashesFile .\backups\hash_manifest.json
     ```

6. **Testing**
   - Run the sanity tests listed in `regression_report.json`:
     ```powershell
     .\run_tests.ps1 -Tests @("smoke_checkout","checkout_flow")
     ```

7. **Rollback if Needed**
   - If tests fail, revert changes:
     ```powershell
     .\restore_backups.ps1 -Confirm
     ```

---

## Invocation examples (PowerShell)

 

Extract package and inspect:

```powershell

Expand-Archive -LiteralPath .\CR-1234-package.zip -DestinationPath .\CR-1234-package

Get-ChildItem .\CR-1234-package -Recurse | Format-List FullName

notepad .\CR-1234-package\change_report.md

notepad .\CR-1234-package\impact_map.json

```

 

Apply changes (interactive confirmation):

```powershell

Set-Location -Path .\CR-1234-package

.\apply_patch.ps1 -Confirm

```

 

Apply changes (non-interactive CI-style):

```powershell

Set-Location -Path .\CR-1234-package

# CI: non-interactive apply (use with caution)

.\apply_patch.ps1 -AutoApply -ExpectedHashesFile .\backups\hash_manifest.json

```

 

Run sanity tests (example, replace with project test runner):

```powershell

# Example wrapper script or test runner invocation

.\run_tests.ps1 -Tests @("smoke_checkout","checkout_flow")

```

 

Rollback if needed:

```powershell

.\restore_backups.ps1 -Confirm

```

 

## Script behavior and safety checks

- Both scripts require confirmation (`-Confirm`) by default and will log actions to `apply_log.txt` and `restore_log.txt`.

- `apply_patch.ps1` verifies target file SHA-256 against the recorded original hash before overwriting; in CI mode the script accepts an expected-hash manifest to skip interactive prompts.

- `apply_patch.ps1` supports a `--dry-run` (preview) mode that reports files that would be changed and verifies checksums without writing files.

- If a checksum mismatch is detected, `apply_patch.ps1` aborts and lists discrepancies.

- `restore_backups.ps1` restores files from `backups/` and logs restored files.

 

## Quality & Coverage Criteria for Packaging

- Minimum sanity test set: at least one smoke test that covers the changed flow must be present in `regression_report.json`.

- Tags: affected features should be tagged with `@smoke` or a team-specific tag to enable focused runs.

- Lint/parse checks: feature files must pass a Gherkin parse check; page objects and step files should pass basic linting (project-specific).

 

## Failure & Recovery Policies

- Fail-fast: the agent halts on unresolved ambiguity and after two repeated unclear clarifications marks the item `BLOCKED`.

- If apply detects mismatched checksums, it aborts and logs the discrepancy; operator should investigate before proceeding.

- On post-apply test failures, operator should run `restore_backups.ps1` and collect logs; the agent will suggest rollback steps and create a failure report.

 

## Packaging and manual promotion

- Because `git` is not available, the operator is responsible for promoting changes from staging to production using their organization's process.

- The package includes diffs for record-keeping and can be archived in your change management system. The `impact_map.json` aids traceability.

 

## Security & Data Handling

- Test data must avoid real PII; synthetic values are required. The agent redacts obvious secrets and flags potential PII in `change_report.md`.

- Do not include access tokens or credentials in the package. Logs will redact lines that match common secret patterns.

 

## Questions and Blockers

- If any change was blocked due to missing locator or acceptance criteria, the agent will list it in `questions_for_clarification.md` with suggested patch snippet and an exact question for the CR author.

 

---

Generated by Autonomous Automation Update Agent (no-git mode).