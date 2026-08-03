# Curriculum 02 — Reliable Ingestion and Partitions

## Purpose

Build a separate ingestion application that can accept several dirty CSV files without silently losing bad records, produce typed valid and rejected datasets, reason about partitions and shuffles, and create a queryable Parquet layout.

This curriculum answers the learner's questions about a 500 GB dirty CSV, directories of many files, and how Spark distributes work. The local dataset is intentionally small; correctness must come from plans and evidence rather than timing claims.

## Project boundary

- Preserve the completed `src/main/scala/curriculum01/SparkOrders.scala` application.
- The learner owns a new source file at `src/main/scala/curriculum02/SparkIngestion.scala`.
- Run the new application explicitly with `runMain curriculum02.SparkIngestion`; do not replace the Curriculum 01 main class.
- Read from `data/curriculum-02/incoming/`.
- Write only beneath `output/curriculum-02/`.
- Continue using Java 21, Scala 2.13.18, Spark 4.2.0, and sbt 1.12.11.

Do not introduce joins, broadcast joins, caching/persistence, streaming, cloud storage, Databricks, or table formats in this curriculum.

## Milestone 1 — Land multiple CSV files under an explicit schema

**Teach**

- Reading a directory of files as one DataFrame.
- `StructType`/`StructField` as an explicit input contract.
- Why a raw landing schema can keep incoming fields as strings until validation.
- Explicit schemas versus the extra pass required by `inferSchema`.
- Why file count, DataFrame partition count, and row count are different concepts.

**Learner actions**

- Create the new `curriculum02.SparkIngestion` application with the established safe `SparkSession` lifecycle.
- Define an explicit eight-column raw schema.
- Read the incoming CSV directory with headers and without schema inference.
- Print the schema, input file names, and total row count.

**Completion evidence**

- The new application runs successfully with Java 21.
- It reports three input files and eighteen total rows.
- The schema contains the eight expected columns as strings.
- The learner explains why the explicit schema avoids the inference pass but does not prove that field values are valid.

Do not cast, reject, repartition, or write data yet.

## Milestone 2 — Type, validate, and quarantine without silent loss

**Teach**

- Normalization before validation, including trimming.
- Safe conversion with `try_cast`.
- Technical validity versus business validity.
- `when`/`otherwise`, null checks, allowed-value checks, and a rejection reason.
- Multiple actions and why an uncached lazy plan may be recomputed.

**Validation contract**

- `order_id` must parse as a positive integer.
- `customer_id` and `product` must be non-empty after trimming.
- `region` must normalize to `North`, `South`, `East`, or `West`.
- `quantity` must parse as an integer greater than zero.
- `unit_price` must parse as a positive decimal value.
- `status` must be `completed` or `cancelled`.
- `order_date` must parse as an ISO date.

**Learner actions**

- Preserve the raw string columns long enough to diagnose failures.
- Create typed normalized columns.
- Attach a useful rejection reason.
- Split the plan into valid and rejected DataFrames.
- Display enough evidence to verify both paths.

**Completion evidence**

- Nine valid rows and nine rejected rows are accounted for; their counts sum to eighteen.
- Invalid numbers, blank required values, an invalid region/status, and an impossible date are visible in the rejected output with reasons.
- The learner explains why malformed values should not simply disappear.
- The learner explains why separate `count()`/`show()` actions may execute shared upstream work more than once when it has not been persisted.

Do not add caching to solve the recomputation yet.

## Milestone 3 — Observe partitions and make one deliberate shuffle decision

**Teach**

- Input file splits, DataFrame partitions, tasks, executor cores, and why they are not one-to-one.
- `spark_partition_id()` as row-level evidence and `rdd.getNumPartitions` as structural evidence.
- Narrow transformations versus wide transformations.
- `coalesce` versus `repartition`.
- `Exchange` in a formatted plan.
- Why minimizing partition count is not the same as minimizing shuffle cost.
- The role of the configured shuffle partition count and Adaptive Query Execution.

**Learner actions**

- Inspect the actual input/validated partition distribution.
- Compare one `coalesce` plan with one `repartition` plan on the lab data.
- Choose the version appropriate for the later partitioned write and justify it.
- Print the relevant formatted plan.

**Completion evidence**

- The learner reports observed partition evidence without assuming one partition per file or core.
- The learner identifies whether an `Exchange` appears and what records it redistributes.
- The learner explains that too few partitions can reduce parallelism or create oversized tasks.
- The choice is justified from the intended write layout, not from tiny local timing.

Do not tune cluster-wide settings or claim a production partition size from this tiny dataset.

## Milestone 4 — Build and prove a partitioned Parquet curated layer

**Teach**

- A DataFrame write as an action.
- Parquet as a typed columnar format.
- File-system partitioning with `partitionBy`.
- Output directories/files versus Spark execution partitions.
- Partition discovery, partition pruning, column pruning, and Parquet filter pushdown.
- Why one globally ordered output file is usually the wrong scalability goal.

**Learner actions**

- Write valid typed rows as Parquet partitioned by normalized region.
- Write rejected rows separately beneath the Curriculum 02 output directory.
- Read the curated Parquet base path back.
- Filter for `North`, select only needed columns, display the result, and print the formatted plan.
- Inspect the generated directory layout.

**Completion evidence**

- The curated layer contains nine valid rows across four region directories.
- The rejected layer contains nine diagnosable rows.
- The filtered read returns the three valid North rows.
- The plan shows the region condition as a partition filter and reads only the required data columns.
- The learner distinguishes partition pruning from row-level filter pushdown and explains why Parquet does not need CSV-style schema inference.

## Completion boundary

Curriculum 02 completes the ingestion-and-partitioning lab. Stop there and compress its progress record. Use later curricula for joins/broadcasting, caching/reuse, testing, deployment, streaming, or production table formats.
