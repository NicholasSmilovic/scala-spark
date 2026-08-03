# Active Learning Track

- Active curriculum: `03-spark-testing`
- Status: Milestone 1 is next.
- Milestones: `curriculum/03-spark-testing/MILESTONES.md`
- Progress: `learning/tracks/03-spark-testing.md`
- Planned learner-owned production source: `src/main/scala/curriculum03/OrderTransforms.scala` (not created yet)
- Planned learner-owned test source: `src/test/scala/curriculum03/OrderTransformsSpec.scala` (not created yet)
- Test input: explicit in-memory DataFrames
- Test output: suite-owned temporary directories only
- Test target: the Curriculum 03 suite through `sbt test`
- Last verified evidence: The learner wrote and read typed Parquet outputs, verified nine valid and nine rejected records, observed the four `region=...` directories, and read three North records from the curated base path. The physical plan showed region partition pruning, a three-column Parquet `ReadSchema`, and pushed `unit_price` filters; the learner distinguished durable Parquet storage, partition pruning, and column pruning.
- Immediate teaching goal: Add the test-scoped ScalaTest dependency and establish one correctly managed local Spark test.

Do not edit or replace `src/main/scala/curriculum01/SparkOrders.scala` or `src/main/scala/curriculum02/SparkIngestion.scala`; they are completed curriculum artifacts.
