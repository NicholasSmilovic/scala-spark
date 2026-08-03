# Curriculum 02 Progress — Reliable Ingestion and Partitions

- Status: Complete; Milestones 1–4 verified.
- Source: `src/main/scala/curriculum02/SparkIngestion.scala`
- Data: `data/curriculum-02/incoming/`
- Output: `output/curriculum-02/`

| Milestone | Status | Concise verified evidence |
| --- | --- | --- |
| 1. Explicit multi-file landing | Complete | Java 21 run of only `curriculum02.SparkIngestion`; explicit eight-string schema; three input files; eighteen rows. Learner explained explicit schema versus inference and recognized that raw `StringType` fields have not enforced business typing. |
| 2. Type, validate, quarantine | Complete | Safely typed and trimmed raw columns, assigned first-match diagnostic rejection reasons, and verified nine valid plus nine rejected rows. Learner explained `col` versus `lit`, lazy plans, and why separate uncached actions may repeat shared CSV reading and validation. |
| 3. Observe/control partitions | Complete | Observed three file partitions and distinguished partitions, tasks, and cores; compared round-robin `repartition(6)` with `Exchange` against uneven `coalesce(2)` without one. Justified a region-key shuffle for the lab while identifying its low-cardinality scaling limit; the executed adaptive plan retained the `Exchange` and showed a nine-row shuffle coalesced at runtime. |
| 4. Partitioned Parquet layer | Complete | Wrote typed valid records partitioned into four `region=...` directories and diagnosable rejected records to a separate Parquet path. Read the curated base path back with its typed schema, returned three North records, and verified nine rejected records; the plan showed region `PartitionFilters`, a pruned three-column `ReadSchema`, and pushed price filters. Learner distinguished durable Parquet from cache and partition pruning from column pruning. |

## Current blocker

None.

## Record-keeping rule

Keep at most a few evidence bullets per milestone. Record a mistake only when it changes the teaching plan or reveals a reusable misconception; do not reproduce terminal logs or conversation history.
