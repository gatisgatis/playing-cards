//ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

import Dependencies._

libraryDependencies ++= Seq(
  ZIO.Http,
)

scalacOptions := Seq(
  "-Xsource:3",
  "-Ymacro-annotations",
)
