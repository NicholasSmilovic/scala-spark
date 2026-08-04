# Future Curriculum Datasets

Read this file only while designing or activating a curriculum that may benefit from realistic-scale data.

## Simulated financial transactions

- Archive: `datasets/fraud-transactions-v1.zip` (intended for Git LFS)
- Archive member: `Fraud.csv` (493,534,783 bytes uncompressed)
- SHA-256 of the archive: `06242096c77aef292f3950784f51c77e891df50c734dde5ba152cf68fa054858`
- Shape: 6,362,620 data rows and 11 columns, with no malformed field counts in the verified snapshot
- Columns: `step`, `type`, `amount`, `nameOrig`, `oldbalanceOrg`, `newbalanceOrig`, `nameDest`, `oldbalanceDest`, `newbalanceDest`, `isFraud`, and `isFlaggedFraud`
- Labels: 8,213 rows have `isFraud = 1` (about 0.13%); positive cases occur only in `TRANSFER` and `CASH_OUT`

Treat this as a candidate dataset for a later curriculum whose goals genuinely benefit from realistic scale, such as large CSV ingestion, partition strategy, data-quality checks, feature engineering, skew or class imbalance, and evaluation metrics. Prefer small controlled fixtures for correctness lessons.

The data is simulated, so describe it as transaction-like educational data rather than real bank activity. For predictive exercises, guard against target leakage: do not use `isFlaggedFraud` as an ordinary input feature for `isFraud`, and review whether balance-derived fields or high-cardinality account identifiers make the exercise unrealistically easy.

Spark does not read the CSV member inside a ZIP archive as an ordinary CSV source. When a suitable curriculum is activated:

1. Verify the archive path and checksum.
2. Extract only `Fraud.csv` into that curriculum's isolated, Git-ignored data location.
3. Keep the archive under Git LFS; do not commit the extracted CSV.
4. Use deterministic small samples for unit tests and reserve the full dataset for learner-run integration or scale exercises.
