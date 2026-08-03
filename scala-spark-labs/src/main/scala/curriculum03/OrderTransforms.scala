package curriculum03

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{col, expr, lit, trim, when}
import org.apache.spark.sql.types.{
    StringType
}

object OrderTransforms {

    def normalizeAndType(rawDataFrame: DataFrame): DataFrame = {

        val preparedOrders = rawDataFrame
            .withColumn(
                "quantity_typed",
                expr("try_cast(trim(quantity) AS INT)")
            )
            .withColumn(
                "order_id_typed",
                expr("try_cast(trim(order_id) AS INT)")
            )
            .withColumn(
                "order_date_typed",
                expr("try_cast(trim(order_date) AS DATE)")
            )
            .withColumn(
                "unit_price_typed",
                expr("try_cast(trim(unit_price) AS DECIMAL(10,2))")
            )
            .withColumn("customer_id_clean", trim(col("customer_id")))
            .withColumn("region_clean", trim(col("region")))
            .withColumn("product_clean", trim(col("product")))
            .withColumn("status_clean", trim(col("status")))


        
        preparedOrders
    }

    def withRejectionReason(normalizedDataFrame: DataFrame): DataFrame = {
        normalizedDataFrame.withColumn(
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


        
    }
    def validOrders(validatedOrders: DataFrame): DataFrame = {
        validatedOrders.filter(col("rejection_reason").isNull)
    }

    def rejectedOrders(validatedOrders: DataFrame): DataFrame = {
        validatedOrders.filter(col("rejection_reason").isNotNull)
    }

    def curatedOrders(validOrders: DataFrame): DataFrame = {
        validOrders.select(
            col("order_id_typed").alias("order_id"),
            col("quantity_typed").alias("quantity"),
            col("unit_price_typed").alias("unit_price"),
            col("order_date_typed").alias("order_date"),
            col("customer_id_clean").alias("customer_id"),
            col("region_clean").alias("region"),
            col("product_clean").alias("product"),
            col("status_clean").alias("status")
        )
    }


    def writeOutputs(
        curated: DataFrame,
        rejected: DataFrame,
        baseOutputPath: String
    ): Unit = {
        curated
            .repartition(col("region"))
            .write
            .mode("overwrite")
            .partitionBy("region")
            .parquet(s"$baseOutputPath/curated")

        rejected
            .write
            .mode("overwrite")
            .parquet(s"$baseOutputPath/rejected")
    }

}