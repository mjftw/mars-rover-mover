package dev.mjftw.codec

import dev.mjftw.model.{
  Command,
  CommandSet,
  Rover,
  Direction,
  Location,
  Status,
  Grid
}
import dev.mjftw.model.Direction._
import cats.implicits._

case class DecodeFailure(text: String) extends Throwable

/** Decode a string into a given object type */
trait Decoder[O] {
  def decode(text: String): Either[DecodeFailure, O]
}

object Decoder {

  /** Find the required decoder in the implicit context */
  def apply[I](implicit decoder: Decoder[I]): Decoder[I] = decoder

  /** Add extension method to strings to allow `myString.decode[Type]` */
  implicit class StringOps(text: String) {
    def decode[O: Decoder]: Either[DecodeFailure, O] = Decoder[O].decode(text)
  }

  implicit def commandSetDecoder: Decoder[CommandSet] = {
    val Pattern = "\\(([0-9]+), ([0-9]+), ([NESW])\\) ([LFR]+)".r

    new Decoder[CommandSet] {
      override def decode(text: String): Either[DecodeFailure, CommandSet] =
        text match {
          case Pattern(xStr, yStr, headingStr, commandsStr) =>
            val heading: Direction = headingStr match {
              case "N" => Direction.North
              case "E" => Direction.East
              case "S" => Direction.South
              case "W" => Direction.West
            }

            val commands: List[Command] = commandsStr.map {
              case 'L' => Command.TurnLeft
              case 'R' => Command.TurnRight
              case 'F' => Command.MoveForward
            }.toList

            // Catch errors here as it would be possible to get numbers greater
            // than the max representable by an Int, at which point an exception
            // would be thrown. Catch this and turn it into a DecodeFailure.
            Either.catchOnly[NumberFormatException](
              Location(xStr.toInt, yStr.toInt)
            ) match {
              case Right(location) =>
                CommandSet(
                  Rover(location, heading, Status.Ok),
                  commands
                ).asRight
              case Left(error) => DecodeFailure(error.getMessage()).asLeft
            }

          // If the text did not match the regex pattern then it's a decode failure
          case _ => Left(DecodeFailure(text))
        }
    }
  }

  implicit def gridDecoder: Decoder[Grid] = {
    val Pattern = "([0-9]+) ([0-9]+)".r

    new Decoder[Grid] {
      override def decode(text: String): Either[DecodeFailure, Grid] =
        text match {
          case Pattern(xStr, yStr) =>
            Either
              .catchOnly[NumberFormatException](
                Grid(xStr.toInt, yStr.toInt)
              )
              .leftMap(error => DecodeFailure(error.getMessage()))

          case _ => Left(DecodeFailure(text))
        }
    }
  }
}
