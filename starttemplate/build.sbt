// Используем 3.3.6, чтобы Metals был доволен (это LTS версия)
val scala3Version = "3.3.6"

lazy val root = project.in(file(".")).settings(
  name                     := "StartTemplate",
  version                  := "0.1.0-SNAPSHOT",
  scalaVersion             := scala3Version,
  Test / parallelExecution := false,

  // Библиотеки для тестов
  libraryDependencies += "org.scalactic"          %% "scalactic"   % "3.2.18",
  libraryDependencies += "org.scalatest"          %% "scalatest"   % "3.2.18" % "test",
  // Dependency Injection (Google Guice)
  libraryDependencies += "com.google.inject"       % "guice"       % "7.0.0",
  // XML support
  libraryDependencies += "org.scala-lang.modules" %% "scala-xml"   % "2.3.0", // 2.3.0
  // JSON support
  libraryDependencies += "org.playframework"      %% "play-json"   % "3.0.3", // 3.0.3
  // GUI DEPENDENCY
  libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
  coverageExcludedPackages                        := "stockpilot\\.view(\\..*)?;stockpilot\\.app(\\..*)?"
)
