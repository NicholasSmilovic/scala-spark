# Learner Profile

This file stores durable cross-curriculum context only. Do not turn it into a session transcript.

## Background and teaching fit

- Most familiar with PHP, Laravel, npm, and relational-database comparisons.
- Has some Java exposure; Scala, JVM terminology, and distributed Spark are newer.
- Learns best by writing useful code after seeing the concrete purpose and data flow.
- Wants a practical adult pace: one coherent behavior change at a time, not token-sized steps.
- Prefers closely related edits and their validating command in one chunk; save-only or readiness checkpoints feel artificially slow unless inspection is genuinely needed.
- Uses four-space indentation for Scala and wants all tutor-provided Scala fragments formatted that way to avoid manual reformatting.
- Prefers a linear explanation flow without nested sidenotes: answer the direct question, give one essential correction if needed, then return to one next action.
- For an unfamiliar Scala or Spark API, needs the exact minimal syntax pattern before using it; do not ask for guessed method names or signatures.
- When a concept does not land, concrete explanation from the current rows, files, or execution plan works better than terminology drills or fill-in-the-blank questions.
- Wants direct correction and expects the tutor to push back when a performance assumption is unsafe.
- Wants the agreed Curriculum 06–10 roadmap kept stable; do not silently promote one technique into a replacement curriculum or renumber/reorder topics without an explicit redesign request.
- Wants a clear finish line: Curriculum 06 completes the core batch Spark track, while Delta, Azure, distributed operations, and production reliability remain optional extensions rather than implied prerequisites.

## Job-opportunity context

- This Spark study began after an RBC interview for a software development engineer position in Toronto.
- The learner's professional background is full-stack development. Spark and data engineering were new areas rather than prior claimed specialties.
- The interviewer offered to reconnect after approximately one week to see how much the learner could learn, specifically as evidence of teachability and growth. Treat the expected Spark follow-up as primarily conversational unless newer evidence says otherwise.
- Interview topics recalled by the learner included Spark transformations and actions, lazy evaluation, `map`/`flatMap`, broadcasting, repeated shuffles, shared cluster resource allocation, and deployment. Databricks, data lakes, Snowflake, and Snowpark were broader ecosystem context.
- The learner's original answer proposed insert batching as a way to reduce repeated shuffling. An important follow-up story is the corrected model: shuffles redistribute data between partitions for operations such as grouping, joining, sorting, and repartitioning; mitigation depends on the cause rather than insert batch size.
- The interviewer's name was not preserved.
- Preserve this context across curricula. Do not ask the learner to restate it unless a genuinely missing detail is required, such as the interviewer's name or a newly supplied job description. Do not merge context from other interview processes into this opportunity.

## Learner ownership

- The learner types commands and edits Scala.
- The tutor explains a command/change before requesting it, inspects saved source directly, and uses pasted runtime output as evidence.
- Do not run the application, install tools, or write the Scala solution unless explicitly asked.

## Durable demonstrated foundations

- Distinguishes Scala source, the Scala compiler, JVM bytecode, the JDK/JVM, sbt, and Spark.
- Understands `SparkSession`, `local[*]`, `val`, a Scala singleton `object`, `main`, and cleanup with `finally`.
- Distinguishes lazy DataFrame transformations from actions; knows `show()` triggers work and returns no DataFrame.
- Understands that DataFrame assignments retain logical plans rather than copying rows, and that separate uncached actions may execute shared upstream work again.
- Has safely converted raw strings with `try_cast`, cleaned text with `trim`, and separated valid from rejected records while retaining diagnostic reasons.
- Explains `col(...)` as a reference/expression over row values and `lit(...)` as a fixed literal expression.
- Has written filters, a derived revenue column, grouped aggregation, descending sorting, and a formatted plan.
- Identified hash-partitioning and range-partitioning exchanges and connected them to grouping and global sorting.
- Understands that reducing rows before a required wide operation can reduce shuffle volume, while forcing the fewest partitions is not automatically faster.
- Distinguishes source files, passive data partitions, per-partition tasks, and available CPU task slots using observed file/partition mappings.
- Compared `repartition` (full redistribution with `Exchange`) against `coalesce` (partition reduction without full shuffle), including the balance-versus-cost tradeoff.
- Reads parsed, analyzed, optimized, and physical plan sections; recognizes column pruning and understands `isFinalPlan=false` as an initial AQE plan that may revise eligible runtime decisions.
- Has built a raw-string landing-to-curated pipeline with typed valid and diagnosable rejected Parquet outputs.
- Distinguishes durable Parquet storage from application-local caching, and distinguishes directory partition pruning from Parquet column pruning.
- Has observed Parquet predicate pushdown in a physical plan and understands that a pushed predicate still requires Spark's final row-level filter.
- Understands that one driver coordinates a Spark application, `collect()` gathers results from all Spark partitions into driver JVM memory, and subsequent Scala collection operations are local rather than distributed; treats `collect()` as appropriate only for bounded results such as small test fixtures.
- Has built local Spark tests with suite lifecycle management, explicit in-memory schemas, exact order-independent row assertions, table-driven validation cases, and temporary-directory Parquet integration coverage.
- Contrasts lazy `DataFrame => DataFrame` transformations with a write method returning Scala `Unit`, whose observable contract is its filesystem side effect or failure.
- Distinguishes inner and left shared-key join contracts, including unmatched right-side nulls, and explains duplicate-key cardinality as the combinations of matching rows rather than a guarantee of one output per left row.
- Reads shuffle-based versus broadcast join plans: recognizes two-sided hash-partitioning exchanges and sort-merge execution, product-side `BroadcastExchange`, `BroadcastHashJoin`, `BuildRight`, and the network/memory requirement for a safely small build side.
- Distinguishes persistence registration, first-action materialization, executor-side cached blocks, `InMemoryTableScan` reads, and blocking `unpersist`; treats caching as a reuse/upstream-cost/executor-pressure decision and does not treat tiny local timing as production performance evidence.
- Has landed nested JSON under an explicit schema, retained syntactically malformed input separately from valid-but-invalid business fields, safely parsed timestamps/decimals/sequences under ANSI mode, flattened structs, preserved arrays, and proved exact curated/rejected contracts.
- Understands that Scala `DataFrame` is `Dataset[Row]`, distinguishes lazy distributed Dataset values from local arrays returned by `collect()`, and recognizes typed `Dataset[Event]` as requiring a real JVM type and encoder.
- Has selected deterministic event-version winners with `row_number` over an event-key window, explained why input order is unsafe across partitioning/shuffle, and read partial/final `WindowGroupLimit`, hash exchange, required sorts, window, filter, and projection in the physical plan.
- Has verified the Curriculum 06 end-to-end Parquet boundary: deduplicated typed curated rows and diagnostic rejected rows remain separate, exact, and rerunnable under overwrite mode.

## Maintenance rule

Add something here only when it affects future curricula. If a gap appears once, track it in `OPEN_LOOPS.md`; if it recurs or materially changes teaching, summarize it here rather than copying the full history.
