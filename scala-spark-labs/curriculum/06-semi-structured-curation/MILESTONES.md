# Curriculum 06 — Semi-Structured JSON and Curated Events

## Purpose

Move beyond flat CSV by ingesting nested JSON safely, parsing timestamps and numeric values, flattening structs while preserving arrays, quarantining malformed or invalid records, and selecting one deterministic event version per key with a window. Publish exact curated and rejected Parquet outputs.

Small file fixtures establish correctness. Realistic scale, Delta Lake, cloud deployment, and performance analysis belong to Curricula 07–10.

## Project boundary

- Preserve all completed Curriculum 01–05 sources and tests.
- Learner-owned production code will live at `src/main/scala/curriculum06/JsonEventPipeline.scala`.
- Learner-owned tests will live at `src/test/scala/curriculum06/JsonEventPipelineSpec.scala`.
- Reuse ScalaTest 3.2.20 and the repository's Java 21 sbt configuration.
- Create one suite-owned `local[2]` Spark session with local networking and the Spark UI disabled.
- Use suite-owned temporary directories for JSON input and Parquet output.
- Model raw events with `event_id`, `account_id`, `event_time`, `amount`, nested `merchant`, `tags`, and `ingest_sequence`.
- Preserve malformed JSON text through Spark's configured corrupt-record column.
- Keep durable sample data under `data/curriculum-06/` only if a later learner-run application needs it.
- Keep result assertions independent of incidental Spark output order.
- Use explicit schemas and the Scala DataFrame API.

Do not introduce Delta Lake, cloud storage, deployment, streaming, generated large datasets, performance benchmarks, Spark UI exercises, skew tuning, machine learning, or production secrets in this curriculum.

## Definition of done for every milestone

- The requested behavior and focused evidence pass.
- Touched code has no unused imports or variables, duplicate declarations, dead scaffolding, obsolete comments, or accidental formatting drift.
- Cleanup is completed inside the behavior milestone rather than deferred to a standalone cleanup milestone.

## Milestone 1 — Land nested JSON with an explicit raw schema

**Teach**

- JSON can carry nested structs and arrays that a flat CSV cannot represent directly.
- An explicit raw schema makes the landing contract reviewable and prevents production inference surprises.
- Permissive parsing plus a named corrupt-record field separates malformed JSON syntax from field-level validation.
- Landing fields as strings where conversion may fail preserves the original evidence for diagnostics.

**Learner actions**

- Create `JsonEventPipeline.scala`.
- Define the complete raw schema, including the nested merchant struct, string tags array, and corrupt-record field.
- Add a reusable JSON reader using permissive mode and the named corrupt-record column.
- Create the Curriculum 06 ScalaTest suite with the established suite-level Spark lifecycle.
- Write valid and malformed JSON lines into a suite-owned temporary input directory.
- Assert exact nested values, array values, and retained corrupt text.

**Completion evidence**

- The focused Curriculum 06 suite passes under Java 21.
- Exact assertions prove the raw contract and malformed-record retention.
- The learner distinguishes syntactically malformed JSON from valid JSON containing invalid business fields.

## Milestone 2 — Curate typed rows and quarantine invalid records

**Teach**

- `try_to_timestamp` with an explicit format and safe numeric conversion turn raw strings into typed columns without discarding diagnostic evidence or throwing under ANSI mode.
- In Scala, `DataFrame` is a type alias for `Dataset[Row]`: a DataFrame uses generic schema-backed rows, while `Dataset[T]` can represent typed JVM values through an encoder. The relationship is core orientation; case-class Dataset programming is optional depth.
- Struct fields can be projected into curated columns while arrays can remain arrays when their repeated structure is meaningful.
- `split` creates an array from a delimited string, while `explode` expands one array element per output row and therefore changes cardinality; neither is needed here because `tags` already arrives as an array and the curated contract preserves it.
- Syntactically valid JSON can still fail required-field, timestamp, amount, or nested-field rules.
- A rejected-data contract should retain the identifying raw values and a deterministic rejection reason.

**Learner actions**

- Parse `event_time` with one explicit format and convert `amount` to the selected fixed-precision decimal type.
- Flatten merchant ID and name while preserving `tags`.
- Separate valid curated rows from rejected rows, including malformed JSON and field-level failures.
- Assert the exact curated schema, exact valid rows, and exact rejection reasons.

**Completion evidence**

- The focused suite passes with exact valid/rejected results.
- The learner explains why parsing and validation happen after landing rather than relying on inference or silently dropping bad rows.
- The learner explains why the Scala `DataFrame` values in this pipeline are `Dataset[Row]` values and distinguishes that from an optionally typed `Dataset[T]`.

## Milestone 3 — Deduplicate deterministically and publish the final contracts

**Teach**

- Duplicate event IDs can represent repeated delivery or corrected versions rather than exact duplicate rows.
- `row_number` over a window can select one version while retaining the full chosen row.
- Window partitioning defines the event key; descending event time and ingest sequence define the winner.
- A complete tie-break contract is required for deterministic reruns.
- The observable contract of a write is the resulting durable dataset, not a returned DataFrame.
- Curated and rejected outputs serve different consumers and should remain separate.
- Rereading output verifies schema and values across the file boundary; this is integration evidence for the new pipeline, not a separate Spark concept.

**Learner actions**

- Add a reusable deduplication transformation over curated rows.
- Partition by `event_id` and order by event timestamp then ingest sequence, both descending.
- Retain only row number one and remove the helper rank from the public output.
- Assert exact winners for repeated event IDs, including a same-timestamp ingest-sequence tie.
- Print the formatted plan and identify the window partition, complete ordering, exchange, and sort.
- Write deduplicated curated events and rejected events to separate suite-owned Parquet paths in a deliberate rerunnable mode.
- Reread both outputs and assert exact schemas and order-independent rows.
- Run the full Curriculum 06 suite and complete the milestone cleanup review.

**Completion evidence**

- The focused suite passes with exact deterministic winners.
- Learner-supplied plan evidence shows the event-key window and full tie-break order.
- The learner explains why input order is not a valid deduplication rule.
- The full Curriculum 06 suite passes with exact file-boundary evidence.
- Curated and rejected Parquet outputs are isolated and rerunnable.
- The learner explains the landing, validation, deterministic deduplication, and publication boundaries.
- The touched production and test code passes its cleanup review.

## Completion boundary

Curriculum 06 completes nested JSON landing, corrupt-record retention, timestamp and decimal curation, struct/array handling, diagnostic quarantine, deterministic window-based deduplication, and Parquet publication. It also completes the practical core Scala/DataFrame Spark fundamentals track.

Stop there unless the learner explicitly chooses an optional professional extension. Curriculum 07 owns Delta Lake tables and incremental processing; it must not auto-activate.
