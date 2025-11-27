// Используем 3.3.6, чтобы Metals был доволен (это LTS версия)
val scala3Version = "3.3.6"

lazy val root = project
  .in(file("."))
  .settings(
    name := "StartTemplate",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,

    // Библиотеки для тестов
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.18",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % "test",

    // --- НАСТРОЙКА ПОКРЫТИЯ (Двойная защита) ---

    // 1. Исключаем по именам файлов (физически)
    // Это сработает, даже если мы ошиблись с именем пакета.
    coverageExcludedFiles := ".*Main.scala;.*CLIView.scala",

    // 2. Исключаем по пакетам (логически)
    // stockpilot.view.* -> весь UI
    // stockpilot.app.main -> класс, генерируемый @main (с маленькой буквы!)
    // stockpilot.app.Main -> на случай, если это объект
    coverageExcludedPackages := "stockpilot\\.view\\..*;stockpilot\\.app\\.main;stockpilot\\.app\\.Main"
  )
