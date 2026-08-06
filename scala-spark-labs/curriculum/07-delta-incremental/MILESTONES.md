# Curriculum 07 — Incremental Pipelines with Delta Lake

## Purpose

Extend the curated event pipeline from immutable Parquet outputs to transactional Delta tables. Prove schema enforcement, deliberate schema evolution, deterministic `MERGE` upserts, idempotent reruns, version history, and bronze/silver/gold table boundaries.

## Compatibility gate

Before implementation, verify the current official Delta Lake compatibility evidence against the repository's Spark, Scala, and Java versions. The activation review selected an isolated Spark 4.1.0 / Delta Lake 4.2.0 / Scala 2.13.17 / Java 21 runtime because Delta Lake 4.2.0 does not publish support for the completed root's Spark 4.2.0 runtime. Local compilation and a transaction-log smoke test are still required before the compatibility gate is complete.

## Project boundary

- Preserve completed Curriculum 01–06 sources and tests.
- Keep the isolated sbt build under `labs/curriculum07/`; do not change the completed root build pins.
- Learner-owned production code will live at `labs/curriculum07/src/main/scala/curriculum07/IncrementalDeltaPipeline.scala`.
- Learner-owned tests will live at `labs/curriculum07/src/test/scala/curriculum07/IncrementalDeltaPipelineSpec.scala`.
- Use suite-owned temporary Delta table paths for local tests.
- Reuse the curated event contract from Curriculum 06 through explicit test fixtures rather than coupling tests to Curriculum 06 output directories.
- Keep every merge source unique on the merge key before calling `MERGE`.
- Test exact table snapshots and version history after each operation.

Do not introduce Azure deployment, streaming, change data feed, `VACUUM`, optimization commands, production catalogs, generated large datasets, or performance benchmarking in this curriculum.

## Definition of done for every milestone

- The requested Delta behavior and focused evidence pass inside the isolated build.
- Exact table state and relevant history are asserted without depending on incidental row or file order.
- Touched code has no unused imports or variables, duplicate declarations, dead scaffolding, obsolete comments, or accidental formatting drift.
- Cleanup is completed inside the behavior milestone rather than deferred to a standalone cleanup milestone.

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

## Milestone 3 — Upsert deterministic batches and prove idempotency

**Learner outcomes**

- Build a source batch containing one existing event update and one new event.
- Deduplicate the source to one row per merge key before merging.
- Use Delta's programmatic Scala `MERGE` API with explicit matched-update and not-matched-insert behavior.
- Assert the exact post-merge target snapshot and commit history.
- Run the same batch twice and prove the same logical table snapshot and row count.
- Distinguish logical idempotency from “no new Delta version was written.”
- Add a second batch and prove deterministic state progression.

**Completion evidence**

- The focused suite proves update and insert behavior without duplicate target keys.
- Exact snapshots prove repeatable reruns and deterministic second-batch progression.
- History evidence is interpreted without assuming that an unchanged logical result must imply no commit.
- The learner explains why duplicate source keys make merge semantics unsafe unless resolved before the merge.
- The learner defines the stable merge key and update contract.

## Milestone 4 — Build an idempotent bronze, silver, and gold pipeline

**Learner outcomes**

- Give each input batch a stable `batch_id`.
- Ingest raw accepted event envelopes to bronze without duplicating the same batch on rerun.
- Merge typed deduplicated current event state into silver.
- Rebuild one exact gold aggregate from silver so it cannot drift from current state.
- Keep each layer's schema, key, write semantics, and ownership explicit.
- Rerun the same batch and prove identical logical bronze, silver, and gold outputs.
- Run the full Curriculum 07 suite.

**Completion evidence**

- Full suite passes with exact bronze/silver/gold snapshots and isolated temporary table paths.
- History and snapshots prove the layer-specific rerun contracts rather than merely proving that three directories exist.
- The learner explains what each layer guarantees, where quarantine evidence lives, and why naming layers bronze/silver/gold does not itself make a pipeline correct or reliable.

## Completion boundary

Curriculum 07 completes local transactional tables, schema controls, deterministic merge/upsert, idempotent incremental batches, history, and medallion boundaries. Stop there. Curriculum 08 owns packaging and Azure Databricks deployment.
