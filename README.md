# Mars Rover

Move a set of Mars rovers around according to a set of instructions provided.

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

