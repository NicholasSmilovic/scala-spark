# Curriculum 03 Progress — Spark Testing and Reusable Transformations

- Status: Complete.
- Planned production source: `src/main/scala/curriculum03/OrderTransforms.scala`
- Planned test source: `src/test/scala/curriculum03/OrderTransformsSpec.scala`
- Persistent input data: none; tests use explicit in-memory fixtures.
- Persistent output: none; file tests use suite-owned temporary directories.

| Milestone | Status | Concise verified evidence |
| --- | --- | --- |
| 1. Spark test lifecycle | Complete | ScalaTest 3.2.20 is test-scoped; repository-local sbt settings selected Java 21.0.11, and `testOnly curriculum03.OrderTransformsSpec` reported exactly one passing test plus successful Spark shutdown. Learner explained test versus `main` execution and cleanup. |
| 2. Normalization and typing tests | Complete | Passing assertions prove trimming, safe integer/decimal/date conversion, retained malformed raw strings with null typed results, and integer/decimal/date schema types. Learner explained lazy plan construction versus execution by `collect()`. |
| 3. Validation contract tests | Complete | Three passing tests prove all rejection messages, quantity-before-region precedence, exact valid/rejected identifier sets, curated typed IDs, and valid-plus-rejected accounting. Learner explained that retained raw `"two"` identifies why safe parsing produced an otherwise ambiguous typed null. |
| 4. Isolated Parquet boundary test | Complete | Four-test suite writes only beneath a suite-owned temporary path, reads exact curated/rejected tuples back, verifies curated types and North/South partition directories, and guarantees cleanup. Learner contrasted a lazy `DataFrame => DataFrame` method with a `Unit` write verified through filesystem effects. |

## Current blocker

None. Curriculum complete.

## Record-keeping rule

Record only learner-supplied compile/test output and explanations that satisfy a milestone gate. Do not treat prepared curriculum files or unexecuted test code as evidence.
