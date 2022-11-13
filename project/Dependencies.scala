import sbt._

object Dependencies {
  val ZioVersion = "2.0.0"
  val ZioHttpVersion = "2.0.0-RC7"

  object ZIO {
    val Std = "dev.zio" %% "zio" % ZioVersion
//    val Streams = "dev.zio" %% "zio-streams" % ZioVersion
//    val test = "dev.zio" %% "zio-test" % ZioVersion % Test
//    val testSbt = "dev.zio" %% "zio-test-sbt" % ZioVersion % Test
    val Http = "io.d11" %% "zhttp" % ZioHttpVersion
  }

  val AkkaVersion = "2.7.0"

  object Akka {
    val ActorTyped = "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion
    val PersistenceTyped = "com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion
    val PersistenceCassandra = "com.typesafe.akka" %% "akka-persistence-cassandra" % "1.1.0"

  }

  val LogbackClassic = "ch.qos.logback" % "logback-classic" % "1.4.4"

}
