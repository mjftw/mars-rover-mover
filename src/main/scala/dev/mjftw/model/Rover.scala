package dev.mjftw.model

trait Status

object Status {
  case object Ok extends Status
  case object Lost extends Status
}

case class Rover(location: Location, facing: Direction, status: Status)
