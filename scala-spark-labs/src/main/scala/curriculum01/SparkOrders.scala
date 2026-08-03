package curriculum01

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.functions.sum
import org.apache.spark.sql.functions.count


object SparkOrders {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("Spark Orders")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    try {
      println(s"Spark ${spark.version} is running.")
      println(s"Master: ${spark.sparkContext.master}")


      val orders = spark.read
        .option("header", "true")
        .option("inferSchema", "true")
        .csv("data/curriculum-01/orders.csv")

      orders.printSchema()


      val validOrders = orders
        .filter(col("status") =!= "cancelled")
        .filter(col("quantity") > 0)
        .withColumn("revenue", col("quantity") * col("unit_price"))


      val regionGroup = validOrders
        .groupBy(col("region"))
        .agg(
          sum("revenue").alias("total_revenue"),
          count("*").alias("order_count")
        )
        .orderBy(col("total_revenue").desc)

      regionGroup.explain("formatted")

      
      regionGroup.show()

    } finally {
      spark.stop()
    }
  }
}
