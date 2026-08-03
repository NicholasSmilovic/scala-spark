# Scala/Spark Learning Labs

This repository contains a sequence of small, gated Scala/Spark curricula. Curricula 01 and 02 are complete, and Curriculum 03 is the active Spark-testing lab.

The learner owns the keyboard: you run the commands and edit the Scala file. Codex acts as the Lead Spark Tutor, gives one step at a time, and checks your evidence before advancing.

## Start a learning session

1. Open a new Codex task with this workspace.
2. Copy the prompt from [prompts/START_SESSION.md](prompts/START_SESSION.md).
3. Paste it as the first message.

The durable teaching rules live in `AGENTS.md`. `learning/ACTIVE.md` points to the current curriculum, while the compact learner profile and open loops carry only information useful across curricula. Detailed completed records are archived and are not loaded during normal startup.

## Curricula

| ID | Curriculum | Status |
| --- | --- | --- |
| 01 | Orders foundations: toolchain, DataFrames, lazy transformations, aggregation, and shuffle plans | Complete |
| 02 | Reliable ingestion: explicit schemas, dirty-data quarantine, partitions, shuffles, and Parquet pruning | Complete |
| 03 | Spark testing: reusable transformations and isolated file-boundary tests | In progress |

See [curriculum/INDEX.md](curriculum/INDEX.md) for the active paths. The one-step-at-a-time teaching loop is defined in `AGENTS.md`.

## Source and data isolation

- [SparkOrders.scala](src/main/scala/curriculum01/SparkOrders.scala) is the completed Curriculum 01 artifact and reads from `data/curriculum-01/`.
- [SparkIngestion.scala](src/main/scala/curriculum02/SparkIngestion.scala) is the completed Curriculum 02 artifact and owns `data/curriculum-02/` plus `output/curriculum-02/`.
- Curriculum 03 uses the `curriculum03` source and test namespaces, in-memory fixtures, and suite-owned temporary output.
- Each curriculum receives a numbered source namespace, milestone file, and concise progress file; persistent data and normal output are added only when needed.
- Generated output remains under `output/` and is ignored by Git.

## Selected toolchain

| Component | Version | Reason |
| --- | --- | --- |
| Java | Temurin 21.0.11 LTS | Already installed and supported by Spark 4.2.0. |
| Scala | 2.13.18 | Matches Spark's Scala 2.13 line and supports Java 21. |
| Apache Spark | 4.2.0 | Stable Spark release selected for the Scala DataFrame API. |
| sbt | 1.12.11 | Pinned in `project/build.properties`; the learner reports sbt is installed. |

Maven exists on the machine but is not used for this lab. No global Scala installation, Python, PySpark, Python virtual environment, or Python dependencies are required.

## Files

- `AGENTS.md`: durable tutor, learner-ownership, and milestone rules.
- `prompts/START_SESSION.md`: the prompt to paste into each new learning task.
- `curriculum/INDEX.md`: curriculum registry and scaling conventions.
- `curriculum/NN-*/MILESTONES.md`: gated outcomes for one lab.
- `learning/ACTIVE.md`: current curriculum pointer and immediate state.
- `learning/PROFILE.md`: durable teaching context.
- `learning/OPEN_LOOPS.md`: unresolved concepts only.
- `learning/tracks/`: concise per-curriculum evidence.
- `learning/archive/`: historical detail, read only when needed.
- `src/main/scala/`: completed and active learner-owned applications.
- `data/curriculum-NN/`: isolated lab input.
- `build.sbt` and `project/build.properties`: the pinned Scala, Spark, and sbt build.
