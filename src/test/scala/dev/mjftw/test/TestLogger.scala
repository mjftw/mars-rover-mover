package dev.mjftw.test

import dev.mjftw.logging.Logger
import cats.Applicative

object TestLogger {

  /** Stub logger that just throws away the messages */
  def nopLogger[F[_]: Applicative]: Logger[F] = new Logger[F] {
    override def log(message: String): F[Unit] = Applicative[F].unit
    override def error(message: String, cause: Option[Throwable]): F[Unit] =
      Applicative[F].unit
  }
}
