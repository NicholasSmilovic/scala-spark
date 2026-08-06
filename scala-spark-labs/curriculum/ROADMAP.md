# Locked Curriculum Roadmap

This roadmap separates the core batch Spark foundation from optional professional data-engineering extensions. Curriculum 06 is the final core curriculum. Curricula 07–10 do not auto-activate and are not required to claim practical Scala/DataFrame Spark fundamentals.

Curriculum topics, order, and completion boundaries do not change unless the learner explicitly requests a redesign. Tutors may refine fixture values, current dependency pins, and verification commands when software compatibility changes, but must not silently replace or renumber a curriculum.

| ID | Curriculum | Durable outcome | Status |
| --- | --- | --- | --- |
| 01 | Orders foundations | Core Scala/Spark DataFrame and execution model | Complete |
| 02 | Reliable ingestion and partitions | Typed CSV curation, rejected records, Parquet, and partition evidence | Complete |
| 03 | Spark testing | Reusable transformations and isolated file-boundary tests | Complete |
| 04 | Join correctness and broadcast decisions | Cardinality, unmatched rows, duplicates, and join plans | Complete |
| 05 | Caching, persistence, and reuse | Materialization, cached scans, cleanup, and cache decisions | Complete |
| 06 | Semi-structured JSON and curated events | Nested JSON, timestamps, arrays/structs, quarantine, deterministic deduplication, and Parquet | Complete — final core |
| 07 | Incremental pipelines with Delta Lake | Delta transaction log, schema controls, merge/upsert, idempotency, and bronze/silver/gold tables | Active — optional extension |
| 08 | First Azure Databricks Scala deployment | Tested Scala JAR, Declarative Automation Bundle, isolated cloud storage, and a repeatable job run | Optional |
| 09 | Real distributed execution evidence | Jobs/stages/tasks, executor memory, file sizing, AQE, skew, and failure evidence on the six-million-row dataset | Optional |
| 10 | Production reliability capstone | Configuration, secrets, quality gates, retries, backfills, observability, scheduling, CI/CD, and cost controls | Optional |

## Core completion boundary

After Curriculum 06, the learner has the practical core batch foundation: lazy execution, DataFrames, schemas, expressions, aggregation, partitions/shuffles, file formats, testing, joins, broadcast, persistence, nested data, timestamps, malformed-data handling, deterministic deduplication, window basics, and curated output.

Scala language mastery, Structured Streaming, Delta Lake, cloud deployment, cluster operations, and CI/CD are separate goals. They are valuable, but they are not prerequisites for completing the Spark fundamentals track.

## Sequence rationale

- Curriculum 06 closes the core track by broadening source and schema complexity locally before any table format or cloud platform is added.
- Curriculum 07 adds incremental table semantics only after curated event correctness exists.
- Curriculum 08 packages and deploys the tested pipeline only after local table behavior is proven.
- Curriculum 09 uses the deployed environment and realistic dataset to observe actual distributed behavior rather than infer it from tiny fixtures.
- Curriculum 10 turns the working pipeline into an operable, repeatable production-style system.

## Compatibility gates

- Curriculum 07 must verify an officially supported Spark/Delta pair before changing dependencies. If the completed root Spark version is not supported, use an isolated curriculum-scoped build or an explicitly verified compatible runtime; do not silently force incompatible artifacts or rewrite completed curricula.
- Curriculum 08 uses the current Azure Databricks term **Declarative Automation Bundles** (formerly Databricks Asset Bundles). Workspace, Unity Catalog volume, compute, CLI, authentication, and permissions are explicit prerequisites.
- Curricula 08–10 use isolated learner-approved development resources. They do not target production or shared non-test data by default.

## Optional specializations

Choose when they match the learner's goals. The production extension normally follows Curricula 07–10 in order, but completing it is optional after the Curriculum 06 core boundary.

- Structured Streaming with Event Hubs or Kafka
- JDBC ingestion from PostgreSQL or Azure SQL
- External orchestration
- Advanced Delta optimization and governance
- Machine learning and imbalanced-label evaluation
