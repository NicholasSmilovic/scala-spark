# Curriculum 03 — Spark Testing and Reusable Transformations

## Purpose

Turn the validation behavior from Curriculum 02 into reusable DataFrame transformations with focused automated tests. Learn which checks belong in fast in-memory transformation tests, which require a real Spark action, and how to test Parquet output without touching the project's normal data or output directories.

The tests use Spark locally because DataFrame expressions are planned and executed by Spark; they are not mocks of Spark behavior. The datasets remain deliberately small, and elapsed time is not used as evidence of production performance.

## Project boundary

- Preserve the completed Curriculum 01 and Curriculum 02 source files.
- Learner-owned production code will live at `src/main/scala/curriculum03/OrderTransforms.scala`.
- Learner-owned tests will live at `src/test/scala/curriculum03/OrderTransformsSpec.scala`.
- Use ScalaTest 3.2.20 in sbt's `Test` configuration.
- Use in-memory DataFrames for transformation tests.
- Use suite-owned temporary directories for file tests; do not write test artifacts under `data/` or `output/`.
- Continue using Java 21, Scala 2.13.18, Spark 4.2.0, and sbt 1.12.11.

Do not introduce mocks, property-based testing, test containers, external databases, cloud storage, streaming, CI configuration, or performance benchmarks in this curriculum.

## Milestone 1 — Establish a real Spark test lifecycle

**Teach**

- The role of a test framework compared with `sbt`, Spark, and application code.
- sbt's `Test` configuration and why a test-only dependency is not packaged as an application dependency.
- A ScalaTest suite, named test, assertion, and failure report.
- Why a suite should create one local `SparkSession`, reuse it across its tests, and stop it afterward.
- Why `local[2]` is enough to exercise Spark behavior without treating the test as a cluster benchmark.

**Learner actions**

- Add ScalaTest as a test-scoped dependency.
- Create the Curriculum 03 test source in the new package.
- Establish suite-level Spark setup and cleanup with local networking and the Spark UI disabled.
- Write one smoke test using a tiny in-memory DataFrame and a known action result.
- Run only the test suite through sbt.

**Completion evidence**

- The test source compiles and the test command succeeds under Java 21.
- Exactly one intentional smoke test is reported as passing.
- The learner distinguishes `sbt test` from running a `main` method.
- The learner explains why stopping the suite's Spark session matters.

Do not create the order transformation or test file I/O yet.

## Milestone 2 — Test normalization and safe typing in memory

**Teach**

- Separating transformation logic from `main`, file paths, printing, and Spark lifecycle management.
- A reusable Scala method that accepts a `DataFrame` and returns a transformed `DataFrame`.
- Test fixtures as deliberately small inputs, not samples intended to estimate performance.
- Why an explicit raw fixture schema is useful when nulls and malformed strings are part of the test.
- When a transformation test actually executes: constructing the result is lazy; collecting or counting it is the action.

**Learner actions**

- Create `OrderTransforms.scala` without changing either completed application.
- Add one transformation method for trimming and safe type conversion.
- Build a small raw DataFrame inside the test suite with the same string-based input contract used by Curriculum 02.
- Assert representative cleaned values, typed values, and malformed-value nulls.
- Assert the important resulting Spark data types.

**Completion evidence**

- Tests prove that surrounding whitespace is removed.
- A valid numeric/date string becomes the expected typed value.
- A malformed value such as a word in a numeric field remains diagnosable through a null typed result while its raw value remains available.
- The learner explains that the production method adds a lazy plan and that the test action executes it.

Do not add file reads/writes or the complete rejection contract yet.

## Milestone 3 — Prove the validation contract without order-dependent tests

**Teach**

- Testing business rules separately from parsing mechanics.
- Table-driven fixture rows: one valid baseline plus focused invalid variations.
- Exact result assertions keyed by stable business identifiers.
- Why DataFrame row order is not guaranteed unless explicitly ordered.
- First-match rejection precedence when one row violates more than one rule.
- Count assertions as accounting checks, not sufficient proof of individual outcomes.

**Learner actions**

- Extend the reusable transformations to assign rejection reasons and produce valid, rejected, and curated DataFrames.
- Cover malformed and non-positive numbers, missing required text, invalid region/status, and invalid date inputs.
- Assert the expected rejection reason for each invalid fixture.
- Assert the exact valid and rejected identifiers without depending on incidental partition order.
- Verify that valid plus rejected equals the total fixture count.

**Completion evidence**

- Every rule in the Curriculum 02 validation contract has focused test evidence.
- One deliberately multi-invalid row proves the documented first-match rejection rule.
- Tests verify exact identifiers/reasons rather than only counts or printed output.
- The learner explains why retaining raw strings makes parsing failures testable and diagnosable.

Do not test timing, shuffle counts, or generated file counts.

## Milestone 4 — Test the Parquet boundary in an isolated directory

**Teach**

- The difference between a transformation test and a filesystem integration test.
- Passing an output base path into production code instead of hard-coding a project path.
- A write method returning `Unit`: the observable result is the filesystem side effect or thrown failure.
- Suite-owned temporary directories, cleanup, and independence between test runs.
- What is stable to assert for Spark output: read-back rows, schema, rejection reasons, and partition-directory values.
- Why exact Parquet filenames and file counts are unstable implementation details.

**Learner actions**

- Put curated and rejected Parquet writes behind a method that receives its base output path.
- Write a known fixture result beneath a newly created temporary directory.
- Read both paths back with Spark and assert their rows and important schema fields.
- Verify the expected region partition-directory values without asserting exact part filenames.
- Ensure temporary output is cleaned even if an assertion fails.

**Completion evidence**

- The filesystem test writes only beneath its suite-owned temporary directory.
- Curated and rejected read-back results match the known fixture.
- The curated schema retains the intended integer, decimal, and date types.
- Region partition directories are verified without relying on a particular number of Spark output files.
- The learner contrasts a `DataFrame => DataFrame` transformation with a write method returning `Unit`.

## Completion boundary

Curriculum 03 completes focused Spark transformation and local file-boundary testing. Stop there. Use later curricula for joins and broadcast decisions, caching/persistence, CI, property-based testing, shared testing libraries, cluster deployment, or production table formats.
