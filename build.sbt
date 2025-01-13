val TinyScalaUtilsTest = "com.github.charpov" %% "tiny-scala-utils-test" % "1.7.0" % Test

val minJavaVersion = 23

ThisBuild / scalaVersion          := "3.7.1"
ThisBuild / crossPaths            := false
ThisBuild / transitiveClassifiers := Seq("sources")

ThisBuild / Test / fork                 := true
ThisBuild / Test / parallelExecution    := false
ThisBuild / Test / run / outputStrategy := Some(StdoutOutput)

ThisBuild / scalacOptions ++= Seq(
  "-deprecation",    // Emit warning and location for usages of deprecated APIs.
  "-encoding:utf-8", // Specify character encoding used by source files.
  "-feature",        // Emit warning for usages of features that should be imported explicitly.
  "-unchecked",      // Enable detailed unchecked (erasure) warnings.
  "-Wunused:linted", // Check unused imports and variables.
  "-preview",        // Enable the use of preview features anywhere in the project.
)

ThisBuild / resolvers += "TinyScalaUtils" at "https://charpov.github.io/TinyScalaUtils/maven/"
ThisBuild / libraryDependencies += {
   assert( // that the JVM meets the minimum required version
     sys.props("java.specification.version").toDouble >= minJavaVersion,
     s"Java $minJavaVersion or above is required for this project."
   )
   TinyScalaUtilsTest
}

lazy val program = (project in file(".")).settings(
  name := "sudoku",
  Test / javaOptions ++= Seq(
    "-Xmx8G",
    "--sun-misc-unsafe-memory-access=allow", // until Scala 3.8
  )
)
