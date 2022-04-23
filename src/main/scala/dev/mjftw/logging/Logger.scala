package dev.mjftw.logging

import cats.effect.kernel.Sync

/** Logging interface, allowing new logging implementations to be added later
  * with minimal effort.
  *
  * E.g. We may want to write logs to the console in a specific format or send
  * logs directly to a centralised logging server.
  */
trait Logger[F[_]] {
  def log(message: String): F[Unit]
  def error(message: String, cause: Option[Throwable] = None): F[Unit]
}

object Logger {

  /** The default logger, write to Console's Stdout and Stderr */
  def consoleLogger[F[_]: Sync]: Logger[F] = new Logger[F] {
    override def log(message: String): F[Unit] =
      Sync[F].delay(Console.out.println(message))

    override def error(
        message: String,
        cause: Option[Throwable] = None
    ): F[Unit] =
      Sync[F].delay {
        Console.err.println(s"Error: ${message}")
        cause.collect(_.printStackTrace(Console.err))
      }
  }
}
