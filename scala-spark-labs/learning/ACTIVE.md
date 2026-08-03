# Active Learning Track

- Active curriculum: `03-spark-testing`
- Status: Complete. Awaiting learner request for the next curriculum.
- Milestones: `curriculum/03-spark-testing/MILESTONES.md`
- Progress: `learning/tracks/03-spark-testing.md`
- Learner-owned production source: `src/main/scala/curriculum03/OrderTransforms.scala`
- Learner-owned test source: `src/test/scala/curriculum03/OrderTransformsSpec.scala`
- Test input: explicit in-memory DataFrames
- Test output: suite-owned temporary directories only
- Test target: the Curriculum 03 suite through `sbt test`
- Last verified evidence: Curriculum 03 complete: under Java 21, four tests pass for Spark lifecycle, normalization/typing, the full validation contract, exact split/curated results, and isolated Parquet write/read/cleanup. The learner contrasted lazy `DataFrame` transformations with a `Unit` filesystem side effect.
- Immediate teaching goal: None. Activate another curriculum only when the learner requests one.

Do not edit or replace `src/main/scala/curriculum01/SparkOrders.scala` or `src/main/scala/curriculum02/SparkIngestion.scala`; they are completed curriculum artifacts.
