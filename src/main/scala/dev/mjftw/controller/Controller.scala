package dev.mjftw.controller

import dev.mjftw.model.{
  Rover,
  Command,
  Direction,
  Grid,
  Status,
  CommandContext,
  CommandSet
}
import fs2.Pipe
import dev.mjftw.io.Reader
import dev.mjftw.io.Writer

object Controller {

  /** The main data pipeline, responsible for reading the commands, executing
    * them, and writing the result.
    *
    * It is designed such that it knows nothing about where the data is being
    * read from or written too. It could just as easily be pulling data from a
    * websocket or a file, and writing it to the console or a file, it wouldn't
    * matter.
    *
    * It is designed as a streaming pipeline which means that it could handle an
    * infinite stream of data without issue. E.g. Reading and writing to a
    * websocket.
    */
  def pipeline[F[_]](
      reader: Reader[F, CommandContext],
      writer: Writer[F, Rover]
  ) =
    reader.read
      .through(executeCommands)
      .through(writer.write)

  /** For each rover position and set of commands (CommandContext), execute all
    * the commands to get the final rover position
    */
  def executeCommands[F[_]]: Pipe[F, CommandContext, Rover] =
    inStream =>
      inStream
        .map { case CommandContext(grid, CommandSet(roverStart, commands)) =>
          // Starting with roverStart, repeatedly move the rover be executing each command
          // to get the final rover position
          commands.foldLeft(roverStart)((rover, command) =>
            moveRover(rover, command, grid)
          )
        }

  /** Execute a single command to move the rover */
  def moveRover(rover: Rover, command: Command, grid: Grid): Rover = {
    def move(): Rover = {
      val movedRover = command match {
        case Command.MoveForward =>
          rover.facing match {
            case Direction.North => rover.copy(location = rover.location.north)
            case Direction.East  => rover.copy(location = rover.location.east)
            case Direction.South => rover.copy(location = rover.location.south)
            case Direction.West  => rover.copy(location = rover.location.west)
          }
        case Command.TurnLeft  => rover.copy(facing = rover.facing.left)
        case Command.TurnRight => rover.copy(facing = rover.facing.right)
      }

      // If the rover has moved outside the grid, ignore the move and mark it as lost
      if (grid.contains(movedRover.location))
        movedRover
      else
        rover.copy(status = Status.Lost)
    }

    // Move the rover only if it is not lost
    rover.status match {
      case Status.Ok   => move()
      case Status.Lost => rover
    }
  }
}
