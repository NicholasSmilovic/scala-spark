package curriculum06

import java.nio.file.{Files, Path}
import scala.jdk.CollectionConverters._
import org.apache.spark.sql.{Row, SparkSession}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite

import org.apache.spark.sql.types.{
    ArrayType,
    DecimalType,
    LongType,
    StringType,
    StructField,
    StructType,
    TimestampType
}

class JsonEventPipelineSpec extends AnyFunSuite with BeforeAndAfterAll {
    private var spark: SparkSession = _


    override protected def beforeAll(): Unit = {
        super.beforeAll()

        spark = SparkSession.builder()
            .appName("JsonEventPipelineSpec")
            .master("local[2]")
            .config("spark.driver.bindAddress", "127.0.0.1")
            .config("spark.driver.host", "127.0.0.1")
            .config("spark.ui.enabled", "false")
            .getOrCreate()

    }


    override protected def afterAll(): Unit = {
        try {
            if(spark != null) {
                spark.stop() 
            }
        } finally {
            super.afterAll()
        }
    }

    private def deleteRecursively(path: Path): Unit = {
        if (Files.exists(path)) {
            val stream = Files.walk(path)

            val paths =
                try {
                    stream.iterator().asScala.toSeq
                        .sortBy(_.getNameCount)
                        .reverse
                } finally {
                    stream.close()
                }

            paths.foreach(current => Files.deleteIfExists(current))
        }
    }

