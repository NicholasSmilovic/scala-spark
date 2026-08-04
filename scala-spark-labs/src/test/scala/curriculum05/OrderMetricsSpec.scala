package curriculum05

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.BeforeAndAfterAll

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{
    col,
    expr,
    sum
}
import org.apache.spark.storage.StorageLevel

class OrderMetricsSpec extends AnyFunSuite with BeforeAndAfterAll {
    private var spark: SparkSession = _


    override protected def beforeAll(): Unit = {
        super.beforeAll()

        spark = SparkSession.builder()
            .appName("OrderMetricsSpec")
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

    test("no name yet") {
        val session = spark

        import session.implicits._


        val orders = Seq(
                (101, 10, 2, 15.0),
                (102, 10, 1, 15.0),
                (103, 20, 3, 12.5),
                (104, 30, 4, 5.0)
            )
            .toDF(
                "order_id",
                "product_id",
                "quantity",
                "unit_price"
            )

        val preparedOrders =
            OrderMetrics.prepareOrders(orders)

        val totals =
            OrderMetrics.revenueTotalsByProduct(preparedOrders)

        val expensiveOrders =
            OrderMetrics.ordersAboveRevenue(preparedOrders, 25.0)


        println("=== UNPERSISTED TOTALS PLAN ===")
        totals.explain("formatted")

        println("=== UNPERSISTED THRESHOLD PLAN ===")
        expensiveOrders.explain("formatted")

        println("=== UNPERSISTED TOTALS EXTENDED PLAN ===")
        totals.explain("extended")

        println("=== UNPERSISTED THRESHOLD EXTENDED PLAN ===")
        expensiveOrders.explain("extended")

        val totalResults = totals
            .collect()
            .map(row => (
                row.getAs[Int]("product_id"),
                row.getAs[Double]("total_revenue")             
            ))
            .toSet
        
        assert(totalResults == Set(
            (10, 45.0),
            (20, 37.5),
            (30, 20.0)
        ))


        val expensiveOrdersResults = expensiveOrders
            .collect()
            .map(row => (
                row.getAs[Int]("order_id"),
                row.getAs[Int]("product_id"),
                row.getAs[Double]("revenue")             
            ))
            .toSet

        assert(expensiveOrdersResults == Set(
            (101, 10, 30.0),
            (103, 20, 37.5)
        ))
    }


    test("persisted prepared orders are reused by both consumers") {
        val session = spark

        import session.implicits._


        val orders = Seq(
                (101, 10, 2, 15.0),
                (102, 10, 1, 15.0),
                (103, 20, 3, 12.5),
                (104, 30, 4, 5.0)
            )
            .toDF(
                "order_id",
                "product_id",
                "quantity",
                "unit_price"
            )

        val preparedOrders = OrderMetrics
            .prepareOrders(orders)
            .persist(StorageLevel.MEMORY_AND_DISK)

        try {
            // Existing persisted test behavior goes here.

            assert(
                preparedOrders.storageLevel ==
                    StorageLevel.MEMORY_AND_DISK
            )
            assert(preparedOrders.count() == 4L)

            val totals =
                OrderMetrics.revenueTotalsByProduct(preparedOrders)

            val expensiveOrders =
                OrderMetrics.ordersAboveRevenue(preparedOrders, 25.0)


            println("=== PERSISTED TOTALS PLAN ===")
            totals.explain("formatted")

            println("=== PERSISTED THRESHOLD PLAN ===")
            expensiveOrders.explain("formatted")

            println("=== PERSISTED TOTALS EXTENDED PLAN ===")
            totals.explain("extended")

            println("=== PERSISTED THRESHOLD EXTENDED PLAN ===")
            expensiveOrders.explain("extended")

            val totalResults = totals
                .collect()
                .map(row => (
                    row.getAs[Int]("product_id"),
                    row.getAs[Double]("total_revenue")             
                ))
                .toSet
            
            assert(totalResults == Set(
                (10, 45.0),
                (20, 37.5),
                (30, 20.0)
            ))


            val expensiveOrdersResults = expensiveOrders
                .collect()
                .map(row => (
                    row.getAs[Int]("order_id"),
                    row.getAs[Int]("product_id"),
                    row.getAs[Double]("revenue")             
                ))
                .toSet

            assert(expensiveOrdersResults == Set(
                (101, 10, 30.0),
                (103, 20, 37.5)
            ))
            
        } finally {
            preparedOrders.unpersist(blocking = true)

            assert(
                preparedOrders.storageLevel ==
                    StorageLevel.NONE
            )
        }


        val recomputedExpensiveOrders = OrderMetrics
            .ordersAboveRevenue(preparedOrders, 25.0)
            .collect()
            .map(row =>
                (
                    row.getAs[Int]("order_id"),
                    row.getAs[Int]("product_id"),
                    row.getAs[Double]("revenue")
                )
            )
            .toSet

        assert(recomputedExpensiveOrders == Set(
            (101, 10, 30.0),
            (103, 20, 37.5)
        ))

    }

}