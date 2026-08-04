# Curriculum 04 Progress — Join Correctness, Cardinality, and Broadcast Decisions

- Status: Complete.
- Production source: `src/main/scala/curriculum04/OrderEnrichment.scala`
- Test source: `src/test/scala/curriculum04/OrderEnrichmentSpec.scala`
- Persistent input: none; tests use explicit in-memory order and product fixtures.
- Persistent output: none.

| Milestone | Status | Concise verified evidence |
| --- | --- | --- |
| 1. Inner enrichment join | Complete | Learner-reported focused suite pass; compatible integer `product_id` keys; exact order-independent enriched rows for orders 101 and 102; unmatched order 103 excluded. Learner explained lazy plan construction, `collect()` execution into driver memory, shared-key join semantics, and explicit projection. |
| 2. Left join and unmatched references | Complete | Learner-reported suite pass; exact IDs prove orders 101–103 are preserved, matched product attributes remain correct, unmatched order 103 retains `product_id = 999` with null right-side attributes, and joined/input counts agree. Learner identified the nulls as the consequence of an absent right-side match. |
| 3. Duplicate-key cardinality | Complete | Learner-reported suite pass; exact rows and multiplicity map prove a duplicated product ID multiplies order 101, while the lazy diagnostic reports product 10 with occurrence count 2. Learner explained matching combinations (`1×2=2`, `2×2=4`) and why arbitrary deduplication or product-name joins cannot repair an untrustworthy reference key. |
| 4. Shuffle and broadcast plans | Complete | Learner-reported full suite pass proves exact equivalence between unhinted and explicitly broadcast results. Learner-supplied plans show `SortMergeJoin` with hash-partitioning exchanges on both inputs versus `BroadcastHashJoin Inner BuildRight` with a product-side `BroadcastExchange` and no orders-side join exchange. Learner explained the right/products build-side lookup, large-side streaming, and the requirement that broadcast size be safe for network distribution and worker memory. |

## Current blocker

None. Curriculum 04 is complete.

## Record-keeping rule

Record only learner-supplied compile/test output, physical-plan output, and explanations that satisfy a milestone gate. Do not treat prepared curriculum files or unexecuted code as evidence.
