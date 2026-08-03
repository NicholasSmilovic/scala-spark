package curriculum03

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{
    DateType,
    DecimalType,
    IntegerType,
    StringType,
    StructField,
    StructType
}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import java.nio.file.{Files, Path}
import scala.jdk.CollectionConverters._

class OrderTransformsSpec extends AnyFunSuite with BeforeAndAfterAll {
    private var spark: SparkSession = _

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
        
    private def rawOrder(
        orderId: String,
        customerId: String = "customer-1",
        region: String = "North",
        product: String = "Widget",
        quantity: String = "1",
        unitPrice: String = "10.00",
        status: String = "completed",
        orderDate: String = "2026-08-03"
    ): Row =
        Row(
            orderId,
            customerId,
            region,
            product,
            quantity,
            unitPrice,
            status,
            orderDate
        )
    
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
        
    override protected def beforeAll(): Unit = {
        super.beforeAll()

        spark = SparkSession.builder()
            .appName("OrderTransformsSpec")
            .master("local[2]")
            .config("spark.driver.bindAddress", "127.0.0.1")
            .config("spark.driver.host", "127.0.0.1")
            .config("spark.ui.enabled", "false")
            .getOrCreate()
    }

    override protected def afterAll(): Unit = {
        try {
            if (spark != null) {
                spark.stop()
            }
        } finally {
            super.afterAll()
        }
    }

    test("counts a tiny in-memory DataFrame") {
        val session = spark
        import session.implicits._

        val orders = Seq(
                ("order-1", 2),
                ("order-2", 3)
            )
            .toDF("order_id", "quantity")

        assert(orders.count() == 2L)
    }

    test("normalizes strings and safely converts typed values") {
        
        val rawRows = Seq(
        rawOrder("101", quantity = "2", unitPrice="12.50"),
        rawOrder(
            orderId="102",
            customerId="customer-2",
            region="South",
            product="Gadget",
            quantity="two",
            unitPrice="9.99",
            status="cancelled",
            orderDate="not-a-date"
        )
        )

        val rawOrders = spark.createDataFrame(
            spark.sparkContext.parallelize(rawRows),
            rawOrderSchema
        )

        val normalized = OrderTransforms.normalizeAndType(rawOrders)

        val rowsById = normalized
            .collect()
            .map(row => row.getAs[Int]("order_id_typed") -> row)
            .toMap

        val valid = rowsById(101)
        val malformed = rowsById(102)


        assert(valid.getAs[String]("customer_id_clean") == "customer-1")
        assert(valid.getAs[String]("region_clean") == "North")
        assert(valid.getAs[Int]("quantity_typed") == 2)
        assert(valid.getDecimal(valid.fieldIndex("unit_price_typed")).toPlainString == "12.50")
        assert(valid.getDate(valid.fieldIndex("order_date_typed")).toString == "2026-08-03")

        assert(malformed.getAs[String]("quantity") == "two")
        assert(malformed.isNullAt(malformed.fieldIndex("quantity_typed")))
        assert(malformed.getAs[String]("order_date") == "not-a-date")
        assert(malformed.isNullAt(malformed.fieldIndex("order_date_typed")))

        assert(normalized.schema("order_id_typed").dataType == IntegerType)
        assert(normalized.schema("quantity_typed").dataType == IntegerType)
        assert(normalized.schema("unit_price_typed").dataType == DecimalType(10, 2))
        assert(normalized.schema("order_date_typed").dataType == DateType)

    }

    test("assigns exact rejection reasons with first-match precedence") {
        val rawRows = Seq(
            rawOrder("100"),
            rawOrder("bad-id"),
            rawOrder("-1"),
            rawOrder("101", quantity = "two"),
            rawOrder("102", quantity = "0"),
            rawOrder("103", unitPrice = null),
            rawOrder("104", unitPrice = "not-a-decimal"),
            rawOrder("105", unitPrice = "0"),
            rawOrder("106", orderDate = "not-a-date"),
            rawOrder("107", customerId = " "),
            rawOrder("108", product = null),
            rawOrder("109", region = "Central"),
            rawOrder("110", status = "pending"),
            rawOrder("111", quantity = "two", region = "Central")
        )

        val rawOrders = spark.createDataFrame(
            spark.sparkContext.parallelize(rawRows),
            rawOrderSchema
        )

        val normalized = OrderTransforms.normalizeAndType(rawOrders)
        val validated = OrderTransforms.withRejectionReason(normalized)

        val actualReasons = validated
            .select("order_id", "rejection_reason")
            .collect()
            .map(row =>
                row.getAs[String]("order_id") ->
                row.getAs[String]("rejection_reason")
            )
            .toMap

        val expectedReasons = Map(
            "100" -> null,
            "bad-id" -> "order_id must be a positive integer",
            "-1" -> "order_id must be a positive integer",
            "101" -> "quantity is not an integer",
            "102" -> "quantity must be greater than zero",
            "103" -> "unit_price is missing",
            "104" -> "unit_price is not a decimal",
            "105" -> "unit_price must be greater than zero",
            "106" -> "order_date is invalid",
            "107" -> "customer_id is missing",
            "108" -> "product is missing",
            "109" -> "region is invalid",
            "110" -> "status is invalid",
            "111" -> "quantity is not an integer"
        )

        assert(actualReasons == expectedReasons)

        val valid = OrderTransforms.validOrders(validated)
        val rejected = OrderTransforms.rejectedOrders(validated)
        val curated = OrderTransforms.curatedOrders(valid)

        val validIds = valid
            .select("order_id")
            .collect()
            .map(_.getAs[String]("order_id"))
            .toSet

        val rejectedIds = rejected
            .select("order_id")
            .collect()
            .map(_.getAs[String]("order_id"))
            .toSet

        val curatedIds = curated
            .select("order_id")
            .collect()
            .map(_.getAs[Int]("order_id"))
            .toSet

        val expectedRejectedIds = expectedReasons.collect {
                case (id, reason) if reason != null => id
            }.toSet

        assert(validIds == Set("100"))
        assert(rejectedIds == expectedRejectedIds)
        assert(curatedIds == Set(100))

        val totalCount = validated.count()
        val validCount = valid.count()
        val rejectedCount = rejected.count()

        assert(totalCount == expectedReasons.size.toLong)
        assert(validCount + rejectedCount == totalCount)
    }


