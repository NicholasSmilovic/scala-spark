package curriculum04

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{
    col,
    trim,
    expr
}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{
    IntegerType,
    StringType,
    StructField,
    StructType
}



import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.BeforeAndAfterAll



class OrderEnrichmentSpec extends AnyFunSuite with BeforeAndAfterAll {
    private var spark: SparkSession = _

    override protected def beforeAll(): Unit = {
        super.beforeAll()

        spark = SparkSession.builder()
            .appName("OrderEnrichmentSpec")
            .master("local[2]")
            .config("spark.driver.bindAddress", "127.0.0.1")
            .config("spark.driver.host", "127.0.0.1")
            .config("spark.ui.enabled", "false")
            .config("spark.sql.autoBroadcastJoinThreshold", "-1")
            /*
                This prevents Spark from automatically broadcasting the tiny test fixture.
                It lets the unhinted join show a shuffle strategy while the explicit
                broadcast(products) hint still produces a broadcast strategy. This is a 
                test control, not a production recommendation.
            */
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

    test("inner join enriches matched orders and excludes unmatched orders") {
        val session = spark

        import session.implicits._

        val orders = Seq(
                (101, 10, 2),
                (102, 20, 1),
                (103, 999, 4)
            )
            .toDF("order_id", "product_id", "quantity")

        val products = Seq(
            (10, "Keyboard", "Accessories"),
            (20, "Mouse", "Accessories")
        )
        .toDF("product_id", "product_name", "category")


        assert(orders.schema("product_id").dataType == IntegerType)
        assert(products.schema("product_id").dataType == IntegerType)



        val curatedResults = OrderEnrichment
            .innerEnrichOrders(
                orders,
                products
            )
            .collect()
            .map(
                row => (
                    row.getAs[Int]("order_id"),
                    row.getAs[Int]("product_id"),
                    row.getAs[Int]("quantity"),
                    row.getAs[String]("product_name"),
                    row.getAs[String]("category"),
                )
            )
            .toSet

            assert(curatedResults == Set(
                (101, 10, 2, "Keyboard", "Accessories"),
                (102, 20, 1, "Mouse", "Accessories")
                
            ))


        val matchedOrderIds = curatedResults.map(_._1)
        assert(matchedOrderIds == Set(101, 102))
          
    }

    test("broadcast inner join enriches matched orders and excludes unmatched orders") {
        val session = spark

        import session.implicits._

        val orders = Seq(
                (101, 10, 2),
                (102, 20, 1),
                (103, 999, 4)
            )
            .toDF("order_id", "product_id", "quantity")

        val products = Seq(
            (10, "Keyboard", "Accessories"),
            (20, "Mouse", "Accessories")
        )
        .toDF("product_id", "product_name", "category")


        assert(orders.schema("product_id").dataType == IntegerType)
        assert(products.schema("product_id").dataType == IntegerType)



        val unhintedEnriched =
            OrderEnrichment.innerEnrichOrders(orders, products)

        val broadcastEnriched =
            OrderEnrichment.innerBroadcastEnrichOrders(orders, products)

        println("=== UNHINTED JOIN PLAN ===")
        unhintedEnriched.explain("formatted")

        println("=== BROADCAST JOIN PLAN ===")
        broadcastEnriched.explain("formatted")

        val unhintedResults = unhintedEnriched
            .collect()
            .map(row =>
                (
                    row.getAs[Int]("order_id"),
                    row.getAs[Int]("product_id"),
                    row.getAs[Int]("quantity"),
                    row.getAs[String]("product_name"),
                    row.getAs[String]("category")
                )
            )
            .toSet

        val broadcastResults = broadcastEnriched
            .collect()
            .map(
                row => (
                    row.getAs[Int]("order_id"),
                    row.getAs[Int]("product_id"),
                    row.getAs[Int]("quantity"),
                    row.getAs[String]("product_name"),
                    row.getAs[String]("category"),
                )
            )
            .toSet

        val expectedResults = Set(
            (101, 10, 2, "Keyboard", "Accessories"),
            (102, 20, 1, "Mouse", "Accessories")
        )

        assert(unhintedResults == expectedResults)
        assert(broadcastResults == expectedResults)
        assert(broadcastResults == unhintedResults)

        val matchedOrderIds = broadcastResults.map(_._1)

        assert(matchedOrderIds == Set(101, 102))
          
    }


    test("left join") {
        val session = spark

        import session.implicits._

        val orders = Seq(
                (101, 10, 2),
                (102, 20, 1),
                (103, 999, 4)
            )
            .toDF("order_id", "product_id", "quantity")

        val products = Seq(
                (10, "Keyboard", "Accessories"),
                (20, "Mouse", "Accessories")
            )
            .toDF("product_id", "product_name", "category")

        val leftEnriched = OrderEnrichment
            .leftEnrichOrders(
                orders,
                products
            )
        val rowsByOrderId = leftEnriched
            .collect()
            .map(row => row.getAs[Int]("order_id") -> row)
            .toMap

        assert(leftEnriched.count() == orders.count())
        assert(rowsByOrderId.keySet == Set(101, 102, 103))

        val keyboardOrder = rowsByOrderId(101)

        assert(keyboardOrder.getAs[String]("product_name") == "Keyboard")
        assert(keyboardOrder.getAs[String]("category") == "Accessories")

        val mouseOrder = rowsByOrderId(102)

        assert(mouseOrder.getAs[String]("product_name") == "Mouse")
        assert(mouseOrder.getAs[String]("category") == "Accessories")


        val unmatched = rowsByOrderId(103)
        assert(unmatched.getAs[Int]("product_id") == 999)
        assert(unmatched.isNullAt(unmatched.fieldIndex("product_name")))
        assert(unmatched.isNullAt(unmatched.fieldIndex("category")))

    }

    test("duplicate product keys multiply matching order rows") {
        val session = spark;

        import session.implicits._

        val orders = Seq(
                (101, 10, 2),
                (102, 20, 1)
            )
            .toDF("order_id", "product_id", "quantity")

        val products = Seq(
                (10, "Keyboard", "Accessories"),
                (10, "Mechanical Keyboard", "Accessories"),
                (20, "Mouse", "Accessories")
            )
            .toDF("product_id", "product_name", "category")


        val duplicateJoinRows = OrderEnrichment
            .innerEnrichOrders(
                orders,
                products
            )
            .collect()
            .map( row => (
                row.getAs[Int]("order_id"),
                row.getAs[Int]("product_id"),
                row.getAs[Int]("quantity"),
                row.getAs[String]("product_name"),
                row.getAs[String]("category")
            ))
            .toSet

        assert(duplicateJoinRows == Set(
            (101, 10, 2, "Keyboard", "Accessories"),
            (101, 10, 2, "Mechanical Keyboard", "Accessories"),
            (102, 20, 1, "Mouse", "Accessories")
        ))

        val multiplicityByOrderId = duplicateJoinRows
            .groupBy(_._1)
            .view
            .mapValues(_.size)
            .toMap

        assert(multiplicityByOrderId == Map(
            101 -> 2,
            102 -> 1
        ))
    


        val duplicateKeys = OrderEnrichment
            .duplicateProductKeys(products)
            .collect()
            .map(row =>
                (
                    row.getAs[Int]("product_id"),
                    row.getAs[Long]("occurrence_count")
                )
            )
            .toSet

        assert(duplicateKeys == Set(
            (10, 2L)
        ))
    }


}