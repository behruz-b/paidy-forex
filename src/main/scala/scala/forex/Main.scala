package scala.forex

import scala.concurrent.ExecutionContext
import cats.effect._

import forex.config._
import fs2.Stream
import org.http4s.blaze.server.BlazeServerBuilder
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.fs2.AsyncHttpClientFs2Backend

import scala.concurrent.ExecutionContext.global

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    new Application[IO].stream(global).compile.drain.as(ExitCode.Success)

}

class Application[F[_]: Async] {

  def stream(ec: ExecutionContext): Stream[F, Unit] =
    for {
      config <- Config.stream("app")
      implicit0(sttpBackend: SttpBackend[F, Any]) <- Stream.resource(AsyncHttpClientFs2Backend.resource())
      module = new Module[F](config)
      _ <- BlazeServerBuilder[F]
            .withExecutionContext(ec)
            .bindHttp(config.http.port, config.http.host)
            .withHttpApp(module.httpApp)
            .serve
    } yield ()

}
