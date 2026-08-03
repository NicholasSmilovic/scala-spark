# Curriculum 01 Progress — Orders Foundations

- Status: Complete on 2026-07-31.
- Source artifact: `src/main/scala/SparkOrders.scala`
- Detailed historical records: `learning/archive/01-orders-foundations/`

| Milestone | Evidence |
| --- | --- |
| 1. Toolchain | Java 21, Scala 2.13.18, Spark 4.2.0, and sbt 1.12.11 verified; Spark ran successfully with `local[*]`; learner explained component roles. |
| 2. CSV DataFrame | Seven-column schema and twelve rows verified; learner explained full schema-inference pass and `show()` as an action. |
| 3. Clean/enrich | Eight correct valid rows; learner calculated revenue and explained lazy `filter`/`withColumn` transformations. |
| 4. Regional summary | Correct East/South/West/North totals and counts; learner identified grouping and ordering exchanges and the `show()` action. |

Retained cross-curriculum gaps are tracked only in `learning/OPEN_LOOPS.md`.
