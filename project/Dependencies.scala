import sbt.*

object Dependencies {

  object Versions {
    val cats       = "2.9.0"
    val catsEffect = "3.4.8"
    val fs2        = "3.7.0"
    val http4s     = "0.23.16"
    val circe      = "0.14.2"
    val pureConfig = "0.17.4"
    val redis4cats = "1.1.1"
    val enumeratum = "1.7.3"
    val sttp       = "3.7.2"

    val betterMonadicFor = "0.3.1"
    val kindProjector    = "0.13.2"
    val logback          = "1.4.7"
    val scalaCheck       = "1.17.0"
    val scalaTest        = "3.2.15"
    val catsScalaCheck   = "0.3.2"
  }

  object Libraries {
    def circe(artifact: String): ModuleID  = "io.circe"   %% artifact % Versions.circe
    def http4s(artifact: String): ModuleID = "org.http4s" %% artifact % Versions.http4s
    private def enumeratum(artifact: String): ModuleID =
      "com.beachape" %% artifact % Versions.enumeratum

    private def redis4cats(artifact: String): ModuleID =
      "dev.profunktor" %% artifact % Versions.redis4cats
    private def sttp(artifact: String): ModuleID =
      "com.softwaremill.sttp.client3" %% artifact % Versions.sttp

    lazy val cats       = "org.typelevel" %% "cats-core"   % Versions.cats
    lazy val catsEffect = "org.typelevel" %% "cats-effect" % Versions.catsEffect
    lazy val fs2        = "co.fs2"        %% "fs2-core"    % Versions.fs2

    lazy val http4sDsl                  = http4s("http4s-dsl")
    lazy val http4sServer               = http4s("http4s-blaze-server")
    lazy val http4sCirce                = http4s("http4s-circe")
    lazy val circeCore                  = circe("circe-core")
    lazy val circeGeneric               = circe("circe-generic")
    lazy val circeGenericExt            = circe("circe-generic-extras")
    lazy val circeParser                = circe("circe-parser")
    lazy val redisCatsEffects: ModuleID = redis4cats("redis4cats-effects")
    lazy val redisLog4cats: ModuleID    = redis4cats("redis4cats-log4cats")
    lazy val enumeratumCore: ModuleID   = enumeratum("enumeratum")
    lazy val enumeratumCirce: ModuleID  = enumeratum("enumeratum-circe")
    lazy val enumeratumCats: ModuleID   = enumeratum("enumeratum-cats")
    lazy val pureConfig                 = "com.github.pureconfig" %% "pureconfig" % Versions.pureConfig
    lazy val sttpCirce: ModuleID        = sttp("circe")
    lazy val sttpFs2Backend: ModuleID   = sttp("async-http-client-backend-fs2")
    // Compiler plugins
    lazy val kindProjector    = "org.typelevel" %% "kind-projector"     % Versions.kindProjector cross CrossVersion.full
    lazy val betterMonadicFor = "com.olegpy"    %% "better-monadic-for" % Versions.betterMonadicFor

    // Runtime
    lazy val logback = "ch.qos.logback" % "logback-classic" % Versions.logback

    // Test
    lazy val scalaTest      = "org.scalatest"     %% "scalatest"       % Versions.scalaTest
    lazy val scalaCheck     = "org.scalacheck"    %% "scalacheck"      % Versions.scalaCheck
    lazy val catsScalaCheck = "io.chrisdavenport" %% "cats-scalacheck" % Versions.catsScalaCheck
  }

}