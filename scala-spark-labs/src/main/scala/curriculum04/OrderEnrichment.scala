package curriculum04

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{
    col,
    broadcast
}

object OrderEnrichment {

    def innerEnrichOrders(
        orders: DataFrame,
        products: DataFrame
    ): DataFrame = {
        orders
            .join(products, Seq("product_id"), "inner")
            .select(
                col("order_id"),
                col("product_id"),
                col("quantity"),
                col("product_name"),
                col("category")
            )
    }
    
    def innerBroadcastEnrichOrders(
        orders: DataFrame,
        products: DataFrame
    ): DataFrame = {
        orders
            .join(broadcast(products), Seq("product_id"), "inner")
            .select(
                col("order_id"),
                col("product_id"),
                col("quantity"),
                col("product_name"),
                col("category")
            )
    }

    def leftEnrichOrders(
        orders: DataFrame,
        products: DataFrame
    ): DataFrame = {
        orders
            .join(products, Seq("product_id"), "left")
            .select(
                col("order_id"),
                col("product_id"),
                col("quantity"),
                col("product_name"),
                col("category")
            )
    }


    def duplicateProductKeys(products: DataFrame): DataFrame = {
        products
            .groupBy(col("product_id"))
            .count()
            .filter(col("count") > 1)
            .select(
                col("product_id"),
                col("count").alias("occurrence_count")
            )
    }
}