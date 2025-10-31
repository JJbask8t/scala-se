val scala3Version = "3.7.3"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % "test"

//addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")

lazy val root = project.in(file(".")).settings(
  name                                   := "StartTemplate",
  version                                := "0.1.0-SNAPSHOT",
  scalaVersion                           := scala3Version,
  libraryDependencies += "org.scalameta" %% "munit" % "1.0.0",
  libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test
)
