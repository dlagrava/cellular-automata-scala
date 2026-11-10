ThisBuild / scalaVersion := "3.8.3"

lazy val root = (project in file("."))
  .settings(
    name := "cellular-automata-scala",
    fork := true,
    Compile / run / mainClass := Some("run")
  )

libraryDependencies += "org.slf4j" % "slf4j-simple" % "2.0.7"
libraryDependencies += "org.scalameta" %% "munit" % "1.3.3" % Test
libraryDependencies += "org.scalafx" %% "scalafx" % "22.0.0-R33"
