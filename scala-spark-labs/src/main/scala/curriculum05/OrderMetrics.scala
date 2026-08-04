package curriculum05

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{
    col,
    expr,
    sum
}


// orders contains:order_id: Int
// product_id: Int
// quantity: Int
// unit_price: Double
object OrderMetrics {

    def prepareOrders(orders: DataFrame): DataFrame = {
        orders.withColumn(
            "revenue",
            col("quantity") * col("unit_price")
        )
    }


// revenueTotalsByProduct returns product_id and total_revenue, summing revenue per product.
    def revenueTotalsByProduct(preparedOrders: DataFrame): DataFrame = {
        preparedOrders
            .groupBy(col("product_id"))
            .agg(
                sum(col("revenue")).alias("total_revenue")
            )
    }

// ordersAboveRevenue returns order_id, product_id, and revenue where revenue is strictly greater than threshold.
    def ordersAboveRevenue(
            preparedOrders: DataFrame,
            threshold: Double
        ): DataFrame = {

        preparedOrders
            .filter(col("revenue") > threshold)
            .select(
                "order_id",
                "product_id",
                "revenue"
            )

    }

}