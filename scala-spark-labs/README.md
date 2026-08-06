# Scala/Spark Learning Labs

This project is a progressive set of Scala and Apache Spark labs. Each lab adds
one practical data-engineering concern while keeping the earlier work available
for comparison.

Labs 01–06 complete the local batch-processing core. Later curricula extend
that foundation into Delta Lake, Databricks deployment, distributed execution,
and production reliability.

## Highlights

- Explicit schemas instead of relying on inference
- Safe parsing with retained raw values and useful rejection reasons
- Partition, shuffle, pruning, and Adaptive Query Execution inspection
- Reusable `DataFrame => DataFrame` transformations
- Exact, order-independent ScalaTest assertions
- Temporary-directory Parquet integration tests
- Inner and left joins, duplicate-key cardinality, and broadcast joins
- Explicit persistence, materialization, plan inspection, and cleanup
- Nested JSON with structs and arrays
- Malformed-record retention and business-rule quarantine
- Deterministic event-version selection with a window
- Separate curated and rejected Parquet outputs

## Run the project

Prerequisites:

- JDK 21
- sbt 1.12.11 or a compatible sbt launcher

Run the complete core test suite:

```bash
sbt test
```

Run a focused suite:

```bash
sbt "testOnly curriculum06.JsonEventPipelineSpec"
```

Run one of the executable ingestion labs:

```bash
sbt "runMain curriculum01.SparkOrders"
sbt "runMain curriculum02.SparkIngestion"
```

## Lab map

| ID | Focus | Status |
| --- | --- | --- |
| 01 | DataFrames, lazy transformations, aggregation, and shuffle plans | Complete |
| 02 | Explicit schemas, dirty-data quarantine, partitions, and Parquet pruning | Complete |
| 03 | Reusable transformations and isolated file-boundary tests | Complete |
| 04 | Join correctness, cardinality, and broadcast decisions | Complete |
| 05 | Caching, persistence, and reuse decisions | Complete |
| 06 | Nested JSON, quarantine, deterministic deduplication, and publication | Complete |
| 07 | Incremental pipelines with Delta Lake | Active optional extension |
| 08–10 | Databricks deployment, distributed execution, and reliability | Planned extensions |

The full sequence is documented in
[curriculum/ROADMAP.md](curriculum/ROADMAP.md), with paths and status in
[curriculum/INDEX.md](curriculum/INDEX.md).

## Useful starting points

- [Reliable ingestion](src/main/scala/curriculum02/SparkIngestion.scala):
  explicit raw schema, safe typing, rejection reasons, partitioned Parquet, and
  pruning evidence.
- [Reusable transforms](src/main/scala/curriculum03/OrderTransforms.scala):
  transformation boundaries separated from Spark session and filesystem
  ownership.
- [Join decisions](src/main/scala/curriculum04/OrderEnrichment.scala):
  inner and left joins, duplicate-key diagnostics, and explicit broadcast
  behavior.
- [Persistence decisions](src/main/scala/curriculum05/OrderMetrics.scala):
  shared lineage consumed with and without persistence.
- [JSON event pipeline](src/main/scala/curriculum06/JsonEventPipeline.scala):
  nested landing, typed curation, quarantine, deterministic deduplication, and
  publication.
- [JSON pipeline tests](src/test/scala/curriculum06/JsonEventPipelineSpec.scala):
  nested-data, rejection, window-winner, and file-boundary assertions.

## Project structure

```text
scala-spark-labs/
├── src/main/scala/       # Spark transformations and applications
├── src/test/scala/       # Local Spark and Parquet integration tests
├── data/                 # Small deterministic fixtures
├── curriculum/           # Lab goals and completion criteria
├── learning/             # Progress evidence and active learning state
├── build.sbt
└── project/
```

Generated Spark output is ignored. Tests use controlled in-memory data or
suite-owned temporary directories and bind local Spark networking to
`127.0.0.1`.

## Learning workflow

The repository keeps its learning scaffolding visible. `AGENTS.md` defines the
tutoring and learner-ownership rules, `learning/ACTIVE.md` identifies the
current curriculum, and `learning/tracks/` records concise milestone evidence.
The restart prompt for a new tutoring session lives in
[`prompts/START_SESSION.md`](prompts/START_SESSION.md).

## Selected toolchain

| Component | Version |
| --- | --- |
| Java | Temurin 21.0.11 LTS |
| Scala | 2.13.18 |
| Apache Spark | 4.2.0 |
| sbt | 1.12.11 |

No Python, PySpark, or global Scala installation is required for the completed
core labs.
