# Archived Session Log — Curriculum 01

Session entries are appended after meaningful work is verified.

## 2026-07-31 — Milestone 1 setup

- Work completed: Inspected the empty repository and local toolchain; created the Scala/sbt project, sample CSV, application, documentation, and learning records.
- Commands run: Java, javac, Scala, sbt, Maven, macOS, Git, and local-cache inspection commands.
- Errors encountered: Scala and sbt were absent. Maven selected Homebrew Java 26 even though the shell default was Temurin Java 21. The proposed project-local sbt download was declined.
- Fixes made: Pinned Java 21, Scala 2.13.18, Spark 4.2.0, and sbt 1.12.11 in the project documentation/build. The learner will install sbt and perform the run.
- Concepts discussed: Toolchain compatibility and the distinction between completing code and demonstrating learner understanding.
- My answers to comprehension questions: None yet.
- Corrections made: The attached PySpark brief was superseded by the explicit Scala-only request; no Python files or dependencies were created.
- Next recommended step at that time: Run the completed application. This was superseded by the curriculum refactor below.

## 2026-07-31 — Agent-led curriculum refactor

- Work completed: Reframed the repository as four gated learning sessions; reset the Scala application to the minimal environment milestone; added durable tutoring rules, curriculum, and a reusable new-task prompt.
- Commands run: Documentation lookup and read-only project inspection. The Spark application was intentionally not run.
- Errors encountered: The Codex manual helper could not resolve the official documentation host from the sandbox.
- Fixes made: Kept agent behavior in the repository instructions and starter prompt; made subagents optional rather than part of every lesson.
- Concepts discussed: Separation between a finished code deliverable and a learner-owned, evidence-based teaching sequence.
- My answers to comprehension questions: None yet.
- Corrections made: The learner will type commands and Scala changes; one tutor teaches and checks progress, with specialists used only when needed.
- Next recommended step: Open a new task with the prompt in `prompts/START_SESSION.md` and begin Milestone 1.

## 2026-07-31 — Milestone 1 learner run

- Work completed: The learner verified active Temurin Java 21.0.11, inspected the selected sbt/Scala/Spark versions, demonstrated the required introductory concepts, and ran the minimal Spark application.
- Commands run by the learner: `java -version` and `sbt run`.
- Verified application output: Spark 4.2.0, `Master: local[*]`, successful sbt completion, and return to the shell prompt.
- Issue found: Spark reported Java 26.0.1 inside the forked application even though the shell reported Java 21.0.11.
- Resolution: The learner reran with `JAVA_HOME="$(/usr/libexec/java_home -v 21)" sbt run`. sbt and Spark both reported Java 21.0.11, Spark printed version 4.2.0 and `local[*]`, and the command completed successfully.
- Current status: Milestone 1 is complete. Advance to Milestone 2.

## 2026-07-31 — Milestones 2 and 3 learner implementation

- Work completed: The learner wrote the CSV reader, enabled headers and schema inference, printed the schema, displayed the source rows, filtered cancelled and non-positive-quantity orders, and added per-row revenue.
- Commands run by the learner: Java-21-prefixed `sbt run` invocations.
- Verified Milestone 2 evidence: Seven expected columns with inferred integer/double/string types; twelve source rows displayed; learner explained the default full inference scan and `show()` as the first visible data action.
- Verified Milestone 3 evidence: Eight valid rows; orders `1004`, `1006`, `1007`, and `1009` absent; correct revenue values; learner calculated order `1010` revenue as `300.00` and explained that filters/`withColumn` build a lazy plan executed by `show()`.
- Teaching adjustments: Use direct saved-file inspection, concrete learner-written Scala, PHP/Laravel/npm comparisons, coherent behavior-sized steps, and no untaught quizzes or throwaway code.
- Current status: Milestones 2 and 3 are complete. Advance to Milestone 4.

## 2026-07-31 — Milestone 4 regional summary and plan

- Work completed: The learner grouped valid orders by region, calculated total revenue and row count in one aggregation, sorted total revenue descending, displayed the result, and printed the formatted physical plan.
- Verified output: East `770.0/2`, South `380.0/2`, West `340.0/2`, and North `240.0/2`.
- Learner-owned correction: A second standalone `agg(count("*"))` initially counted the four already-aggregated regional rows globally. The learner changed it to a second expression in the grouped `agg`, producing two orders per region.
- Plan evidence: The learner supplied a plan with partial `HashAggregate`, `Exchange hashpartitioning(region, 200)`, final `HashAggregate`, `Exchange rangepartitioning(total_revenue DESC, 200)`, `Sort`, and an initial adaptive plan.
- Learner explanation: Exchange `(5)` reshuffles regional partial results so they can be summed; Exchange `(7)` reshuffles the regional result for `orderBy`; `show()` processes the data while `explain()` plans and prints.
- Current status: Milestone 4 and the starter curriculum are complete. Consolidate schema inference versus column pruning, then stop and use a separate project for later topics.
