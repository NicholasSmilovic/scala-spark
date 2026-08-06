# Curriculum 07 Runtime Compatibility

Reviewed on 2026-08-06 before activating Curriculum 07.

## Completed root runtime

| Component | Version |
| --- | --- |
| Apache Spark | 4.2.0 |
| Scala | 2.13.18 |
| Java | 21 |
| sbt | 1.12.11 |

Curricula 01–06 keep these completed root pins unchanged.

## Official compatibility evidence

- The [Delta Lake 4.2.0 release](https://github.com/delta-io/delta/releases/tag/v4.2.0) states that its Delta Spark artifacts are built for Apache Spark 4.1.0 and 4.0.1 and are published for Scala 2.13. It does not list Spark 4.2.0.
- The [Apache Spark 4.1.0 release](https://spark.apache.org/releases/spark-release-4-1-0.html) records Scala 2.13.17 for that Spark line.
- The [Apache Spark 4.1 documentation](https://spark.apache.org/docs/4.1.1/) states that Spark 4.1 runs on Java 17/21 and Scala 2.13.
- The [Delta Lake quick start](https://docs.delta.io/quick-start/) documents the required `DeltaSparkSessionExtension` and `DeltaCatalog` session settings.

## Selected isolated runtime

| Component | Selected version |
| --- | --- |
| Apache Spark | 4.1.0 |
| Delta Lake | 4.2.0 |
| Scala | 2.13.17 |
| Java | 21 |
| ScalaTest | 3.2.20 |
| sbt | 1.12.11 |

Use a separate sbt build under `labs/curriculum07/`.

The planned Delta dependency is the Spark-4.1-specific Scala 2.13 artifact:

```text
io.delta:delta-spark_4.1_2.13:4.2.0
```

The isolated Spark SQL dependency is:

```text
org.apache.spark:spark-sql_2.13:4.1.0
```

The local `SparkSession` must include:

```text
spark.sql.extensions=io.delta.sql.DeltaSparkSessionExtension
spark.sql.catalog.spark_catalog=org.apache.spark.sql.delta.catalog.DeltaCatalog
```

## Local verification gate

Documentation compatibility is not runtime evidence. Milestone 1 remains incomplete until the learner-owned isolated build:

1. Resolves the selected dependencies.
2. Starts a local Java 21 Spark session with the Delta extension and catalog.
3. Writes and rereads an exact Delta fixture.
4. Produces `_delta_log/00000000000000000000.json`.
5. Reports table-history version `0`.

Do not add Delta dependencies to the completed root build and do not claim Spark 4.2.0 compatibility without newer official evidence.
