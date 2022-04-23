package dev.mjftw

import cats.Applicative
import fs2.Pipe
import dev.mjftw.codec.{DecodeFailure, Decoder}
import dev.mjftw.codec.Decoder._

object StreamUtils {

  /** A helper utility that takes a Stream of results of some operation that
    * could have either succeeded or failed.
    *
    * Failures are of type `Left[E]` and successes have the are of type
    * `Right[O]`. Any failures are passed to the function `fn` to be dealt with.
    * Only successful results are passed along the stream, failures are dropped.
    *
    * This is useful for applications like logging errors whilst allowing the
    * stream to recover and continue processing data.
    */
  def handleErrorsWith[F[_]: Applicative, E, O](
      fn: E => F[Unit]
  ): Pipe[F, Either[E, O], O] =
    inStream =>
      inStream
        .evalTap {
          case Left(error) => fn(error)
          case _           => Applicative[F].unit
        }
        .collect { case Right(visit) =>
          visit
        }
}
