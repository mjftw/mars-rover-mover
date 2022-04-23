package dev.mjftw.test

import dev.mjftw.io.Writer
import cats.effect.IO
import cats.effect.kernel.Ref
import fs2.{Pipe, Stream}
import dev.mjftw.model.Rover

object TestWriter {

  /** Writer that writes to a String */
  def stringWriter(ref: Ref[IO, String]): Writer[IO, Rover] =
    Writer.roverWriter(
      new Writer[IO, String] {
        override def write: Pipe[IO, String, Unit] =
          inStream => inStream.flatMap(s => Stream.eval(ref.update(_ + s)))

      }
    )
}