    test("lands valid nested JSON and retains malformed JSON") {
        val inputDirectory =
            Files.createTempDirectory("json-event-input-")
        val inputFile = inputDirectory.resolve("events.json")

        val validJson =
            """{"event_id":"evt-1","account_id":"acct-1","event_time":"2026-08-05 10:00:00","amount":"12.50","merchant":{"merchant_id":"m-1","name":"Corner Shop"},"tags":["food","local"],"ingest_sequence":"1"}"""

        val malformedJson = "not-json-at-all"

        val businessInvalidJson =
            """{"event_id":"evt-2","account_id":"acct-2","event_time":"tomorrow","amount":"free","merchant":{"merchant_id":"m-2","name":"Other Shop"},"tags":[],"ingest_sequence":"many"}"""

        Files.writeString(
            inputFile,
            s"$validJson\n$businessInvalidJson\n$malformedJson\n"
        )

        val landed = JsonEventPipeline.readRawEvents(
            spark,
            inputDirectory.toString
        )

        val prepared =
            JsonEventPipeline.prepareEvents(landed)

        val curated =
            JsonEventPipeline.curatedEvents(prepared)

        val rejected =
            JsonEventPipeline.rejectedEvents(prepared)

        try {
            
            assert(landed.schema == JsonEventPipeline.rawEventSchema)

            val rows = prepared.collect()
            assert(rows.length == 3)

            val validRow = rows
                .find(_.getAs[String]("event_id") == "evt-1")
                .getOrElse(fail("Expected the valid event row"))

            assert(
                validRow.getAs[java.sql.Timestamp](
                    "event_time_typed"
                ) == java.sql.Timestamp.valueOf(
                    "2026-08-05 10:00:00"
                )
            )

            assert(
                validRow.getAs[java.math.BigDecimal](
                    "amount_typed"
                ) == new java.math.BigDecimal("12.50")
            )

            assert(
                validRow.getAs[Long](
                    "ingest_sequence_typed"
                ) == 1L
            )

            assert(validRow.getAs[String]("account_id") == "acct-1")
            assert(
                validRow.getAs[String]("event_time") ==
                    "2026-08-05 10:00:00"
            )
            assert(validRow.getAs[String]("amount") == "12.50")

            val tags = validRow
                .getAs[scala.collection.Seq[String]]("tags")
                .toList

            assert(tags == List("food", "local"))

            assert(
                validRow.getAs[String]("ingest_sequence") == "1"
            )
            assert(
                validRow.getAs[String]("_corrupt_record") == null
            )

            val merchant = validRow.getAs[Row]("merchant")

            assert(merchant.getAs[String]("merchant_id") == "m-1")
            assert(merchant.getAs[String]("name") == "Corner Shop")

            val malformedRow = rows
                .find(
                    _.getAs[String]("_corrupt_record") != null
                )
                .getOrElse(fail("Expected a malformed event row"))

            assert(
                malformedRow.getAs[String]("_corrupt_record") ==
                    malformedJson
            )
            assert(malformedRow.getAs[String]("event_id") == null)

            val invalidRow = rows
                .find(_.getAs[String]("event_id") == "evt-2")
                .getOrElse(fail("Expected the invalid event row"))

            assert(invalidRow.getAs[String]("event_time") == "tomorrow")
            assert(invalidRow.getAs[String]("amount") == "free")
            assert(
                invalidRow.getAs[String]("ingest_sequence") == "many"
            )
            assert(
                invalidRow.getAs[String]("_corrupt_record") == null
            )

            assert(
                invalidRow.isNullAt(
                    invalidRow.fieldIndex("event_time_typed")
                )
            )
            assert(
                invalidRow.isNullAt(
                    invalidRow.fieldIndex("amount_typed")
                )
            )
            assert(
                invalidRow.isNullAt(
                    invalidRow.fieldIndex(
                        "ingest_sequence_typed"
                    )
                )
            )


            assert(
                validRow.getAs[String]("rejection_reason") == null
            )
            assert(
                invalidRow.getAs[String]("rejection_reason") ==
                    "invalid_event_time"
            )
            assert(
                malformedRow.getAs[String]("rejection_reason") ==
                    "malformed_json"
            )



            assert(
                curated.columns.toSeq == Seq(
                    "event_id",
                    "account_id",
                    "event_time",
                    "amount",
                    "merchant_id",
                    "merchant_name",
                    "tags",
                    "ingest_sequence"
                )
            )


            val expectedCuratedSchema = StructType(Seq(
                StructField("event_id", StringType, true),
                StructField("account_id", StringType, true),
                StructField("event_time", TimestampType, true),
                StructField(
                    "amount",
                    DecimalType(12, 2),
                    true
                ),
                StructField("merchant_id", StringType, true),
                StructField("merchant_name", StringType, true),
                StructField(
                    "tags",
                    ArrayType(StringType, containsNull = true),
                    true
                ),
                StructField(
                    "ingest_sequence",
                    LongType,
                    true
                )
            ))

            assert(curated.schema == expectedCuratedSchema)
            val curatedRows = curated.collect()

            assert(curatedRows.length == 1)
            assert(
                curatedRows.head.getAs[String]("event_id") ==
                    "evt-1"
            )
            assert(
                curatedRows.head.getAs[String]("merchant_id") ==
                    "m-1"
            )

            val curatedTags = curatedRows.head
                .getAs[scala.collection.Seq[String]]("tags")
                .toList

            assert(curatedTags == List("food", "local"))

            val expectedRejectedSchema = StructType(Seq(
                StructField("event_id", StringType, true),
                StructField("account_id", StringType, true),
                StructField("event_time", StringType, true),
                StructField("amount", StringType, true),
                StructField("merchant_id", StringType, true),
                StructField("merchant_name", StringType, true),
                StructField(
                    "tags",
                    ArrayType(StringType, containsNull = true),
                    true
                ),
                StructField(
                    "ingest_sequence",
                    StringType,
                    true
                ),
                StructField(
                    "_corrupt_record",
                    StringType,
                    true
                ),
                StructField(
                    "rejection_reason",
                    StringType,
                    true
                )
            ))

            assert(rejected.schema == expectedRejectedSchema)

            val rejectedRows = rejected.collect()

            assert(rejectedRows.length == 2)
            assert(
                rejectedRows
                    .map(
                        _.getAs[String]("rejection_reason")
                    )
                    .toSet ==
                    Set(
                        "invalid_event_time",
                        "malformed_json"
                    )
            )

        } finally {
            Files.deleteIfExists(inputFile)
            Files.deleteIfExists(inputDirectory)
        }
    }


