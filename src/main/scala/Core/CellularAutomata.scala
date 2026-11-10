package Core

import scala.language.postfixOps

class CellularAutomata[T](
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
  private var generation: Int = 0

  def init(): Unit = {
    initializer.initialize(board)
    board.setNeighborhood(neighborhood)
    boundaryCondition.apply(board)
    tmpBoard.copyFromBoard(board)
  }

  def evolve(maxIterations: Int): Unit =
    while (generation < maxIterations) {
      generation += 1
      applyRule()
      board.copyFromBoard(tmpBoard)
      boundaryCondition.apply(board)
      if (generation % 10 == 0) println(s"Generation: $generation, board: ${board.printBoard()}")
    }

  def applyRule(): Unit = {
    for {
      x <- 0 until xSize
      y <- 0 until ySize
    } {
      applyRule(x, y)
    }
  }

  def applyRule(x: Int, y: Int): Unit = {
    val neighbors = neighborhood.getNeighbors(x, y)
    val neigborsValues: List[Node[T]] =
      neighbors.map(n => board.getNode(n._1, n._2))
    val newValue = rule.apply(neigborsValues)
    tmpBoard.setNode(x, y, newValue)
  }
}
