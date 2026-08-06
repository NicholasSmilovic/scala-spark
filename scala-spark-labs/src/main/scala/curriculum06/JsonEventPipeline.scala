package curriculum06

import org.apache.spark.sql.types.{
    ArrayType,
    StringType,
    StructField,
    StructType
}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{
    col,
    expr,
    lit,
    row_number,
    trim,
    when
}
import org.apache.spark.sql.expressions.Window


object JsonEventPipeline {

    val rawEventSchema: StructType = StructType(Seq(
        StructField("event_id", StringType, nullable = true),
        StructField("account_id", StringType, nullable = true),
        StructField("event_time", StringType, nullable = true),
        StructField("amount", StringType, nullable = true),
        StructField(
            "merchant",
            StructType(Seq(
                StructField(
                    "merchant_id",
                    StringType,
                    nullable = true
                ),
                StructField(
                    "name",
                    StringType,
                    nullable = true
                )
            )),
            nullable = true
        ),
        StructField(
            "tags",
            ArrayType(StringType, containsNull = true),
            nullable = true
        ),
        StructField(
            "ingest_sequence",
            StringType,
            nullable = true
        ),
        StructField(
            "_corrupt_record",
            StringType,
            nullable = true
        )
    ))


    def readRawEvents(
        spark: SparkSession,
        inputPath: String
    ): DataFrame = {

        spark.read
            .schema(rawEventSchema)
            .option("mode", "PERMISSIVE")
            .option(
                "columnNameOfCorruptRecord",
                "_corrupt_record"
            )
            .json(inputPath)
    }

    def prepareEvents(rawEvents: DataFrame): DataFrame = {
        rawEvents
            .withColumn(
                "event_time_typed",
                expr(
                    "try_to_timestamp(" +
                        "event_time, " +
                        "'yyyy-MM-dd HH:mm:ss'" +
                    ")"
                )
            )
            .withColumn(
                "amount_typed",
                expr(
                    "try_cast(amount AS DECIMAL(12,2))"
                )
            )
            .withColumn(
                "ingest_sequence_typed",
                expr(
                    "try_cast(ingest_sequence AS BIGINT)"
                )
            )
                        .withColumn(
                "event_id_clean",
                trim(col("event_id"))
            )
            .withColumn(
                "account_id_clean",
                trim(col("account_id"))
            )
            .withColumn(
                "merchant_id_clean",
                trim(col("merchant.merchant_id"))
            )
            .withColumn(
                "merchant_name_clean",
                trim(col("merchant.name"))
            )
            .withColumn(
                "rejection_reason",
                when(
                    col("_corrupt_record").isNotNull,
                    lit("malformed_json")
                )
                .when(
                    col("event_id_clean").isNull ||
                        (col("event_id_clean") === ""),
                    lit("missing_event_id")
                )
                .when(
                    col("account_id_clean").isNull ||
                        (col("account_id_clean") === ""),
                    lit("missing_account_id")
                )
                .when(
                    col("event_time_typed").isNull,
                    lit("invalid_event_time")
                )
                .when(
                    col("amount_typed").isNull,
                    lit("invalid_amount")
                )
                .when(
                    col("amount_typed") <= 0,
                    lit("non_positive_amount")
                )
                .when(
                    col("merchant_id_clean").isNull ||
                        (col("merchant_id_clean") === ""),
                    lit("missing_merchant_id")
                )
                .when(
                    col("merchant_name_clean").isNull ||
                        (col("merchant_name_clean") === ""),
                    lit("missing_merchant_name")
                )
                .when(
                    col("ingest_sequence_typed").isNull,
                    lit("invalid_ingest_sequence")
                )
                .otherwise(
                    lit(null).cast(StringType)
                )
            )
    }

    def curatedEvents(
            preparedEvents: DataFrame
        ): DataFrame = {

        preparedEvents
            .filter(col("rejection_reason").isNull)
            .select(
                col("event_id_clean").alias("event_id"),
                col("account_id_clean").alias("account_id"),
                col("event_time_typed").alias("event_time"),
                col("amount_typed").alias("amount"),
                col("merchant_id_clean").alias("merchant_id"),
                col("merchant_name_clean").alias("merchant_name"),
                col("tags"),
                col("ingest_sequence_typed")
                    .alias("ingest_sequence")
            )
    }

    def rejectedEvents(
            preparedEvents: DataFrame
        ): DataFrame = {

        preparedEvents
            .filter(col("rejection_reason").isNotNull)
            .select(
                col("event_id"),
                col("account_id"),
                col("event_time"),
                col("amount"),
                col("merchant.merchant_id").alias("merchant_id"),
                col("merchant.name").alias("merchant_name"),
                col("tags"),
                col("ingest_sequence"),
                col("_corrupt_record"),
                col("rejection_reason")
            )
    }

    def deduplicateEvents(events: DataFrame): DataFrame = {
        val eventVersionWindow = Window
            .partitionBy(col("event_id"))
            .orderBy(
                col("event_time").desc,
                col("ingest_sequence").desc
            )

        events
            .withColumn(
                "_version_rank",
                row_number().over(eventVersionWindow)
            )
            .filter(col("_version_rank") === 1)
            .drop("_version_rank")
    }

    def writeOutputs(
            deduplicatedCuratedEvents: DataFrame,
            rejectedEvents: DataFrame,
            baseOutputPath: String
        ): Unit = {

        deduplicatedCuratedEvents
            .write
            .mode("overwrite")
            .parquet(s"$baseOutputPath/curated")

        rejectedEvents
            .write
            .mode("overwrite")
            .parquet(s"$baseOutputPath/rejected")
    }
}