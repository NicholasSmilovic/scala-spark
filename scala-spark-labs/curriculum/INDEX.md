# Curriculum Index

This repository is a sequence of small Scala/Spark labs. Each curriculum owns one milestone file, one compact progress record, and a source namespace. Data and output locations are persistent only when the curriculum needs them; testing curricula use in-memory fixtures and isolated temporary output.

The topic and ordering contract is defined in `curriculum/ROADMAP.md`.

| ID | Curriculum | Status | Milestones | Progress |
| --- | --- | --- | --- | --- |
| 01 | Orders foundations | Complete | `curriculum/01-orders-foundations/MILESTONES.md` | `learning/tracks/01-orders-foundations.md` |
| 02 | Reliable ingestion and partitions | Complete | `curriculum/02-ingestion-partitions/MILESTONES.md` | `learning/tracks/02-ingestion-partitions.md` |
| 03 | Spark testing and reusable transformations | Complete | `curriculum/03-spark-testing/MILESTONES.md` | `learning/tracks/03-spark-testing.md` |
| 04 | Join correctness, cardinality, and broadcast decisions | Complete | `curriculum/04-join-cardinality-broadcast/MILESTONES.md` | `learning/tracks/04-join-cardinality-broadcast.md` |
| 05 | Caching, persistence, and reuse decisions | Complete | `curriculum/05-cache-persistence-reuse/MILESTONES.md` | `learning/tracks/05-cache-persistence-reuse.md` |
| 06 | Semi-structured JSON and curated events | Complete — final core | `curriculum/06-semi-structured-curation/MILESTONES.md` | `learning/tracks/06-semi-structured-curation.md` |
| 07 | Incremental pipelines with Delta Lake | Active — optional extension | `curriculum/07-delta-incremental/MILESTONES.md` | `learning/tracks/07-delta-incremental.md` |
| 08 | First Azure Databricks Scala deployment | Optional | `curriculum/08-azure-databricks-deployment/MILESTONES.md` | `learning/tracks/08-azure-databricks-deployment.md` |
| 09 | Real distributed execution evidence | Optional | `curriculum/09-distributed-execution/MILESTONES.md` | `learning/tracks/09-distributed-execution.md` |
| 10 | Production reliability capstone | Optional | `curriculum/10-production-reliability/MILESTONES.md` | `learning/tracks/10-production-reliability.md` |

## Scaling conventions

- Scala for curriculum `NN` lives under package/directory `curriculumNN`; completed source is not overwritten by later labs.
- When dependency compatibility requires isolation, an optional curriculum may use its own build under `labs/curriculumNN/`; Curriculum 07 uses this exception to preserve the completed Spark 4.2 root.
- Persistent input data lives under `data/curriculum-NN/` when required.
- Normal generated output lives under `output/curriculum-NN/` and remains ignored by Git; automated tests use suite-owned temporary directories.
- `learning/PROFILE.md` contains only durable cross-curriculum teaching information.
- `learning/OPEN_LOOPS.md` contains only unresolved concepts worth revisiting.
- `learning/tracks/NN-*.md` contains concise milestone evidence, not a transcript.
- Detailed completed records may be retained under `learning/archive/`, but tutors do not read that directory by default.
- Git history is the detailed change log. Markdown learning records should not duplicate every command or conversation.

When a curriculum is complete, compress its progress file to the evidence needed for future teaching and leave prior source/data untouched. Mark another curriculum active in `learning/ACTIVE.md` only when the learner requests one.

Do not rename, renumber, reorder, or replace Curricula 06–10 without an explicit learner request to redesign the locked roadmap.
