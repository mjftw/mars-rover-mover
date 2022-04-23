# Mars Rover

Move a set of Mars rovers around according to a set of instructions provided.

## Problem statement

The problem - Mars Rover
Write a program that takes in commands and moves one or more robots around
Mars.

* The world should be modelled as a grid with size m x n
* Your program should read the input, update the robots, and print out the final states
of the robots
* Each robot has a position (x, y), and an orientation (N, E, S, W)
* Each robot can move forward one space (F), rotate left by 90 degrees (L), or rotate
right by 90 degrees (R)
* If a robot moves off the grid, it is marked as ‘lost’ and its last valid grid position and
orientation is recorded
* Going from `x -> x + 1` is in the easterly direction, and `y -> y + 1` is in the northerly
direction. i.e. (0, 0) represents the south-west corner of the grid

The input takes the form:

```
4 8
(2, 3, E) LFRFF
(0, 2, N) FFLFRFF
```

The first line of the input `'4 8'` specifies the size of the grid. The subsequent lines each
represent the initial state and commands for a single robot. `(0, 2, N)` specifies the initial state
of the form `(x, y, orientation)`. `FFLFRFF` represents the sequence of movement commands
for the robot.

The output should take the form:

```
(4, 4, E)
(0, 4, W) LOST
```

Each line represents the final position and orientation of the robots of the form `(x, y,
orientation)` and optionally whether the robot was lost.

Another example for the input:

```
4 8
(2, 3, N) FLLFR
(1, 0, S) FFRLF
```

The output would be:

```
(2, 3, W)
(1, 0, S) LOST
```

## Implementation notes

**This solution is massively over engineered.**

Thought it would be fun to make it so that a continuous stream of input could be processed, and
to add abstractions to easily change where that data is read from and results written to.

It could also do with more unit tests, but the top level black box test is enough to prove it works.

## Run application

```shell
sbt 'run input_file output_file'
```

```shell
$ sbt 'run commands.txt output.txt'
[info] welcome to sbt 1.5.8 (Ubuntu Java 11.0.14.1)
...
[info] running dev.mjftw.Main commands.txt output.txt

Starting data processing
Finished processing data

```

## Run tests

```shell
sbt test
```

## Generating data

A Python CLI tool is provided to generate instruction input for the mars rover program.

```shell
$ python3 gen_instructions.py --help
usage: gen_instructions.py [-h] rovers grid_size_x grid_size_y max_rover_moves

A lightweight CLI application for generating random rover instructions

positional arguments:
  rovers           Number of rovers to generate commands for
  grid_size_x      X size of the grid
  grid_size_y      Y size of the grid
  max_rover_moves  Maximum number of moves to give a rover

optional arguments:
  -h, --help       show this help message and exit
```

Example usage:

```shell
$ python3 gen_instructions.py 3 10 15 8
1 5
(0, 15, W) RFFFL
(2, 6, E) RL
(3, 13, E) FFRFFRRR
```

Piping to a file to be read by the mars rover program:

```shell
$ python3 gen_instructions.py 3 10 15 8 > commands.txt
```