    test("writes and reads Parquet beneath a temporary directory") {
        val tempDir = Files.createTempDirectory("curriculum03-")

        try {
            val rawRows = Seq(
                rawOrder(
                    "201",
                    customerId = "customer-201",
                    region = "North",
                    quantity = "2",
                    unitPrice = "12.50"
                ),
                rawOrder(
                    "202",
                    customerId = "customer-202",
                    region = "South",
                    product = "Gadget",
                    quantity = "3",
                    unitPrice = "8.25",
                    status = "cancelled"
                ),
                rawOrder("203", region = "Central")
            )

            val rawOrders = spark.createDataFrame(
                spark.sparkContext.parallelize(rawRows),
                rawOrderSchema
            )

            val normalized = OrderTransforms.normalizeAndType(rawOrders)
            val validated = OrderTransforms.withRejectionReason(normalized)
            val valid = OrderTransforms.validOrders(validated)
            val rejected = OrderTransforms.rejectedOrders(validated)
            val curated = OrderTransforms.curatedOrders(valid)

            OrderTransforms.writeOutputs(
                curated,
                rejected,
                tempDir.toString
            )

            val savedCurated =
                spark.read.parquet(s"${tempDir.toString}/curated")

            val savedRejected =
                spark.read.parquet(s"${tempDir.toString}/rejected")

            val curatedResults = savedCurated
                .select(
                    "order_id",
                    "quantity",
                    "unit_price",
                    "order_date",
                    "customer_id",
                    "region",
                    "product",
                    "status"
                )
                .collect()
                .map(row =>
                    (
                        row.getAs[Int]("order_id"),
                        row.getAs[Int]("quantity"),
                        row.getDecimal(row.fieldIndex("unit_price")).toPlainString,
                        row.getDate(row.fieldIndex("order_date")).toString,
                        row.getAs[String]("customer_id"),
                        row.getAs[String]("region"),
                        row.getAs[String]("product"),
                        row.getAs[String]("status")
                    )
                )
                .toSet

            assert(curatedResults == Set(
                (
                    201,
                    2,
                    "12.50",
                    "2026-08-03",
                    "customer-201",
                    "North",
                    "Widget",
                    "completed"
                ),
                (
                    202,
                    3,
                    "8.25",
                    "2026-08-03",
                    "customer-202",
                    "South",
                    "Gadget",
                    "cancelled"
                )
            ))

            val rejectedResults = savedRejected
                .select("order_id", "rejection_reason")
                .collect()
                .map(row =>
                    (
                        row.getAs[String]("order_id"),
                        row.getAs[String]("rejection_reason")
                    )
                )
                .toSet

            assert(rejectedResults == Set(
                ("203", "region is invalid")
            ))

            assert(savedCurated.schema("order_id").dataType == IntegerType)
            assert(savedCurated.schema("quantity").dataType == IntegerType)
            assert(
                savedCurated.schema("unit_price").dataType ==
                    DecimalType(10, 2)
            )
            assert(savedCurated.schema("order_date").dataType == DateType)

            val directoryStream =
                Files.list(tempDir.resolve("curated"))

            try {
                val regionDirectories =
                    directoryStream.iterator().asScala
                        .filter(path => Files.isDirectory(path))
                        .map(_.getFileName.toString)
                        .filter(_.startsWith("region="))
                        .toSet

                assert(regionDirectories == Set(
                    "region=North",
                    "region=South"
                ))
            } finally {
                directoryStream.close()
            }
        } finally {
            deleteRecursively(tempDir)
        }
    }
}