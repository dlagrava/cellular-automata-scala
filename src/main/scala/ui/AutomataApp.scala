package ui

import Core.*
import scalafx.Includes.*
import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp3
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, ComboBox, Label, Spinner}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.{BorderPane, HBox, StackPane}
import view.BinaryColorMap

object AutomataApp extends JFXApp3:

  private val DefaultSize = 256
  private val MinSize = 32
  private val MaxSize = 512
  private val SizeStep = 16
  private val CellPx = 3
  private val FrameNs = 80_000_000L // ~12.5 Hz
  private val ControlBarHeight = 40

  private def ruleFor(name: String): Rule[Int] =
    name match
      case "Conway" => new ConwayRule()
      case _        => new ParityRule()

  private def buildCa(ruleName: String, size: Int): CellularAutomata[Int] =
    val ca = new CellularAutomata[Int](
      size,
      size,
      new MooreNeighborhood(),
      ruleFor(ruleName),
      new PeriodicBoundaryCondition[Int](),
      new SquareInitializer(1, (0.1 * size).toInt),
      initial = 0
    )
    ca.init()
    ca

  override def start(): Unit =
    var currentRule = "Parity"
    var currentSize = DefaultSize
    var ca = buildCa(currentRule, currentSize)
    var view = new ScalaFxView[Int](new BinaryColorMap, currentSize, currentSize, CellPx)
    var paused = true
    var lastTickNs = 0L

    val canvasPane = new StackPane:
      children = Seq(view.imageView)

    def updateTitle(): Unit =
      stage.title = s"${ca.ruleName}: gen ${ca.generation}${
          if paused then " (paused)" else ""
        }"

    def applyConfig(ruleName: String, size: Int): Unit =
      currentRule = ruleName
      currentSize = size
      ca = buildCa(ruleName, size)
      view = new ScalaFxView[Int](new BinaryColorMap, size, size, CellPx)
      canvasPane.children = Seq(view.imageView)
      stage.scene.value.getWindow.sizeToScene()
      view.render(ca.generation, ca.snapshot())
      updateTitle()

    val ruleCombo = new ComboBox[String](ObservableBuffer("Parity", "Conway")):
      value = "Parity"
      prefWidth = 120

    val sizeSpinner = new Spinner[Int](MinSize, MaxSize, DefaultSize, SizeStep):
      editable = true
      prefWidth = 100

    val startBtn = new Button("Start")
    val pauseBtn = new Button("Pause")

    startBtn.onAction = (_: ActionEvent) =>
      val selectedRule = Option(ruleCombo.value.value).getOrElse(currentRule)
      val selectedSize = sizeSpinner.value.value
      if selectedRule != currentRule || selectedSize != currentSize then
        applyConfig(selectedRule, selectedSize)
      paused = false
      updateTitle()

    pauseBtn.onAction = (_: ActionEvent) =>
      paused = true
      updateTitle()

    val controls = new HBox(8):
      padding = Insets(8)
      children = Seq(
        new Label("Rule:"),
        ruleCombo,
        new Label("Size:"),
        sizeSpinner,
        startBtn,
        pauseBtn
      )

    val rootPane = new BorderPane:
      top = controls
      center = canvasPane

    val timer = AnimationTimer { now =>
      if !paused && now - lastTickNs >= FrameNs then
        lastTickNs = now
        ca.step()
        view.render(ca.generation, ca.snapshot())
        updateTitle()
    }

    stage = new JFXApp3.PrimaryStage:
      title = s"${ca.ruleName}: gen 0 (paused)"
      scene = new Scene(DefaultSize * CellPx, DefaultSize * CellPx + ControlBarHeight):
        root = rootPane
        onKeyPressed = (e: KeyEvent) =>
          e.code match
            case KeyCode.Space =>
              paused = !paused
              updateTitle()
            case KeyCode.R =>
              ca.init()
              view.render(ca.generation, ca.snapshot())
              updateTitle()
            case _ => ()

    view.render(ca.generation, ca.snapshot())
    timer.start()
