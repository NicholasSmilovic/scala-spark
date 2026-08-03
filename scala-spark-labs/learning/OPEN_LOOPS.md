# Open Learning Loops

Only unresolved ideas that should affect a future lesson belong here. Remove an item after the learner supplies closing evidence; retain the evidence in the relevant track summary.

| Area | Current evidence gap | Planned revisit | Closing evidence |
| --- | --- | --- | --- |
| Declared input type vs validation | Learner correctly explained that the raw string schema has not enforced numeric typing, then guessed that declaring a numeric CSV field would necessarily stop on `"two"`. | Curriculum 03, Milestones 2–3 | Tests and explains safe parsing from retained raw strings, typed null results, and diagnosable rejection. |
| Spark testing | No Scala/Spark test structure or testing strategy has been introduced yet. | Curriculum 03, Milestones 1–4 | Writes focused transformation tests with controlled input DataFrames, verifies rows and schemas, and tests file output in isolated temporary paths. |
| Scala `Unit` | Practical `void` analogy is sufficient, but the single-value type model is not yet demonstrated. | Curriculum 03, Milestone 4 | Contrasts a `DataFrame => DataFrame` transformation with an output method returning `Unit` and verifies the latter through its side effect. |

If the same gap recurs in a later curriculum, update the existing row rather than adding a duplicate.
