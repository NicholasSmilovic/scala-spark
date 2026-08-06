# Curriculum 07 Progress — Incremental Pipelines with Delta Lake

- Status: Active; Milestone 1 is next.
- Isolated build: `labs/curriculum07/`
- Planned source: `labs/curriculum07/src/main/scala/curriculum07/IncrementalDeltaPipeline.scala`
- Planned test: `labs/curriculum07/src/test/scala/curriculum07/IncrementalDeltaPipelineSpec.scala`
- Selected runtime: Spark 4.1.0, Delta Lake 4.2.0, Scala 2.13.17, Java 21; local verification remains.
- Compatibility: `curriculum/07-delta-incremental/COMPATIBILITY.md`

| Milestone | Status | Concise verified evidence |
| --- | --- | --- |
| 1. Compatible Delta runtime and transactional table | Not started | Official compatibility review selected an isolated supported runtime; dependency resolution and version-zero table evidence remain. |
| 2. Schema enforcement and deliberate evolution | Not started | — |
| 3. Deterministic idempotent merge/upsert | Not started | — |
| 4. Idempotent bronze/silver/gold pipeline | Not started | — |

## Activation evidence

Activated after Curriculum 06 completion at the learner's explicit request. The current Delta release does not support the completed root Spark 4.2.0 pin, so the local lab must remain isolated.
