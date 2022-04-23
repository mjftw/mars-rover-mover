package dev.mjftw

import cats.effect.{IO, SyncIO}
import munit.CatsEffectSuite
import dev.mjftw.controller.Controller
import dev.mjftw.io.Reader
import cats.Id
import fs2.Stream
import dev.mjftw.test.TestReader
import dev.mjftw.test.TestWriter
import cats.effect.kernel.Ref
import scala.concurrent.duration._
import fs2.io.file.Path
import dev.mjftw.test.TestLogger

// Top level black box tests for Controller.pipeline
class ControllerSpec extends CatsEffectSuite {
  implicit val logger = TestLogger.nopLogger[IO]

  val goldenInOut = Map(
    (
      """|4 8
         |(2, 3, E) LFRFF
         |(0, 2, N) FFLFRFF""".stripMargin,
      """|(4, 4, E)
         |(0, 4, W) LOST""".stripMargin
    ),
    (
      """|4 8
         |(2, 3, N) FLLFR
         |(1, 0, S) FFRLF""".stripMargin,
      """|(2, 3, W)
         |(1, 0, S) LOST""".stripMargin
    )
  )

  goldenInOut.map { case (input, expectedOutput) =>
    test("pipeline should produce correct output") {
      val reader = TestReader.stringReader(input.split("\n").toList)

      for {
        ref <- Ref[IO].of("")
        writer = TestWriter.stringWriter(ref)
        _ <- Controller
          .pipeline(reader, writer)
          .compile
          .drain
        output <- ref.get
      } yield {
        assertEquals(output, expectedOutput)
      }
    }
  }
}
