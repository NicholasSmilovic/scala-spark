# Archived Misconceptions — Curriculum 01

## Shuffle versus insert batching

**Original model**

Repeated Spark shuffles might be reduced by processing inserts in batches, such as 500 records at a time.

**Why it is incomplete**

A shuffle is about redistributing records between Spark partitions. Insert batch size controls how records may be written to an external system; it does not remove the redistribution required by a grouping, join, distinct operation, global sort, repartition, or some aggregations.

**Corrected model**

A Spark shuffle is normally caused by redistributing data between partitions for operations such as `groupBy`, joins, `distinct`, global sorting, `repartition`, and some aggregations. Batching inserts does not generally solve the underlying shuffle.

**Can I explain the correction?**

Not yet demonstrated by the learner.

## Scala versus the JVM

**Original model**

Node.js might correspond to Scala, with Scala translating behind the scenes to a JVM that might itself be Java.

**Why it is incomplete**

Scala is a source language, so its closest Node-ecosystem comparison is JavaScript rather than Node.js. The JVM is a runtime that executes compiled JVM bytecode, so it is closer to Node.js. Java is also a source language; Java and Scala can both compile to bytecode for the JVM.

**Corrected model**

In a rough Node comparison: JavaScript corresponds to Scala, Node.js corresponds to the JVM, and npm corresponds to sbt. The JVM has “Java” in its name but is not the Java language.

**Can I explain the correction?**

Yes. The learner stated: “Scala source is compiled into JVM bytecode, which is then executed by the JVM.”

## JDK versus the Scala compiler

**Original model**

Java 21, or whichever JDK is used with Scala, produces the JVM bytecode.

**Why it is incomplete**

The Scala compiler translates `.scala` source into JVM bytecode. sbt coordinates that compilation. The JDK supplies the JVM that runs sbt and the Scala compiler and then executes the compiled application; its Java compiler, `javac`, compiles Java source rather than Scala source.

**Corrected model**

For this project: sbt invokes the Scala compiler, the Scala compiler produces JVM bytecode, and the JVM supplied by JDK 21 executes it.

**Can I explain the correction?**

Yes. The learner stated: “sbt invokes the scala compiler to produce JVM bytecode, and JDK 21 supplies the JVM that executes it.”

## Scala `Unit` as an amount of work

**Original model**

`Unit` might mean a single instance of work and the smallest amount a Scala method can return.

**Why it is incomplete**

`Unit` does not describe how much work occurred or the size of a result. It is a type with exactly one possible value, written `()`. Since every `Unit`-returning call produces that same value, the return value carries no useful information.

**Corrected model**

A return type of `Unit` says the method may perform work, but it does not return a meaningful result to its caller. It is Scala's practical counterpart to Java's `void`.

**Can I explain the correction?**

Not yet demonstrated by the learner.

## Scala and PySpark execution

**Original model**

A Scala program is JVM bytecode, Spark clusters run JVM bytecode, and both PySpark and Scala produce JVM bytecode. After seeing that a Python process communicates with Spark's JVM engine, the learner asked whether that means Python eventually becomes JVM bytecode.

**Why it is incomplete**

Scala source is compiled into JVM bytecode and packaged for JVM execution. Python source used through PySpark is not compiled into JVM bytecode. The Python API communicates with Spark's JVM engine; built-in Spark operations can be planned and executed by that engine, while Python-specific functions may run in separate Python worker processes.

**Corrected model**

Scala Spark code runs as compiled JVM classes. PySpark uses Python processes plus communication with Spark's JVM-based engine; Python code remains Python rather than becoming JVM bytecode.

**Can I explain the correction?**

Yes. The learner stated that the Python source remains Python and that Python control “never actually compiles to JVM bytecode.” The remaining “mapped Scala bytecode” wording was corrected to operation descriptions and plans executed by Spark's JVM engine.

## sbt `[error]` prefixes versus a failed run

**Original model**

The many Spark lines prefixed with `[error]` by sbt mean that the application produced many errors.

**Why it is incomplete**

The application is configured to run in a forked JVM. Spark writes much of its normal logging to the process's standard-error stream, and sbt labels that stream `[error]` regardless of whether the embedded Spark level is `INFO`, `WARN`, or `ERROR`. A real failure must be identified from the message content, exception or nonzero exit, and final build status.

**Corrected model**

In this run, the embedded messages are informational or nonfatal warnings, the schema printed, sbt ended with `[success]`, and the shell prompt returned. Therefore the run succeeded.

**Can I explain the correction?**

Not yet demonstrated by the learner.

## One Spark session per row range

**Original model**

Parallel CSV processing might use one Spark session for rows 1–1,000,000 and another session for rows 10,000,000–20,000,000.

**Why it is incomplete**

A Spark application normally uses one coordinating driver and Spark session. Spark divides input into partitions, creates tasks for those partitions, and sends the tasks to executor threads or processes. File splits are based on storage/file boundaries and byte ranges rather than manually assigned row-number ranges; distributed DataFrames do not have an inherent global row order.

**Corrected model**

One Spark session can coordinate a DataFrame containing many partitions. Parallel tasks process those partitions locally across cores or on executors across a cluster.

**Can I explain the correction?**

Not yet demonstrated by the learner.

## Fixed inference sample and whole-dataset `show()`

**Original model**

Schema inference may read the first fixed number of rows, perhaps 100, and `show()` displays the whole dataset.

**Why it is incomplete**

CSV schema inference uses a configurable `samplingRatio`; its default is 1.0, so the current pipeline examines the full input during the early inference pass rather than a fixed first 100 rows. Parameterless `show()` displays at most the top 20 rows. It happened to display the whole lab dataset only because that dataset has twelve rows.

**Corrected model**

With the current options, inference performs an early full-input type scan. `show()` is a data action that retrieves a limited preview—20 rows by default—rather than printing the whole DataFrame.

**Can I explain the correction?**

Yes. The learner explained the default full inference scan and identified `show()` as an action that retrieves up to 20 rows, with a configurable count and no offset.

## `filter` as an action

**Original model**

Schema inference is a scan, `filter` is an action, and `show()` is another action.

**Why it is incomplete**

Schema inference performs an early input scan when the DataFrame is created. `filter` is a transformation: it returns a new DataFrame containing an updated logical plan and does not by itself process every row. `show()` is the action that triggers execution of the read and filters to produce visible output.

**Corrected model**

In the current pipeline, inference is an early scan, both `filter` calls are lazy transformations, and `validOrders.show()` is the data action.

**Can I explain the correction?**

Yes. The learner stated that `filter`/`withColumn` are transformations and `show()` is the action that makes Spark work through them.

## Actions returning DataFrames and only one action

**Original model**

The learner considered `validOrders.show(1).revenue()`, assigning `result = show`, then adding transformations to `result`, and asked whether a pipeline can have only one action.

**Why it is incomplete**

`show()` is an action that prints rows and returns `Unit`, not a DataFrame, so transformations cannot be chained after its result. `first()` is also an action and returns one `Row`. A program may invoke multiple actions on the same DataFrame; each action can trigger execution of its upstream plan again.

**Corrected model**

Transformations return DataFrames and can extend a plan. Actions request results and return or emit non-DataFrame results. Multiple actions are allowed, and without reused materialized results they may repeat upstream computation.

**Can I explain the correction?**

Not yet demonstrated by the learner.
