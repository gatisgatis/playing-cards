import sbt._

object Dependencies {
  val zioVersion = "2.0.0"
  val zHttpVersion = "2.0.0-RC10"

  object ZIO {
    val Std = "dev.zio" %% "zio" % zioVersion
    val Streams = "dev.zio" %% "zio-streams" % zioVersion
    val test = "dev.zio" %% "zio-test" % zioVersion % Test
    val testSbt = "dev.zio" %% "zio-test-sbt" % zioVersion % Test
    val Http = "io.d11" %% "zhttp" % zHttpVersion
    val HttpTest = "io.d11" %% "zhttp-test" % zHttpVersion % Test
  }

}
