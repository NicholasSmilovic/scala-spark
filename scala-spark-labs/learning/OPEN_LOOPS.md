# Open Learning Loops

| Area | Current evidence gap | Planned revisit | Closing evidence |
| --- | --- | --- | --- |
| Everyday DataFrame API breadth | The locked core uses the major transformation, aggregation, join, partition, persistence, nested-data, and window patterns, but does not systematically cover helpers such as `split`, `explode`, `unionByName`, null-filling, or broader string/date/array functions. | Offer an optional short API clinic after Curriculum 06; introduce `split`/`explode` conceptually in Curriculum 06 without changing its preserve-tags contract. | Small exact tests prove representative string-to-array, array-to-rows/cardinality, union, null, string, date, and array behaviors. |
| Typed Dataset implementation depth | Learner now explains `DataFrame`/`Dataset[Row]`, typed `Dataset[Event]`, and their local `collect()` result types, but has not implemented a case-class Dataset or encoder. | Optional typed-Dataset specialization after the core track, only if the learner chooses it. | A case-class Dataset exercise demonstrates encoders and an appropriate typed use case. |
