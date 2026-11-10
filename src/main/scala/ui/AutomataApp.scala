package ui

import Core.*
import scalafx.Includes.*
import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.StackPane
import view.BinaryColorMap

object AutomataApp extends JFXApp3:

  private val BoardSize = 256
  private val CellPx = 3
  private val FrameNs = 80_000_000L // ~12.5 Hz

  override def start(): Unit =
    val ca = new CellularAutomata[Int](
      BoardSize,
      BoardSize,
      new MooreNeighborhood(),
      new ParityRule(),
      new PeriodicBoundaryCondition[Int](),
      new SquareInitializer(1, (0.1 * BoardSize).toInt),
      initial = 0
    )
    ca.init()

    val colorMap = new BinaryColorMap
    val view = new ScalaFxView[Int](colorMap, BoardSize, BoardSize, CellPx)

    var paused = false
    var lastTickNs = 0L

    val timer = AnimationTimer { now =>
      if !paused && now - lastTickNs >= FrameNs then
        lastTickNs = now
        ca.step()
        view.render(ca.generation, ca.snapshot())
        stage.title = s"${ca.ruleName}: gen ${ca.generation}${if paused then " (paused)" else ""}"
    }

    stage = new JFXApp3.PrimaryStage:
      title = s"${ca.ruleName}: gen 0"
      scene = new Scene(BoardSize * CellPx, BoardSize * CellPx):
        root = new StackPane:
          children = Seq(view.imageView)
        onKeyPressed = (e: KeyEvent) =>
          e.code match
            case KeyCode.Space =>
              paused = !paused
              stage.title =
                s"${ca.ruleName}: gen ${ca.generation}${if paused then " (paused)" else ""}"
            case KeyCode.R =>
              ca.init()
              view.render(ca.generation, ca.snapshot())
              stage.title =
                s"${ca.ruleName}: gen ${ca.generation}${if paused then " (paused)" else ""}"
            case _ => ()

    view.render(ca.generation, ca.snapshot())
    timer.start()