    test("assigns every business validation reason") {
        val session = spark

        import session.implicits._

        def eventJson(
                eventId: String,
                accountId: String = "acct-ok",
                eventTime: String = "2026-08-05 10:00:00",
                amount: String = "10.00",
                merchantId: String = "merchant-ok",
                merchantName: String = "Shop",
                ingestSequence: String = "1"
            ): String = {

            s"""{"event_id":"$eventId","account_id":"$accountId","event_time":"$eventTime","amount":"$amount","merchant":{"merchant_id":"$merchantId","name":"$merchantName"},"tags":[],"ingest_sequence":"$ingestSequence"}"""
        }

        val validationCases = Seq(
            (
                eventJson(
                    eventId = "",
                    accountId = "case-missing-event"
                ),
                "case-missing-event",
                "missing_event_id"
            ),
            (
                eventJson(
                    eventId = "case-missing-account",
                    accountId = ""
                ),
                "case-missing-account",
                "missing_account_id"
            ),
            (
                eventJson(
                    eventId = "case-invalid-time",
                    eventTime = "tomorrow"
                ),
                "case-invalid-time",
                "invalid_event_time"
            ),
            (
                eventJson(
                    eventId = "case-invalid-amount",
                    amount = "free"
                ),
                "case-invalid-amount",
                "invalid_amount"
            ),
            (
                eventJson(
                    eventId = "case-non-positive",
                    amount = "0.00"
                ),
                "case-non-positive",
                "non_positive_amount"
            ),
            (
                eventJson(
                    eventId = "case-missing-merchant-id",
                    merchantId = ""
                ),
                "case-missing-merchant-id",
                "missing_merchant_id"
            ),
            (
                eventJson(
                    eventId = "case-missing-merchant-name",
                    merchantName = ""
                ),
                "case-missing-merchant-name",
                "missing_merchant_name"
            ),
            (
                eventJson(
                    eventId = "case-invalid-sequence",
                    ingestSequence = "many"
                ),
                "case-invalid-sequence",
                "invalid_ingest_sequence"
            )
        )

        val inputDirectory =
            Files.createTempDirectory("validation-events-")
        val inputFile =
            inputDirectory.resolve("events.json")

        Files.writeString(
            inputFile,
            validationCases
                .map(_._1)
                .mkString("", "\n", "\n")
        )
        
        val rawEvents = JsonEventPipeline.readRawEvents(
            spark,
            inputDirectory.toString
        )
        try {

            val rejectedRows = JsonEventPipeline
                .rejectedEvents(
                    JsonEventPipeline.prepareEvents(rawEvents)
                )
                .collect()

            assert(rejectedRows.length == validationCases.length)

            val actualReasons = rejectedRows
                .map { row =>
                    val eventId =
                        row.getAs[String]("event_id")

                    val caseId = Option(eventId)
                        .filter(_.nonEmpty)
                        .getOrElse(
                            row.getAs[String]("account_id")
                        )

                    (
                        caseId,
                        row.getAs[String]("rejection_reason")
                    )
                }
                .toSet

            val expectedReasons = validationCases
                .map { case (_, caseId, reason) =>
                    (caseId, reason)
                }
                .toSet

            assert(actualReasons == expectedReasons)

        } finally {
            Files.deleteIfExists(inputFile)
            Files.deleteIfExists(inputDirectory)
        }
    }
        test("selects deterministic event-version winners") {
        val session = spark

        import session.implicits._

        val events = Seq(
            (
                "evt-1",
                "acct-older",
                java.sql.Timestamp.valueOf(
                    "2026-08-06 10:00:00"
                ),
                new java.math.BigDecimal("10.00"),
                "merchant-1",
                "Old Shop",
                Array("older"),
                99L
            ),
            (
                "evt-1",
                "acct-newer",
                java.sql.Timestamp.valueOf(
                    "2026-08-06 11:00:00"
                ),
                new java.math.BigDecimal("20.00"),
                "merchant-1",
                "New Shop",
                Array("newer"),
                1L
            ),
            (
                "evt-2",
                "acct-original",
                java.sql.Timestamp.valueOf(
                    "2026-08-06 12:00:00"
                ),
                new java.math.BigDecimal("30.00"),
                "merchant-2",
                "Original Shop",
                Array("original"),
                1L
            ),
            (
                "evt-2",
                "acct-correction",
                java.sql.Timestamp.valueOf(
                    "2026-08-06 12:00:00"
                ),
                new java.math.BigDecimal("35.00"),
                "merchant-2",
                "Corrected Shop",
                Array("corrected"),
                2L
            ),
            (
                "evt-3",
                "acct-single",
                java.sql.Timestamp.valueOf(
                    "2026-08-06 09:00:00"
                ),
                new java.math.BigDecimal("40.00"),
                "merchant-3",
                "Single Shop",
                Array("single"),
                1L
            )
        ).toDF(
            "event_id",
            "account_id",
            "event_time",
            "amount",
            "merchant_id",
            "merchant_name",
            "tags",
            "ingest_sequence"
        )

        val deduplicated =
            JsonEventPipeline.deduplicateEvents(events)

        println("=== DEDUPLICATION PLAN ===")
        deduplicated.explain("formatted")

        assert(deduplicated.schema == events.schema)
        assert(
            !deduplicated.columns.contains("_version_rank")
        )

        val winners = deduplicated
            .collect()
            .map { row =>
                (
                    row.getAs[String]("event_id"),
                    row.getAs[String]("account_id"),
                    row.getAs[Long]("ingest_sequence")
                )
            }
            .toSet

        assert(
            winners == Set(
                ("evt-1", "acct-newer", 1L),
                ("evt-2", "acct-correction", 2L),
                ("evt-3", "acct-single", 1L)
            )
        )
    }

