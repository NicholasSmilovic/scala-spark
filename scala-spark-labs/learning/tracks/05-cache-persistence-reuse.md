# Curriculum 05 Progress — Caching, Persistence, and Reuse Decisions

- Status: Complete.
- Production source: `src/main/scala/curriculum05/OrderMetrics.scala`
- Test source: `src/test/scala/curriculum05/OrderMetricsSpec.scala`
- Persistent input/output: none; tests use explicit in-memory fixtures.

| Milestone | Status | Concise verified evidence |
| --- | --- | --- |
| 1. Reusable uncached lineage | Complete | Java 21 focused suite passes; separate actions return exact order-independent totals and threshold rows, and both analyzed plans contain the shared revenue projection. Learner distinguishes shared planning from cached rows and explains that no reuse storage was requested. |
| 2. Persist and materialize | Complete | Passing exact results after explicit `MEMORY_AND_DISK` registration and deliberate materialization; supplied downstream plans show `InMemoryRelation`/`InMemoryTableScan`. Learner distinguishes registration, first-action materialization, executor-local cache reads, and downstream shuffle. |
| 3. Cleanup and cache decisions | Complete | Passing suite uses `try/finally`, blocking `unpersist`, verifies `StorageLevel.NONE`, and proves exact results recompute afterward. Learner favors expensive multi-action branch points that fit safely and avoids cheap one-use or oversized caches; local timing is not production evidence. |

## Current blocker

None. Curriculum 05 is complete; later activation state is tracked in `learning/ACTIVE.md`.

## Record-keeping rule

Record only learner-supplied compile/test output, formatted-plan output, storage-level observations, and explanations that satisfy a milestone gate. Do not treat prepared curriculum files or unexecuted code as evidence.
