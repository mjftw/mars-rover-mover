package dev.mjftw.model

// Represents a heading and what heading you'd have if turning to the left or right
trait Direction {
  def left: Direction
  def right: Direction
}

object Direction {
  case object North extends Direction {
    def left = West
    def right = East
  }
  case object East extends Direction {
    def left = North
    def right = South
  }
  case object South extends Direction {
    def left = East
    def right = West
  }
  case object West extends Direction {
    def left = South
    def right = North
  }
}

case class Location(x: Int, y: Int) {
  def north = Location(x, y + 1)
  def east = Location(x + 1, y)
  def south = Location(x, y - 1)
  def west = Location(x - 1, y)
}

case class Grid(xMax: Int, yMax: Int) {

  /** Return true if the location falls withing the grid */
  def contains(location: Location): Boolean =
    location match {
      case Location(x, y) => (0 <= x && x <= xMax) && (0 <= y && y <= yMax)
    }

}
