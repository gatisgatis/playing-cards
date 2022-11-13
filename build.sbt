scalaVersion := "2.13.4"
name := "big-one"

import Dependencies._

libraryDependencies ++= Seq(
  ZIO.Std,
  ZIO.Http,
  Akka.ActorTyped,
  Akka.PersistenceTyped,
  Akka.PersistenceCassandra,
  LogbackClassic,
)

scalacOptions := Seq(
  "-Xsource:3",
  "-Ymacro-annotations",
)
