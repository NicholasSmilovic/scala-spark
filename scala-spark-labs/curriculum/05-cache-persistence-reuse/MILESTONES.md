# Curriculum 05 — Caching, Persistence, and Reuse Decisions

## Purpose

Prove when shared DataFrame lineage is recomputed, how explicit persistence changes later physical plans, and how to release cached data safely. Learn to treat caching as a measured reuse decision rather than a default performance switch.

The curriculum uses small controlled fixtures because elapsed time on local data is not performance evidence. Correctness comes from exact results, storage-level state, formatted plans, and learner explanations.

## Project boundary

- Preserve all completed Curriculum 01–04 sources and tests.
- Learner-owned production code will live at `src/main/scala/curriculum05/OrderMetrics.scala`.
- Learner-owned tests will live at `src/test/scala/curriculum05/OrderMetricsSpec.scala`.
- Reuse ScalaTest 3.2.20 and the repository's Java 21 sbt configuration.
- Create one suite-owned `local[2]` Spark session with local networking and the Spark UI disabled.
- Use explicit in-memory order fixtures with integer IDs and quantities plus double unit prices.
- Write no persistent input or output files.
- Keep result assertions independent of incidental Spark row order.
- Use an explicit `StorageLevel` whenever persistence is introduced.

Do not introduce joins, broadcast hints, skew mitigation, accumulators, custom Spark listeners, streaming, external databases, generated large datasets, performance benchmarks, Spark UI exercises, executor-memory sizing rules, or cluster deployment in this curriculum.

## Milestone 1 — Prove reusable lineage without persistence

**Teach**

- Reusing a DataFrame value reuses its logical plan; it does not automatically reuse computed rows.
- Two downstream DataFrames can share the same upstream transformation while remaining separate lazy plans.
- Each uncached action may execute the shared upstream work again.
- Exact results establish behavior; local elapsed time does not establish recomputation cost.

**Learner actions**

- Create `OrderMetrics.scala` with a reusable transformation that adds `revenue = quantity * unit_price`.
- Add two reusable consumers of that prepared DataFrame: revenue totals by product and orders above a supplied revenue threshold.
- Create a Curriculum 05 ScalaTest suite using the established suite-level Spark lifecycle.
- Build one explicit in-memory fixture and assert the exact output of both consumers.
- Print both formatted plans and identify the shared upstream revenue expression.
- Trigger each consumer with its own deliberate action.

**Completion evidence**

- The focused Curriculum 05 suite passes under Java 21.
- Exact assertions prove both consumers receive the expected prepared values.
- Learner-supplied plans show the shared revenue transformation inside both uncached consumer plans.
- The learner explains why assigning and reusing a DataFrame does not itself cache computed rows and why separate actions may repeat upstream work.

Do not call `cache()`, `persist()`, or `unpersist()` yet.

## Milestone 2 — Persist and materialize one reused DataFrame

**Teach**

- `persist(...)` registers a lazy DataFrame for reuse and returns a DataFrame; it does not materialize rows by itself.
- The first action materializes cached partitions while producing that action's result.
- Later consumers of the same persisted plan can read cached data through `InMemoryTableScan`.
- Persistence changes physical reuse, not the logical result contract.
- An explicit storage level makes the memory/disk fallback decision visible.

**Learner actions**

- Persist the shared prepared-order DataFrame with `StorageLevel.MEMORY_AND_DISK`.
- Assert the selected storage level.
- Materialize it with one deliberate bounded-fixture action.
- Run the same two consumers and retain their exact uncached behavior assertions.
- Print formatted downstream plans and identify `InMemoryRelation` or `InMemoryTableScan`.

**Completion evidence**

- The suite passes with the same exact consumer results as Milestone 1.
- Learner-supplied plans show downstream consumers reading the persisted prepared-order plan.
- The learner explains the difference between registering persistence, materializing it with an action, and reading it during later actions.
- The learner states that the tiny fixture and local timing do not prove a production speedup.

Do not compare storage levels through timing or cache unrelated one-use DataFrames.

## Milestone 3 — Release persistence and make a defensible cache decision

**Teach**

- Cached executor data has a lifecycle and should be released when its reuse window ends.
- `unpersist(blocking = true)` waits for removal; it does not destroy the DataFrame's logical lineage.
- A later action can still recompute the DataFrame after unpersisting.
- Caching is most defensible when upstream work is meaningfully expensive, the result is reused by multiple actions, and the persisted working set is safe for available memory.
- Filtering and projecting before persistence can reduce the working set when every downstream consumer needs only that reduced contract.
- Caching a one-use, cheap, or oversized DataFrame can add materialization, memory, serialization, spill, and eviction costs without enough reuse benefit.

**Learner actions**

- Protect the persisted-data lifecycle with `try/finally`.
- Call `unpersist(blocking = true)` in cleanup and assert that the DataFrame returns to `StorageLevel.NONE`.
- Prove with an exact result that the same logical DataFrame remains usable and recomputes after unpersisting.
- Explain one defensible cache placement for the current two-consumer fixture and one case where caching should be avoided.

**Completion evidence**

- The full Curriculum 05 suite passes and leaves no persisted test DataFrame registered by the learner code.
- Exact assertions prove unpersisting affects reuse state rather than the DataFrame's logical result.
- The learner explains the cache lifecycle and gives a reuse-, cost-, and memory-based decision rather than “cache is faster.”

## Completion boundary

Curriculum 05 completes introductory DataFrame persistence, materialization, cached-plan evidence, cleanup, and cache-placement decisions. Stop there. Use later curricula for storage-level benchmarking, Spark UI analysis, executor-memory sizing, AQE tuning, skew mitigation, checkpointing, streaming state, or cluster deployment.
