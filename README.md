# Cellular Automata in Scala

Generic 2D cellular automata on Scala 3, with a ScalaFX visualizer.

## Features

- Pluggable **rules**: Conway, diffusion, parity
- **Neighborhoods**: Moore, von Neumann
- **Boundary conditions**: periodic, fixed-value
- Board initializers (e.g. random, square)
- Live **ScalaFX** view (`AutomataView` + color maps)

## Run

```bash
sbt run
```

Opens a 256×256 window (3px cells). Default demo: parity rule, Moore neighborhood, periodic boundaries, centered square seed.

| Key | Action |
|-----|--------|
| Space | Pause / resume |
| R | Re-init board |

## Test

```bash
sbt test
```
