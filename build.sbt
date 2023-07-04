val zioVersion = "2.0.15"
val scala3Version = "3.3.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "sudoku-solver",
    version := "1.0",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-nio" % "2.0.1",
      "dev.zio" %% "zio-json" % "0.5.0",
      "io.circe" %% "circe-core" % "0.14.3",
      "io.circe" %% "circe-generic" % "0.14.3",
      "io.circe" %% "circe-parser" % "0.14.3"

      // Add other libraries like zio-nio and zip-json here if needed
    ).map(_ % Compile),
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.29"
    ).map(_ % Test)
  )
