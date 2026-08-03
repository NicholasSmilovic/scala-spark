# Curriculum 01 — Spark Orders Foundations

The milestones are gates, not a checklist to complete automatically. Advance only after the learner supplies the stated evidence.

## Milestone 1 — Make the local toolchain work

**Starting state**

`SparkOrders.scala` creates a local `SparkSession`, prints the Spark version and master, and stops.

**Teach**

- The roles of Java, Scala, sbt, and Spark.
- Why sbt downloads Scala and Spark libraries even though `scala` is not installed globally.
- What `object`, `def main`, and `val` mean, with brief Java equivalents.
- What `local[*]` means.
- Why `spark.stop()` belongs in `finally`.

**Learner actions**

- Inspect the selected versions.
- Run the project with Java 21 through sbt.
- Paste or describe the relevant output.

**Completion evidence**

- The process exits successfully.
- Output reports Spark 4.2.0 and `local[*]`.
- The learner can state the distinct jobs of Java, sbt, Scala, and Spark.

Do not read the CSV during this milestone.

## Milestone 2 — See the CSV as a DataFrame

**Teach**

- `spark.read`, CSV options, DataFrames, headers, and inferred schemas.
- `printSchema()` versus `show()`.
- Why the working directory controls the relative CSV path.
- The early scan caused by schema inference.

**Learner actions**

- Add CSV reading to `SparkOrders.scala`.
- Print the schema.
- Display the original rows.
- Run the application and inspect the twelve rows.

**Completion evidence**

- The schema contains the seven expected columns.
- Twelve source rows display.
- The learner identifies `show()` as the first visible data action and explains what schema inference did.

Do not filter or calculate revenue during this milestone.

## Milestone 3 — Clean and enrich the orders

**Teach**

- Spark `Column` expressions.
- `filter` and `withColumn`.
- Transformations versus actions.
- Lazy evaluation.
- Why `=!=` and `>` here do not produce ordinary Scala booleans.

**Learner actions**

- Import Spark SQL functions.
- Exclude cancelled orders.
- Exclude quantities of zero or less.
- Add `revenue = quantity * unit_price`.
- Display the valid enriched rows.

**Completion evidence**

- Eight valid rows display.
- Orders `1004`, `1006`, `1007`, and `1009` are absent.
- The learner predicts at least one row's revenue and explains why assigning the transformed DataFrame did not itself process every row.

Do not group, sort, or inspect the plan during this milestone.

## Milestone 4 — Build and explain the regional summary

**Teach**

- `groupBy`, aggregation, aliases, and descending sorting.
- The distinction between a transformation pipeline and the `show()` action.
- Why grouping and global sorting may introduce shuffle exchanges.
- What `explain("formatted")` provides.
- Why insert batching does not remove a Spark shuffle.

**Learner actions**

- Group valid orders by region.
- Calculate total revenue and order count.
- Sort total revenue descending.
- Display the result.
- Print the formatted execution plan.

**Completion evidence**

| Region | Total revenue | Order count |
| --- | ---: | ---: |
| East | 770.00 | 2 |
| South | 380.00 | 2 |
| West | 340.00 | 2 |
| North | 240.00 | 2 |

The learner must also identify the grouping and global sorting exchanges in the plan and explain lazy evaluation in their own words.

## Completion boundary

Milestone 4 completes the starter project. Stop there. Recommend a new, separate project for later topics such as joins, broadcast decisions, caching, partitioning, or Parquet.
