//ThisBuild / version := "0.1.0-SNAPSHOT"

scalaVersion := "2.13.10"

import Dependencies._

libraryDependencies ++= Seq(
  ZIO.Http,
  Akka.ActorTyped,
  Akka.PersistenceTyped,
)

scalacOptions := Seq(
  "-Xsource:3",
  "-Ymacro-annotations",
)
