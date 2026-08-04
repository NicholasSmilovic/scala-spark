# Active Learning Track

- Active curriculum: `04-join-cardinality-broadcast`
- Status: Milestone 1 is next.
- Milestones: `curriculum/04-join-cardinality-broadcast/MILESTONES.md`
- Progress: `learning/tracks/04-join-cardinality-broadcast.md`
- Planned learner-owned production source: `src/main/scala/curriculum04/OrderEnrichment.scala` (not created yet)
- Planned learner-owned test source: `src/test/scala/curriculum04/OrderEnrichmentSpec.scala` (not created yet)
- Test input: explicit in-memory order and product DataFrames
- Persistent input/output: none
- Test target: the Curriculum 04 suite through `sbt test`
- Last verified evidence: Curriculum 03 complete under Java 21 with four passing tests covering lifecycle, reusable normalization/validation, exact order-independent assertions, and isolated Parquet write/read/cleanup. The learner understands bounded `collect()` results as driver-memory Scala collections.
- Immediate teaching goal: Write and test one lazy inner product-enrichment join that retains only matched product IDs.

Do not edit or replace completed Curriculum 01–03 sources or tests.
