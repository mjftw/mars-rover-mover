package dev.mjftw.test

import dev.mjftw.io.Reader
import fs2.Stream
import cats.effect.IO
import dev.mjftw.model.CommandContext
import cats.Applicative

object TestReader {
  implicit def logger[F[_]: Applicative] = TestLogger.nopLogger[F]

  /** Reader tha reads input from a list of strings */
  def stringReader(input: List[String]): Reader[IO, CommandContext] =
    Reader.commandContextReader(
      new Reader[IO, String] {
        override def read: Stream[IO, String] =
          Stream.emits(input)
      }
    )
}
