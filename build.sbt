val scala3Version = "3.7.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "durak_ronny",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % "test",
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
    libraryDependencies += "org.scalafx" %% "scalafx" % "19.0.0-R30",

    coverageExcludedFiles := ".*Main|.*gui.*"
  )
