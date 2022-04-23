package dev.mjftw.model

case class CommandSet(roverStart: Rover, commands: List[Command])
case class CommandContext(grid: Grid, commandSet: CommandSet)

trait Command

object Command {
  case object MoveForward extends Command
  case object TurnLeft extends Command
  case object TurnRight extends Command
}
