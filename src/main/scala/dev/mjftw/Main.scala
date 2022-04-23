package dev.mjftw

import cats.effect.IOApp
import cats.effect.{IO, ExitCode}
import dev.mjftw.io.Reader
import dev.mjftw.io.Writer
import dev.mjftw.logging.Logger
import fs2.io.file.Path
import dev.mjftw.controller.Controller
import cats.implicits._

object Main extends IOApp {

  /** This logger will be silently passed to any functions requiring a logger,
    * such as Reader.fileReader
    *
    * If we wanted we could change this logger to a different implementation.
    * E.g. to send logs to a centralised logging server or write them to a file.
    */
  implicit val logger = Logger.consoleLogger[IO]

  def app(inFile: Path, outFile: Path) = {
    // We are setup to read and write from from files, different Reader/Writer
    // implementations could be used here to change the data source and sink
    val reader = Reader.fileReader[IO](inFile)
    val writer = Writer.fileWriter[IO](outFile)

    // Run the data pipeline to completion
    Controller
      .pipeline(reader, writer)
      .compile
      .drain
  }

  val usage = """
    |usage: input-file output-file
    |
    |input-file:   Path to a file containing a set of commands to execute
    |output-file:  Path to a file to write the results
  """.stripMargin
  def run(args: List[String]): IO[ExitCode] =
    args match {
      // Get the first 2 command line arguments
      case inFile :: outFile :: _ =>
        for {
          _ <- logger.log("Starting data processing")
          _ <- app(Path(inFile), Path(outFile))
          _ <- logger.log("Finished processing data")
        } yield ExitCode.Success

      // Only 1 command line argument provided
      case _ :: _ =>
        logger
          .error("Must provide output-file")
          .flatTap(_ => IO.print(usage))
          .as(ExitCode.Error)

      // No command line arguments provided
      case _ =>
        logger
          .error("Must provide input-file and output-file")
          .flatTap(_ => IO.print(usage))
          .as(ExitCode.Error)
    }
}
