package Core

trait BoundaryCondition[T] {
  val length: Int
  def apply(board: Board[T]): Unit

  def getRowIndexes(row: Int, rowLength: Int): List[(Int, Int)] = {
    for {
      j <- List.range(0, rowLength)
    } yield (row, j)
  }

  def getColumnIndexes(column: Int, columnLength: Int): List[(Int, Int)] = {
    for {
      i <- List.range(0, columnLength)
    } yield (i, column)
  }
}

class PeriodicBoundaryCondition[T] extends BoundaryCondition[T] {
  override val length: Int = 1
  override def apply(board: Board[T]): Unit = {
    val halo = length
    val sizeX = board.width
    val sizeY = board.height

    def inHalo(x: Int, y: Int): Boolean =
      x < 0 || x >= sizeX || y < 0 || y >= sizeY

    /** Map a logical halo index back into the interior via wrap-around. */
    def wrap(index: Int, size: Int): Int =
      Math.floorMod(index, size)

    val haloCells = for {
      x <- List.range(-halo, sizeX + halo)
      y <- List.range(-halo, sizeY + halo)
      if inHalo(x, y)
    } yield (x, y)

    val sources = haloCells.map { case (x, y) =>
      (wrap(x, sizeX), wrap(y, sizeY))
    }
    board.setValues(haloCells, board.getValues(sources))
  }
}

class FixedValueBoundaryCondition[T] extends BoundaryCondition[T] {
  override val length: Int = 1
  override def apply(board: Board[T]): Unit = {}
}
