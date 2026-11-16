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
import scalafx.scene.image.ImageView
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.{BorderPane, HBox, StackPane}
import view.{BinaryColorMap, JetColorMap}

object AutomataApp extends JFXApp3:

  private val DefaultSize = 256
  private val MinSize = 32
  private val MaxSize = 512
  private val SizeStep = 16
  private val CellPx = 3
  private val FrameNs = 80_000_000L // ~12.5 Hz
  private val ControlBarHeight = 40

  private trait LiveSim:
    def generation: Int
    def ruleName: String
    def step(): Unit
    def init(): Unit
    def render(): Unit
    def imageView: ImageView

  private def intSim(rule: Rule[Int], size: Int): LiveSim =
    val ca = new CellularAutomata[Int](
      size,
      size,
      new MooreNeighborhood(),
      rule,
      new PeriodicBoundaryCondition[Int](),
      new SquareInitializer(1, (0.1 * size).toInt),
      initial = 0
    )
    ca.init()
    val view = new ScalaFxView[Int](new BinaryColorMap, size, size, CellPx)
    new LiveSim:
      def generation = ca.generation
      def ruleName = ca.ruleName
      def step() = ca.step()
      def init() = ca.init()
      def render() = view.render(ca.generation, ca.snapshot())
      def imageView = view.imageView

  private def diffusionSim(size: Int): LiveSim =
    val ca = new CellularAutomata[Double](
      size,
      size,
      new VonNeumannNeighborhood(),
      new DiffusionRule(),
      new PeriodicBoundaryCondition[Double](),
      new SquareInitializer(1.0, (0.1 * size).toInt),
      initial = 0.0
    )
    ca.init()
    val view =
      new ScalaFxView[Double](new JetColorMap(0.0, 1.0), size, size, CellPx)
    new LiveSim:
      def generation = ca.generation
      def ruleName = ca.ruleName
      def step() = ca.step()
      def init() = ca.init()
      def render() = view.render(ca.generation, ca.snapshot())
      def imageView = view.imageView

  private def buildSim(ruleName: String, size: Int): LiveSim =
    ruleName match
      case "Conway"    => intSim(new ConwayRule(), size)
      case "Diffusion" => diffusionSim(size)
      case _           => intSim(new ParityRule(), size)

  override def start(): Unit =
    var currentRule = "Parity"
    var currentSize = DefaultSize
    var sim = buildSim(currentRule, currentSize)
    var paused = true
    var lastTickNs = 0L

    val canvasPane = new StackPane:
      children = Seq(sim.imageView)

    def updateTitle(): Unit =
      stage.title = s"${sim.ruleName}: gen ${sim.generation}${
          if paused then " (paused)" else ""
        }"

    def applyConfig(ruleName: String, size: Int): Unit =
      currentRule = ruleName
      currentSize = size
      sim = buildSim(ruleName, size)
      canvasPane.children = Seq(sim.imageView)
      stage.scene.value.getWindow.sizeToScene()
      sim.render()
      updateTitle()

    val ruleCombo =
      new ComboBox[String](ObservableBuffer("Parity", "Conway", "Diffusion")):
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
        sim.step()
        sim.render()
        updateTitle()
    }

    stage = new JFXApp3.PrimaryStage:
      title = s"${sim.ruleName}: gen 0 (paused)"
      scene = new Scene(DefaultSize * CellPx, DefaultSize * CellPx + ControlBarHeight):
        root = rootPane
        onKeyPressed = (e: KeyEvent) =>
          e.code match
            case KeyCode.Space =>
              paused = !paused
              updateTitle()
            case KeyCode.R =>
              sim.init()
              sim.render()
              updateTitle()
            case _ => ()

    sim.render()
    timer.start()
