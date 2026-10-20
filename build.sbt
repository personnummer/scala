import Dependencies._

ThisBuild / scalaVersion := "2.13.18"
ThisBuild / version := "3.1.0"
ThisBuild / organization := "personnummer"
ThisBuild / organizationName := "personnummer"

lazy val root = (project in file("."))
  .settings(
    name := "Personnummer",
    scalacOptions ++= Seq("-release", "8"),
    libraryDependencies += scalaTest % Test,
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % "0.13.0" % Test)
  )

ThisBuild / description := "Validate Swedish personal identity numbers."
ThisBuild / licenses := List(License.MIT)
ThisBuild / homepage := Some(url("https://personnummer.dev"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/personnummer/scala"),
    "scm:git@github.com:personnummer/scala.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id = "personnummer",
    name = "Personnummer and Contributors",
    email = "hello@personnummer.dev",
    url = url("http://personnummer.dev")
  )
)
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true
