# Curriculum 06 Progress — Semi-Structured JSON and Curated Events

- Status: Complete; Milestones 1–3 verified.
- Production source: `src/main/scala/curriculum06/JsonEventPipeline.scala`
- Test source: `src/test/scala/curriculum06/JsonEventPipelineSpec.scala`
- Test input/output: suite-owned temporary JSON and Parquet paths.

| Milestone | Status | Concise verified evidence |
| --- | --- | --- |
| 1. Explicit nested JSON landing | Complete | Focused suite passes with exact schema, nested merchant, tags array, and retained malformed text; corrupt-record destination and parsing-versus-validation boundary established. |
| 2. Typed curation and quarantine | Complete | Focused suite proves safe conversions under ANSI mode, exact typed curated and raw rejected schemas/rows, flattened merchant fields, preserved tags, and file-backed coverage of all eight deterministic business reasons; learner distinguishes typed/lazy Dataset values from local action results. |
| 3. Deterministic deduplication and publication | Complete | Exact timestamp/sequence winners and plan evidence prove the window contract; full suite passes after two overwrite-mode writes, and separate curated/rejected Parquet rereads preserve exact schemas and rows, including typed winners, tags, raw invalid values, corrupt text, and reasons. |

## Current blocker

None.

## Record-keeping rule

Record only learner-supplied compile/test output, formatted or extended plan evidence, exact result evidence, and explanations that satisfy a milestone gate. Do not treat prepared curriculum files or unexecuted code as evidence.
