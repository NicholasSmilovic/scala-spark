ThisBuild / scalaVersion := "2.13.18"
ThisBuild / organization := "learning.spark"
ThisBuild / version := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "scala-spark-labs",
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-sql" % "4.2.0",
      "org.scalatest" %% "scalatest" % "3.2.20" % Test
    ),
    Test / fork := true,
    Test / javaOptions +=
      "--add-opens=java.base/sun.util.calendar=ALL-UNNAMED",
    Compile / run / fork := true,
    Compile / run / outputStrategy := Some(StdoutOutput),
    Compile / run / javaOptions ++= Seq(
      "-Dspark.driver.bindAddress=127.0.0.1",
      "-Dspark.driver.host=127.0.0.1",
      "-Dspark.ui.enabled=false"
    )
  )
