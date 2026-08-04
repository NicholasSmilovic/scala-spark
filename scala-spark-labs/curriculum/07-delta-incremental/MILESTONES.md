# Curriculum 07 — Incremental Pipelines with Delta Lake

## Purpose

Extend the curated event pipeline from immutable Parquet outputs to transactional Delta tables. Prove schema enforcement, deliberate schema evolution, deterministic `MERGE` upserts, idempotent reruns, version history, and bronze/silver/gold table boundaries.

## Compatibility gate

Before implementation, verify the current official Delta Lake compatibility matrix against the repository's Spark, Scala, and Java versions. Pin one supported combination and record it. If the completed root Spark version is unsupported, isolate Curriculum 07's build/runtime rather than silently installing an incompatible Delta artifact or rewriting completed curricula.

## Project boundary

- Preserve completed Curriculum 01–06 sources and tests.
- Learner-owned production code will live under package `curriculum07` with the primary source `IncrementalDeltaPipeline.scala`.
- Learner-owned tests will live under package `curriculum07` with the primary suite `IncrementalDeltaPipelineSpec.scala`.
- Use suite-owned temporary Delta table paths for local tests.
- Reuse the curated event contract from Curriculum 06 through explicit test fixtures rather than coupling tests to Curriculum 06 output directories.
- Keep every merge source unique on the merge key before calling `MERGE`.
- Test exact table snapshots and version history after each operation.

Do not introduce Azure deployment, streaming, change data feed, `VACUUM`, optimization commands, production catalogs, generated large datasets, or performance benchmarking in this curriculum.

## Milestone 1 — Establish a compatible Delta runtime and transactional table

**Learner outcomes**

- Verify and pin a supported Spark/Delta/Scala/Java combination.
- Configure the Delta session extensions/catalog required by the selected runtime.
- Write an initial curated fixture as a Delta table and read it back exactly.
- Identify Parquet data files and the `_delta_log` as different parts of one table.
- Inspect table history and prove the initial commit version.

**Completion evidence**

- Compatibility source and exact pins are recorded.
- The focused suite passes with an exact version-zero snapshot and history assertion.
- The learner explains that Delta adds transaction metadata around data files rather than replacing Spark's DataFrame API.

## Milestone 2 — Prove schema enforcement and deliberate evolution

**Learner outcomes**

- Attempt an incompatible or unexpected-schema append and assert its failure contract.
- Preserve the unchanged target snapshot after the rejected write.
- Add one approved nullable column with operation-scoped schema evolution.
- Assert the evolved schema and old/new row values exactly.

**Completion evidence**

- Tests prove rejection without unintended mutation.
- Tests prove one explicitly approved schema evolution.
- The learner distinguishes schema enforcement from schema evolution and rejects session-wide auto-evolution as a casual default.

## Milestone 3 — Upsert one deterministic incremental batch

**Learner outcomes**

- Build a source batch containing one existing event update and one new event.
- Deduplicate the source to one row per merge key before merging.
- Use Delta's programmatic Scala `MERGE` API with explicit matched-update and not-matched-insert behavior.
- Assert the exact post-merge target snapshot and commit history.

**Completion evidence**

- The focused suite proves update and insert behavior without duplicate target keys.
- The learner explains why duplicate source keys make merge semantics unsafe unless resolved before the merge.

## Milestone 4 — Make reruns idempotent

**Learner outcomes**

- Run the same incremental batch twice.
- Assert that the second run produces the same logical table snapshot and row count.
- Distinguish logical idempotency from “no new Delta version was written.”
- Add a second batch and prove deterministic state progression.

**Completion evidence**

- Exact snapshots prove repeatable reruns.
- History evidence is interpreted without assuming that an unchanged logical result must imply no commit.
- The learner defines the stable merge key and update contract.

## Milestone 5 — Build bronze, silver, and gold table boundaries

**Learner outcomes**

- Write raw accepted event envelopes to bronze, typed deduplicated events to silver, and one exact business aggregate to gold.
- Keep each layer's schema and ownership explicit.
- Rerun the batch pipeline from the same inputs and prove identical logical outputs.
- Run the full Curriculum 07 suite.

**Completion evidence**

- Full suite passes with exact bronze/silver/gold snapshots and isolated temporary table paths.
- The learner explains what each layer guarantees, where quarantine evidence lives, and why layering alone does not make a pipeline reliable.

## Completion boundary

Curriculum 07 completes local transactional tables, schema controls, deterministic merge/upsert, idempotent incremental batches, history, and medallion boundaries. Stop there. Curriculum 08 owns packaging and Azure Databricks deployment.
