# Active Learning Track

- Active curriculum: `04-join-cardinality-broadcast`
- Status: Complete.
- Milestones: `curriculum/04-join-cardinality-broadcast/MILESTONES.md`
- Progress: `learning/tracks/04-join-cardinality-broadcast.md`
- Learner-owned production source: `src/main/scala/curriculum04/OrderEnrichment.scala`
- Learner-owned test source: `src/test/scala/curriculum04/OrderEnrichmentSpec.scala`
- Test input: explicit in-memory order and product DataFrames
- Persistent input/output: none
- Test target: the Curriculum 04 suite through `sbt test`
- Last verified evidence: The learner reported the full Curriculum 04 suite passing. Exact assertions cover inner and left join contracts, unmatched right-side nulls, duplicate-key row multiplication, duplicate-key diagnostics, and equivalent unhinted/broadcast results. Learner-supplied plans show a two-exchange `SortMergeJoin` versus a product-side `BroadcastExchange` and `BroadcastHashJoin Inner BuildRight`; the learner explained that the small products side is copied into a worker-local lookup while the orders side avoids a join shuffle.
- Immediate teaching goal: Curriculum boundary reached. Keep Curriculum 04 complete and activate another curriculum only when the learner requests one.

Do not edit or replace completed Curriculum 01–04 sources or tests.
