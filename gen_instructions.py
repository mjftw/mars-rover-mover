import random
from itertools import repeat, islice
from typing import Tuple
import argparse

def main():
  # A little lightweight CLI arg parser
  parser = argparse.ArgumentParser(description='A lightweight CLI application for generating random rover instructions')
  parser.add_argument("rovers", help="Number of rovers to generate commands for", type=int)
  parser.add_argument("grid_size_x", help="X size of the grid", type=int)
  parser.add_argument("grid_size_y", help="Y size of the grid", type=int)
  parser.add_argument("max_rover_moves", help="Maximum number of moves to give a rover", type=int)
  args = parser.parse_args()

  print(draw_instructions(args.grid_size_x, args.grid_size_y, args.rovers, args.max_rover_moves))

"""Draw random rover instructions"""
def draw_instructions(grid_size_x: int, grid_size_y: int, num_rovers: int, max_rover_moves: int) -> str:
  grid_size = draw_xy(grid_size_x, grid_size_y)

  instructions = [f'{grid_size[0]} {grid_size[1]}']

  for _ in range(num_rovers):
    num_moves = random.randint(1, max_rover_moves)
    rover_commands = draw_command(grid_size_x, grid_size_y, num_moves)
    instructions.append(rover_commands)

  return '\n'.join(instructions)


"""Draw a random rover command string"""
def draw_command(x_max: int, y_max: int, max_moves: int) -> str:
  start_at = draw_xy(x_max, y_max)
  heading = draw_heading()
  moves = draw_moves(max_moves)

  return f'({start_at[0]}, {start_at[1]}, {heading}) {moves}'

"""Draw a random heading. E.g. N"""
def draw_heading() -> str:
  return random.sample("NESW", 1)[0]

"""Draw random coordinates. E.g. (2, 11)"""
def draw_xy(x_max: int, y_max: int) -> Tuple[int, int]:
  return (random.randint(0, x_max), random.randint(0, y_max))

"""Draw a random moves command string of length @n. E.g. FFLRFLLR"""
def draw_moves(n: int) -> str:
  return ''.join(islice(map(lambda flr: random.sample(flr, 1)[0], repeat("FLR")), n))

if __name__ == "__main__":
  main()