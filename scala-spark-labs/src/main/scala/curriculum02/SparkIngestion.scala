package curriculum02

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.functions.{col, expr, lit, trim, when}


object SparkIngestion {
    def main(args: Array[String]): Unit = {
        val spark = SparkSession
        .builder()
        .appName("Spark Ingestion")
        .master("local[*]")
        .getOrCreate()

        spark.sparkContext.setLogLevel("WARN")


        try {
            println(s"spark version: ${spark.version} is running")
            println(s"Master: ${spark.sparkContext.master}")

            val rawOrderSchema = StructType(Seq(
                StructField("order_id", StringType, nullable = true),
                StructField("customer_id", StringType, nullable = true),
                StructField("region", StringType, nullable = true),
                StructField("product", StringType, nullable = true),
                StructField("quantity", StringType, nullable = true),
                StructField("unit_price", StringType, nullable = true),
                StructField("status", StringType, nullable = true),
                StructField("order_date", StringType, nullable = true)
            ))
            
            val rawOrders = spark.read
                .option("header", "true")
                .schema(rawOrderSchema)
                .csv("data/curriculum-02/incoming")

            val preparedOrders = rawOrders
                .withColumn(
                    "quantity_typed",
                    expr("try_cast(trim(quantity) AS INT)")
                )
                .withColumn(
                    "order_id_typed",
                    expr("try_cast(trim(order_id) AS INT)")
                )
                .withColumn(
                    "unit_price_typed",
                    expr("try_cast(trim(unit_price) AS DECIMAL(10,2))")
                )
                .withColumn(
                    "order_date_typed",
                    expr("try_cast(trim(order_date) AS DATE)")
                )
                .withColumn("customer_id_clean", trim(col("customer_id")))
                .withColumn("region_clean", trim(col("region")))
                .withColumn("product_clean", trim(col("product")))
                .withColumn("status_clean", trim(col("status")))
                .withColumn(
                    "rejection_reason",

                    when(
                        col("order_id_typed").isNull || (col("order_id_typed") <= 0),
                        lit("order_id must be a positive integer")
                    )

                    .when(
                        col("quantity_typed").isNull,
                        lit("quantity is not an integer")
                    )
                    .when(
                        col("quantity_typed") <= 0,
                        lit("quantity must be greater than zero")
                    )

                    .when(
                        col("unit_price").isNull,
                        lit("unit_price is missing")
                    )
                    .when(
                        col("unit_price_typed").isNull,
                        lit("unit_price is not a decimal")
                    )
                    .when(
                        col("unit_price_typed") <= 0,
                        lit("unit_price must be greater than zero")
                    )
                    
                    .when(
                        col("order_date_typed").isNull,
                        lit("order_date is invalid")
                    )

                    .when(
                        col("customer_id_clean").isNull ||
                            col("customer_id_clean") === "",
                        lit("customer_id is missing")                        
                    )
                    .when(
                        col("product_clean").isNull ||
                            col("product_clean") === "",
                        lit("product is missing")
                    )
                    .when(
                        col("region_clean").isNull ||
                            !col("region_clean").isin("North", "South", "East", "West"),
                        lit("region is invalid")
                    )
                    .when(
                        col("status_clean").isNull ||
                            !col("status_clean").isin("completed", "cancelled"),
                        lit("status is invalid")
                    )


                    .otherwise(lit(null).cast(StringType))
                )



            val validOrders = preparedOrders
                .filter(col("rejection_reason").isNull)

            val rejectedOrders = preparedOrders
                .filter(col("rejection_reason").isNotNull)

            val curatedOrders = validOrders.select(
                    col("order_id_typed").alias("order_id"),
                    col("quantity_typed").alias("quantity"),
                    col("unit_price_typed").alias("unit_price"),
                    col("order_date_typed").alias("order_date"),
                    col("customer_id_clean").alias("customer_id"),
                    col("region_clean").alias("region"),
                    col("product_clean").alias("product"),
                    col("status_clean").alias("status")
                )


            val writeReadyOrders = curatedOrders.repartition(col("region"))

            writeReadyOrders
                .write
                .mode("overwrite")
                .partitionBy("region")
                .parquet("output/curriculum-02/curated")


            rejectedOrders.select(
                    col("order_id"),
                    col("order_id_typed"),
                    col("customer_id"),
                    col("customer_id_clean"),
                    col("region"),
                    col("region_clean"),
                    col("product"),
                    col("product_clean"),
                    col("quantity"),
                    col("quantity_typed"),
                    col("unit_price"),
                    col("unit_price_typed"),
                    col("status"),
                    col("status_clean"),
                    col("order_date"),
                    col("order_date_typed"),
                    col("rejection_reason")
                )
                .write
                .mode("overwrite")
                .parquet("output/curriculum-02/rejected")


            val savedCuratedOrders = spark.read
                .parquet("output/curriculum-02/curated")


            val northOrders = savedCuratedOrders
                .filter(col("region") === "North")
                .select(
                    col("order_id"),
                    col("quantity"),
                    col("unit_price")
                )
                northOrders.explain("formatted")

                northOrders.show(truncate = false)


            val savedRejectedOrders = spark.read
                .parquet("output/curriculum-02/rejected")

            val rejectedOrdersSaved = savedRejectedOrders
                .select(
                    col("order_id"),
                    col("rejection_reason")
                )

            println(s"rejected count ${rejectedOrdersSaved.count()}")

            rejectedOrdersSaved.show()

            
        } finally {
            spark.stop()
        }
    
    }
}
