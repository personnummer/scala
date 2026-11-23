import Dependencies._

ThisBuild / scalaVersion := "2.13.18"
ThisBuild / version := "3.1.0"
ThisBuild / organization := "personnummer"
ThisBuild / organizationName := "personnummer"

lazy val root = (project in file("."))
  .settings(
    name := "Personnummer",
    libraryDependencies += scalaTest % Test,
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % "0.13.0" % Test)
  )

ThisBuild / description := "Validate Swedish personal identity numbers."
ThisBuild / licenses := List(
  "MIT" -> uri("https://opensource.org/licenses/MIT")
)
ThisBuild / homepage := Some(uri("https://personnummer.dev"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    uri("https://github.com/personnummer/scala"),
    "scm:git@github.com:personnummer/scala.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id = "personnummer",
    name = "Personnummer and Contributors",
    email = "hello@personnummer.dev",
    url = uri("http://personnummer.dev")
  )
)
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
  if (isSnapshot.value) Some("central-snapshots" at centralSnapshots)
  else localStaging.value
}
ThisBuild / publishMavenStyle := true
