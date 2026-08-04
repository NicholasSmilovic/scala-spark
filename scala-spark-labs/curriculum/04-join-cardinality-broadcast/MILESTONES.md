# Curriculum 04 — Join Correctness, Cardinality, and Broadcast Decisions

## Purpose

Build reusable order-enrichment joins and prove their relational behavior before reasoning about physical execution. Learn how inner and left joins handle unmatched keys, how duplicate keys change row cardinality, and when broadcasting a genuinely small reference DataFrame avoids a large-side shuffle.

The curriculum reuses the local Spark and ScalaTest discipline established in Curriculum 03. Tests use explicit in-memory order and product fixtures; physical-plan observations use the same controlled local session. Timing is not evidence of production performance.

## Project boundary

- Preserve all completed Curriculum 01–03 sources and tests.
- Learner-owned production code will live at `src/main/scala/curriculum04/OrderEnrichment.scala`.
- Learner-owned tests will live at `src/test/scala/curriculum04/OrderEnrichmentSpec.scala`.
- Reuse ScalaTest 3.2.20 and the repository's Java 21 sbt configuration.
- Create one suite-owned `local[2]` Spark session with local networking and the Spark UI disabled.
- Use explicit in-memory DataFrames for orders and a product reference dataset.
- Write no persistent input or output files.
- Keep join-result assertions independent of incidental Spark row order.

Do not introduce caching/persistence, skew salting, bucketing, non-equi/range joins, streaming joins, external databases, generated large datasets, performance benchmarks, AQE tuning, cluster deployment, or production sizing rules in this curriculum.

## Milestone 1 — Prove an inner enrichment join

**Teach**

- A join combines rows by a key; it does not merely append two DataFrames.
- Both sides must expose compatible key types.
- An inner join retains only keys present on both sides.
- Using a shared-key join such as `Seq("product_id")` keeps one join-key column, while explicit projection controls the output contract.
- A reusable join method accepts DataFrames and returns another lazy DataFrame without owning Spark or test fixtures.

**Learner actions**

- Create `OrderEnrichment.scala` with one reusable inner product-enrichment method.
- Create a Curriculum 04 ScalaTest suite using the established suite-level Spark lifecycle.
- Build explicit in-memory order and product fixtures containing matched and unmatched product IDs.
- Assert exact enriched rows and matched order IDs without relying on row order.
- Trigger the join through a deliberate test action.

**Completion evidence**

- The Curriculum 04 suite passes under Java 21.
- Exact assertions prove matched product attributes and exclusion of unmatched orders.
- Input and output key types are explicit and compatible.
- The learner explains that calling the enrichment method builds a lazy join plan and the test action executes it.

Do not add left-join behavior, duplicate reference keys, or broadcast hints yet.

## Milestone 2 — Preserve and diagnose unmatched orders with a left join

**Teach**

- A left join preserves every left-side order while filling unmatched right-side attributes with null.
- A null produced by an unmatched join has a different cause from a null produced by failed parsing.
- Exact ID assertions prove preservation more strongly than a printed table.
- Count equality is an accounting check, not sufficient proof of which order was unmatched.

**Learner actions**

- Add a reusable left enrichment method with an explicit output contract.
- Reuse the controlled fixtures to prove matched attributes and one unmatched order.
- Assert the exact complete set of order IDs.
- Assert null product attributes for the specific unmatched ID.
- Assert that the left-join count equals the input order count while retaining exact row evidence.

**Completion evidence**

- Tests prove inner and left joins differ only in the expected unmatched behavior for the fixture.
- The unmatched order is identified by a stable ID and has null reference attributes.
- The learner distinguishes an unmatched-reference null from a safe-cast parsing null.

Do not repair missing products with defaults or silently discard unmatched orders.

## Milestone 3 — Make join cardinality and duplicate keys visible

**Teach**

- Join cardinality depends on matching rows per key, not only on left-side row count.
- A unique product key gives each matching order at most one reference match.
- Duplicate product keys legitimately multiply joined order rows.
- `dropDuplicates` without a rule chooses an arbitrary survivor and can hide a data-quality problem.
- A reusable duplicate-key diagnostic can remain a lazy DataFrame transformation.

**Learner actions**

- Add a focused duplicate product-key fixture.
- Prove the exact multiplied rows or per-order multiplicities produced by the join.
- Add a reusable transformation that reports product IDs appearing more than once.
- Assert the exact duplicate key and its count.
- Keep the normal unique-key fixture passing without arbitrary deduplication.

**Completion evidence**

- Tests demonstrate a one-to-many multiplication caused by a duplicated reference key.
- Duplicate-key diagnostics identify the exact product ID and occurrence count.
- The learner explains why a join can return more rows than the orders input.
- The learner explains why arbitrary deduplication is not a valid uniqueness contract.

Do not introduce skew mitigation or production data-quality infrastructure.

## Milestone 4 — Compare shuffle and broadcast physical strategies

**Teach**

- Logical join results and physical join strategies are separate concerns.
- An unhinted equi-join may require both sides to exchange data by the join key.
- A broadcast hash join distributes a small build side so the larger streamed side does not shuffle for that join.
- Spark can choose broadcast automatically when usable statistics and thresholds support it; an explicit `broadcast(...)` hint records a deliberate caller decision.
- Broadcasting is unsafe when the build side is not genuinely small enough for executor memory and network distribution.
- Local elapsed time and the tiny fixture do not establish a production performance win.

**Learner actions**

- Produce equivalent unhinted and explicitly broadcast enrichment plans from the same fixture.
- Assert that both strategies return the same exact rows.
- Print formatted physical plans in a focused learning test or plan inspection step.
- Identify the join operator, build side, broadcast exchange, and shuffle exchanges shown in learner-supplied output.
- Explain one safe and one unsafe broadcast scenario.

**Completion evidence**

- Behavioral assertions prove that changing physical strategy did not change join results.
- Learner-supplied formatted plans show and distinguish a shuffle-based join from a broadcast hash join.
- The learner explains why the product side is the build side in this controlled example.
- The learner states that broadcast decisions depend on measured/estimated size and available executor memory, not row-count intuition alone.

## Completion boundary

Curriculum 04 completes equi-join correctness, unmatched-reference behavior, duplicate-key cardinality, and introductory broadcast decisions. Stop there. Use later curricula for caching and persistence, join skew and salting, AQE tuning, window functions, non-equi joins, production statistics management, or cluster sizing.
