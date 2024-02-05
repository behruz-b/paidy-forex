package scala.forex.redis

import cats._
import cats.implicits.toFunctorOps
import dev.profunktor.redis4cats.RedisCommands
import io.circe.{ Encoder, Printer }
import io.circe.syntax.EncoderOps

import scala.concurrent.duration.FiniteDuration

trait RedisClient[F[_]] {
  def put(
      key: String,
      value: String,
      expire: FiniteDuration,
  ): F[Unit]

  def put[A: Encoder](
      key: String,
      value: A,
      expire: FiniteDuration,
  ): F[Unit]

  def get(key: String): F[Option[String]]

  def del(key: String*): F[Unit]
}

object RedisClient {
  private val printer: Printer = Printer.spaces2.copy(dropNullValues = true)

  def apply[F[_]: MonadThrow](
      redis: RedisCommands[F, String, String],
      prefix: String,
  ): RedisClient[F] =
    new RedisClient[F] {
      override def put(
          key: String,
          value: String,
          expire: FiniteDuration,
      ): F[Unit] = redis.setEx(s"$prefix:$key", value, expire)

      override def put[A: Encoder](
          key: String,
          value: A,
          expire: FiniteDuration,
      ): F[Unit] =
        redis.setEx(s"$prefix:$key", value.asJson.printWith(printer), expire)

      override def get(key: String): F[Option[String]] = redis.get(s"$prefix:$key")

      override def del(key: String*): F[Unit] = redis.del(key.map(k => s"$prefix:$k"): _*).void
    }
}
