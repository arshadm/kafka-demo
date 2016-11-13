
lazy val commonSettings = Seq(
  organization := "io.spinor",
  version := "1.0.0-SNAPSHOT",
  scalaVersion := "2.11.8"
)

lazy val kafkaDemo = (project in file(".")).
  enablePlugins(JavaAppPackaging).
  settings(commonSettings: _*).
  settings(
    name := "kafka-demo",
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.3.1",
      "org.apache.kafka" %% "kafka" % "0.10.1.0",
      "com.google.guava" % "guava" % "20.0",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
      "ch.qos.logback" % "logback-classic" % "1.1.3",
      "org.scalatest" %% "scalatest" % "2.2.6" % "test"
    )
  )

fork in run := true
