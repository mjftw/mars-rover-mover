package dev.mjftw.io

import dev.mjftw.model.Rover
import dev.mjftw.codec.Encoder._
import fs2.io.file.Path
import fs2.io.file.Files
import fs2.text
import fs2.{Stream, Pipe}

/** Interface to represent a data sink, writing it to a file, websocket, etc */
trait Writer[F[_], I] {
  def write: Pipe[F, I, Unit]
}

object Writer {

  def fileWriter[F[_]: Files](output: Path): Writer[F, Rover] = roverWriter(
    fileStringWriter(output)
  )

  def roverWriter[F[_]](writer: Writer[F, String]): Writer[F, Rover] =
    new Writer[F, Rover] {
      override def write: Pipe[F, Rover, Unit] =
        inStream =>
          writer.write(
            inStream
              .map(_.encode)
              .intersperse("\n")
          )
    }

  // An implementation of the writer interface that writes strings to a file
  def fileStringWriter[F[_]: Files](output: Path) = new Writer[F, String] {
    override def write: Pipe[F, String, Unit] =
      inStream =>
        inStream
          .through(text.utf8.encode)
          .through(Files[F].writeAll(output))
  }
}
