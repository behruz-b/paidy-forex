package scala.forex

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.global
import scala.forex.redis.RedisClient

import cats.effect._
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.effect.Log.NoOp.instance
import forex.config._
import fs2.Stream
import org.http4s.blaze.server.BlazeServerBuilder
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.fs2.AsyncHttpClientFs2Backend

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    new Application[IO].stream(global).compile.drain.as(ExitCode.Success)
}

class Application[F[_]: Async] {
  def stream(ec: ExecutionContext): Stream[F, Unit] =
    for {
      config <- Config.stream("app")
      implicit0(redis: RedisClient[F]) <- Stream.resource(
        Redis[F].utf8(config.redis.uri.toString).map(RedisClient[F](_, config.redis.prefix))
      )

      implicit0(sttpBackend: SttpBackend[F, Any]) <- Stream.resource(
        AsyncHttpClientFs2Backend.resource()
      )
      module = new Module[F](config)
      _ <- BlazeServerBuilder[F]
        .withExecutionContext(ec)
        .bindHttp(config.http.port, config.http.host)
        .withHttpApp(module.httpApp)
        .serve
    } yield ()
}
