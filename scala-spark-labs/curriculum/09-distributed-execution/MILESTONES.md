# Curriculum 09 — Real Distributed Execution Evidence

## Purpose

Use the repository's six-million-row simulated transaction dataset and the isolated Azure Databricks development job to connect DataFrame plans to real jobs, stages, tasks, executors, memory/storage behavior, adaptive execution, skew, file sizing, and recoverable failures.

This curriculum observes evidence from one controlled environment. It does not turn a single elapsed-time result into a universal tuning rule.

## Dataset contract

- Archive: `datasets/fraud-transactions-v1.zip`
- Expected SHA-256: `06242096c77aef292f3950784f51c77e891df50c734dde5ba152cf68fa054858`
- Extracted member: `Fraud.csv`
- Verified shape: 6,362,620 data rows and 11 columns
- Data is simulated transaction-like educational data, not real bank activity.
- Spark cannot read the CSV member directly from the ZIP as an ordinary CSV source.
- Extract only into an ignored Curriculum 09 location and upload only to learner-approved development storage.

## Project boundary

- Preserve the tested deployment and Delta pipeline from Curricula 07–08.
- Add Curriculum 09 analysis code under package `curriculum09`.
- Keep small deterministic fixtures for correctness tests; reserve the full dataset for learner-run distributed evidence.
- Capture plan and UI/metric observations concisely rather than committing raw logs or screenshots containing workspace identifiers.
- Treat `isFraud` as a label for distribution analysis only; do not build a predictive model.
- Do not use `isFlaggedFraud` as a predictive input or claim real-world fraud conclusions.

Do not introduce production data, unrestricted cluster sizing experiments, destructive fault injection, streaming, machine learning, or benchmark claims that generalize beyond the observed environment.

## Milestone 1 — Verify and ingest the realistic dataset safely

**Learner outcomes**

- Verify archive path, checksum, member name, and ignored extraction target.
- Extract `Fraud.csv` without committing it.
- Upload to the approved development input path.
- Read it with an explicit schema and assert exact row count, field count, label counts, and transaction-type counts.
- Record source file size, resulting Spark input partitions, and any rejected rows.

**Completion evidence**

- Dataset identity and baseline counts match the verified contract.
- Local repository and cloud paths remain isolated and non-production.

## Milestone 2 — Map actions to jobs, stages, tasks, and exchanges

**Learner outcomes**

- Run one narrow action and one aggregation requiring an exchange.
- Map each action to its job, stage boundaries, task counts, input partitions, and output.
- Connect `Exchange` in the physical plan to the observed shuffle stage.
- Distinguish driver coordination from executor task execution using run evidence.

**Completion evidence**

- Learner-supplied plan and Spark UI evidence consistently maps action → job → stages → tasks.
- The learner explains why task count follows partitions at a stage boundary rather than total row count.

## Milestone 3 — Observe executor execution memory and cached storage

**Learner outcomes**

- Select one defensible reused, reduced DataFrame and persist it with an explicit storage level.
- Materialize it, inspect aggregate memory/disk cache size and per-executor block placement, then run two consumers.
- Observe execution-memory or spill metrics for an aggregation without assuming all cache reads require network transfer.
- Release the cache and verify its removal from storage evidence.

**Completion evidence**

- Storage evidence reports actual materialized size and executor distribution.
- Plans and logs distinguish local cached reads from downstream shuffle traffic.
- Cleanup is verified.

## Milestone 4 — Compare initial and final AQE behavior and diagnose skew

**Learner outcomes**

- Capture initial and final adaptive plans for a grouped or joined workload.
- Identify eligible AQE changes such as shuffle partition coalescing.
- Measure transaction-type and label imbalance and inspect per-task input/shuffle distributions.
- Distinguish business-value imbalance from task-level performance skew.
- Apply one evidence-driven mitigation only if observed task metrics justify it.

**Completion evidence**

- Initial/final plans and task metrics support the stated AQE and skew conclusions.
- The learner avoids salting or repartitioning merely because a column is imbalanced.

## Milestone 5 — Reason about file sizing and recoverable failures

**Learner outcomes**

- Write an isolated curated output and inspect file count and size distribution.
- Compare an obviously fragmented layout with one defensible layout without optimizing for a magic universal file size.
- Trigger only a safe, isolated recoverable failure such as one malformed batch or a failed task attempt in test code.
- Identify retry behavior, failure ownership, and the boundary between recomputation, idempotent rerun, and manual intervention.
- Run the full distributed lab and summarize environment-specific findings.

**Completion evidence**

- Output layout and failure conclusions are supported by concrete metrics.
- The learner explains why executor loss, task retry, cached-block loss, and job rerun are related but different recovery scopes.

## Completion boundary

Curriculum 09 completes realistic distributed execution evidence for jobs/stages/tasks, executor storage/execution memory, AQE, skew diagnosis, file layout, and recoverable failures. Stop there. Curriculum 10 owns operational reliability and delivery controls.
