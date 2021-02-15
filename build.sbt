name := "PetSpark"

version := "0.1"

scalaVersion := "2.12.10"
val sparkVersion = "2.4.4"
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-hive" % sparkVersion,
  "org.apache.spark" % "spark-sql-kafka-0-10_2.12" % sparkVersion,
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "com.typesafe.akka" %% "akka-stream" % "2.6.12",
  "com.typesafe.akka" %% "akka-stream-kafka" % "2.0.7",
  "com.github.dwickern" %% "scala-nameof" % "2.0.0",
  "org.apache.kafka" %% "kafka" % "2.1.0"
)
