# Cellular Automata in Scala

Generic 2D cellular automata on Scala 3.

## Features

- Pluggable **rules**: Conway, diffusion, parity
- **Neighborhoods**: Moore, von Neumann
- **Boundary conditions**: periodic, fixed-value
- Board initializers (e.g. random)

## Run

```bash
sbt run
```

Default demo: 30×30 Conway board, Moore neighborhood, periodic boundaries, 100 generations.

## Test

```bash
sbt test
```