    test("writes and rereads curated and rejected Parquet") {
        val tempRoot =
            Files.createTempDirectory("json-event-output-")
        val inputDirectory = tempRoot.resolve("input")
        val outputDirectory = tempRoot.resolve("output")

        Files.createDirectories(inputDirectory)

        val inputFile =
            inputDirectory.resolve("events.json")

        val malformedJson = "not-json-at-all"

        val jsonLines = Seq(
            """{"event_id":"evt-1","account_id":"acct-older","event_time":"2026-08-06 10:00:00","amount":"10.00","merchant":{"merchant_id":"m-1","name":"Old Shop"},"tags":["older"],"ingest_sequence":"99"}""",
            """{"event_id":"evt-1","account_id":"acct-newer","event_time":"2026-08-06 11:00:00","amount":"20.00","merchant":{"merchant_id":"m-1","name":"New Shop"},"tags":["newer"],"ingest_sequence":"1"}""",
            """{"event_id":"evt-2","account_id":"acct-single","event_time":"2026-08-06 09:00:00","amount":"30.00","merchant":{"merchant_id":"m-2","name":"Single Shop"},"tags":["single"],"ingest_sequence":"1"}""",
            """{"event_id":"evt-bad","account_id":"acct-bad","event_time":"2026-08-06 08:00:00","amount":"free","merchant":{"merchant_id":"m-bad","name":"Bad Shop"},"tags":[],"ingest_sequence":"1"}""",
            malformedJson
        )

        Files.writeString(
            inputFile,
            jsonLines.mkString("", "\n", "\n")
        )

        try {
            val landed = JsonEventPipeline.readRawEvents(
                spark,
                inputDirectory.toString
            )

            val prepared =
                JsonEventPipeline.prepareEvents(landed)

            val curated =
                JsonEventPipeline.curatedEvents(prepared)

            val rejected =
                JsonEventPipeline.rejectedEvents(prepared)

            val deduplicated =
                JsonEventPipeline.deduplicateEvents(curated)

            JsonEventPipeline.writeOutputs(
                deduplicated,
                rejected,
                outputDirectory.toString
            )

            // A second call proves overwrite mode is rerunnable.
            JsonEventPipeline.writeOutputs(
                deduplicated,
                rejected,
                outputDirectory.toString
            )

            val savedCurated = spark.read.parquet(
                outputDirectory.resolve("curated").toString
            )

            val savedRejected = spark.read.parquet(
                outputDirectory.resolve("rejected").toString
            )

            assert(Files.isDirectory(
                outputDirectory.resolve("curated")
            ))
            assert(Files.isDirectory(
                outputDirectory.resolve("rejected")
            ))

            assert(savedCurated.schema == deduplicated.schema)
            assert(savedRejected.schema == rejected.schema)

            val curatedResults = savedCurated
                .collect()
                .map { row =>
                    (
                        row.getAs[String]("event_id"),
                        row.getAs[String]("account_id"),
                        row.getAs[java.sql.Timestamp](
                            "event_time"
                        ).toString,
                        row.getAs[java.math.BigDecimal](
                            "amount"
                        ).toPlainString,
                        row.getAs[String]("merchant_name"),
                        row.getAs[
                            scala.collection.Seq[String]
                        ]("tags").toList,
                        row.getAs[Long]("ingest_sequence")
                    )
                }
                .toSet

            assert(
                curatedResults == Set(
                    (
                        "evt-1",
                        "acct-newer",
                        "2026-08-06 11:00:00.0",
                        "20.00",
                        "New Shop",
                        List("newer"),
                        1L
                    ),
                    (
                        "evt-2",
                        "acct-single",
                        "2026-08-06 09:00:00.0",
                        "30.00",
                        "Single Shop",
                        List("single"),
                        1L
                    )
                )
            )

            val rejectedResults = savedRejected
                .collect()
                .map { row =>
                    (
                        Option(row.getAs[String]("event_id")),
                        Option(row.getAs[String]("amount")),
                        Option(
                            row.getAs[String]("_corrupt_record")
                        ),
                        row.getAs[String]("rejection_reason")
                    )
                }
                .toSet

            assert(
                rejectedResults == Set(
                    (
                        Some("evt-bad"),
                        Some("free"),
                        None,
                        "invalid_amount"
                    ),
                    (
                        None,
                        None,
                        Some(malformedJson),
                        "malformed_json"
                    )
                )
            )
        } finally {
            deleteRecursively(tempRoot)
        }
    }
}