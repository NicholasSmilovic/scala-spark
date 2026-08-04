# Active Learning Track

- Active curriculum: `06-semi-structured-curation`
- Status: Milestone 1 is next.
- Track boundary: Curriculum 06 is the final core Spark curriculum; Curricula 07–10 are optional and must not auto-activate.
- Milestones: `curriculum/06-semi-structured-curation/MILESTONES.md`
- Progress: `learning/tracks/06-semi-structured-curation.md`
- Planned learner-owned production source: `src/main/scala/curriculum06/JsonEventPipeline.scala` (not created yet)
- Planned learner-owned test source: `src/test/scala/curriculum06/JsonEventPipelineSpec.scala` (not created yet)
- Test input/output: suite-owned temporary nested JSON and Parquet paths
- Persistent input/output: none required for automated tests
- Test target: the Curriculum 06 suite through `sbt test`
- Last verified evidence: Curriculum 05 complete with exact uncached/cached behavior, cached-plan evidence, safe blocking cleanup, recomputation after unpersist, and reuse/cost/memory-based cache decisions.
- Immediate teaching goal: Define the complete raw nested-event schema and prove that valid JSON and syntactically malformed JSON are both retained under an explicit landing contract.

Do not edit or replace completed Curriculum 01–05 sources or tests.
