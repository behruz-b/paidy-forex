package scala.forex.config

import java.net.URI

import scala.concurrent.duration.FiniteDuration
import scala.forex.redis.RedisConfig

case class ApplicationConfig(
    http: HttpConfig,
    oneFrame: OneFrameConfig,
    redis: RedisConfig,
  )

case class HttpConfig(
    host: String,
    port: Int,
    timeout: FiniteDuration,
  )

case class OneFrameConfig(
    uri: URI,
    token: String,
  )
