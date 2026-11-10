package Core

import scala.reflect.ClassTag

class CellularAutomata[T: ClassTag](
    xSize: Int,
    ySize: Int,
    neighborhood: Neighborhood,
    rule: Rule[T],
    boundaryCondition: BoundaryCondition[T],
    initializer: BoardInitializer[T],
    initial: T
) {
  private val board: Board[T] = Board[T](xSize, ySize, neighborhood.halo, initial)
  private val tmpBoard: Board[T] = Board[T](xSize, ySize, neighborhood.halo, initial)
  private var _generation: Int = 0

  def generation: Int = _generation
  def width: Int = xSize
  def height: Int = ySize

  def init(): Unit = {
    initializer.initialize(board)
    board.setNeighborhood(neighborhood)
    boundaryCondition.apply(board)
    tmpBoard.copyFromBoard(board)
  }

  def step(): Unit = {
    _generation += 1
    applyRule()
    board.copyFromBoard(tmpBoard)
    boundaryCondition.apply(board)
  }

  def evolve(maxIterations: Int): Unit =
    while (_generation < maxIterations) {
      step()
    }

  def snapshot(): Array[Array[T]] = board.snapshot()

  def applyRule(): Unit = {
    for {
      x <- 0 until xSize
      y <- 0 until ySize
    } {
      applyRule(x, y)
    }
  }

  def ruleName: String = rule.name

  def applyRule(x: Int, y: Int): Unit = {
    val cell = board.node(x, y)
    val neighborNodes = cell.neighbors.map { case (nx, ny) => board.node(nx, ny) }
    tmpBoard.node(x, y).value = rule.apply(neighborNodes)
  }
}
