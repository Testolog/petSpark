name := "petSpark"

version := "0.1"

scalaVersion := "2.12.10"
val sparkVersion = "2.4.4"
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-hive" % sparkVersion,
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "com.github.dwickern" %% "scala-nameof" % "2.0.0"
)
