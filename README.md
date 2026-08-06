# Scala/Spark Data Engineering Labs

A hands-on Scala and Apache Spark project covering batch ingestion, data
quality, execution plans, joins, persistence, semi-structured data, and
Parquet-backed pipelines.

The implementation lives in [`scala-spark-labs/`](scala-spark-labs/).

## What is covered

- Lazy DataFrame transformations and action-triggered execution
- Explicit schemas and diagnosable dirty-data quarantine
- Partitions, shuffles, pruning, and physical-plan inspection
- Reusable transformations with local ScalaTest coverage
- Join cardinality and broadcast-join decisions
- Persistence, materialization, reuse, and cleanup
- Nested JSON, safe typing, deterministic deduplication, and Parquet output

## Completed labs

| Lab | Focus |
| --- | --- |
| 01 | DataFrames, transformations, aggregation, and execution plans |
| 02 | Reliable CSV ingestion, validation, partitions, and Parquet |
| 03 | Reusable transformations and isolated Spark tests |
| 04 | Join correctness, cardinality, and broadcasting |
| 05 | Cache and persistence lifecycle |
| 06 | Nested JSON, quarantine, windows, and deduplication |

## Run the tests

Prerequisites are JDK 21 and sbt.

```bash
cd scala-spark-labs
sbt test
```

See the [project README](scala-spark-labs/README.md) for the module map,
focused commands, toolchain versions, and learning workflow.
