package dev.mjftw.io

import dev.mjftw.StreamUtils.handleErrorsWith
import dev.mjftw.model.{Grid, CommandSet, CommandContext}
import dev.mjftw.logging.Logger
import dev.mjftw.codec.Decoder._
import dev.mjftw.codec.Encoder._

import fs2.Stream
import fs2.text
import fs2.io.file.{Files, Path}
import cats.Applicative

/** Interface to represent pulling commands from a data source such as a file,
  * websocket etc.
  */
trait Reader[F[_], O] {
  def read: Stream[F, O]
}

object Reader {

  /** An implementation of the Reader interface that reads strings from a file
    * and decodes them to CommandContext objects
    */
  def fileReader[F[_]: Files: Applicative: Logger](
      file: Path
  ): Reader[F, CommandContext] =
    commandContextReader(
      fileStringReader(file)
    )

  /** Create a Reader to produce a Stream of CommandContext for for any Reader
    * that produces a Stream of Strings by decoding the stream
    */
  def commandContextReader[F[_]: Applicative](reader: Reader[F, String])(
      implicit logger: Logger[F]
  ): Reader[F, CommandContext] = new Reader[F, CommandContext] {
    override def read: Stream[F, CommandContext] = {
      val lines = reader.read.filter(!_.isBlank())

      // Take the first line and decode it to a grid
      val grid = lines
        .take(1)
        .map(_.decode[Grid])
        .through(
          handleErrorsWith(err =>
            logger.error("Failed to decode grid", Some(err))
          )
        )

      // Skip the first line (grid) and decode the rest as command sets
      val commandSets = lines
        .drop(1)
        .map(_.decode[CommandSet])
        .through(
          handleErrorsWith(error =>
            logger.error("Failed to decode rover command set", Some(error))
          )
        )

      // Package the Grid with every CommandSet to create a stream of CommandContexts
      // The CommandContext independently contains everything required to run all the
      // commands for a given rover and find its final location
      grid.repeat
        .zip(commandSets)
        .map { case (grid, commandSet) => CommandContext(grid, commandSet) }
    }
  }

  /** An implementation of the Reader interface that reads strings from a file
    */
  implicit def fileStringReader[F[_]: Files: Applicative](
      file: Path
  )(implicit logger: Logger[F]): Reader[F, String] =
    new Reader[F, String] {
      override def read: Stream[F, String] = {
        // Get the lines from the file, assuming utf8 encoding
        Files[F]
          .readAll(file)
          .through(text.utf8.decode)
          .through(text.lines)
      }

    }
}
