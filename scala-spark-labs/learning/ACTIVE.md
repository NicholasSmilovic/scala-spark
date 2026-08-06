# Active Learning Track

- Active curriculum: `07-delta-incremental`
- Status: Milestone 1 is next; official compatibility review complete, local runtime verification remains.
- Track boundary: Curriculum 07 is an optional professional extension; Curriculum 08 must not auto-activate.
- Milestones: `curriculum/07-delta-incremental/MILESTONES.md`
- Compatibility record: `curriculum/07-delta-incremental/COMPATIBILITY.md`
- Progress: `learning/tracks/07-delta-incremental.md`
- Isolated build root: `labs/curriculum07/` (not created yet)
- Planned learner-owned production source: `labs/curriculum07/src/main/scala/curriculum07/IncrementalDeltaPipeline.scala`
- Planned learner-owned test source: `labs/curriculum07/src/test/scala/curriculum07/IncrementalDeltaPipelineSpec.scala`
- Test input/output: suite-owned temporary Delta table paths
- Persistent input/output: none required for automated tests
- Test target: the Curriculum 07 isolated suite through `cd labs/curriculum07 && sbt test`
- Last verified evidence: Curriculum 06 and the core track are complete. Official Delta 4.2.0 evidence supports Spark 4.1.0/4.0.1 rather than the root Spark 4.2.0, so Curriculum 07 selected an isolated Spark 4.1.0 / Delta 4.2.0 / Scala 2.13.17 / Java 21 runtime.
- Immediate teaching goal: Create and locally verify the isolated build, then prove an exact version-zero Delta table and transaction history.

Do not edit or replace completed Curriculum 01–06 sources, tests, or root runtime pins.
