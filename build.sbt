name := "akka-http-petstore"
organization := "io.github.manuzhang"
scalaVersion := "2.12.12"
version := "0.1.0-SNAPSHOT"

enablePlugins(JavaServerAppPackaging)

lazy val akkaHttpVersion = "10.2.4"
lazy val akkaVersion = "2.6.12"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "ch.megard" %% "akka-http-cors" % "1.1.1",
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.lihaoyi" %% "upickle" % "1.3.7",
  "org.rogach" %% "scallop" % "4.0.2",
  "io.getquill" %% "quill-jdbc" % "3.7.0",
  "mysql" % "mysql-connector-java" % "8.0.23",
  "com.h2database" % "h2" % "1.4.200",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "fr.davit" %% "akka-http-metrics-prometheus" % "1.5.1",
  "org.scalatest" %% "scalatest" % "3.2.10" % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "test"
)

mainClass in Compile := Some("io.github.manuzhang.petstore.Service")
discoveredMainClasses in Compile := Seq()
Test / fork := true

addCompilerPlugin(scalafixSemanticdb)
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"
scalacOptions ++= Seq(
  "-feature",
  "-language:existentials",
  "-Ywarn-unused-import",
  "-Xfatal-warnings"
)

ThisBuild / githubWorkflowBuildPreamble := Seq(
  WorkflowStep.Sbt(List("scalastyle", "test:scalastyle"), name = Some("Check scalastyle"))
)
ThisBuild / githubWorkflowPublishTargetBranches := Seq()