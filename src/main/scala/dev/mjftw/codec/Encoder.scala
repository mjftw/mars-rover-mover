package dev.mjftw.codec

import dev.mjftw.model.{CommandSet, Rover, Direction, Location, Status, Grid}
import dev.mjftw.model.Direction._

/** Encode a string from a given object type */
trait Encoder[I] {
  def encode(in: I): String
}

object Encoder {

  /** Find the required encoder in the implicit context */
  def apply[I](implicit encoder: Encoder[I]): Encoder[I] = encoder

  /** Allow calling `.encode` on anything with an encoder instance */
  implicit class EncoderOps[I: Encoder](i: I) {
    def encode: String = Encoder[I].encode(i)
  }

  implicit def roverEncoder = new Encoder[Rover] {
    override def encode(rover: Rover): String =
      rover match {
        case Rover(location, facing, status) =>
          val statusString = status match {
            case Status.Ok   => ""
            case Status.Lost => " LOST"
          }

          val facingString = facing match {
            case North => "N"
            case East  => "E"
            case South => "S"
            case West  => "W"
          }
          s"(${location.x}, ${location.y}, ${facingString})$statusString"
      }
  }
}
