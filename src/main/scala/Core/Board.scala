package Core

import scala.reflect.ClassTag
import scala.util.Random

class Board[T: ClassTag](sizeX: Int, sizeY: Int, haloSize: Int, initial: T) {
  private val internalXSize = sizeX + 2 * haloSize
  private val internalYSize = sizeY + 2 * haloSize
  private val nodes: Array[Array[Node[T]]] =
    Array.tabulate(internalXSize, internalYSize)((_, _) => Node[T](initial))

  private def toInternal(x: Int, y: Int): (Int, Int) =
    (x + haloSize, y + haloSize)

  private[Core] def node(x: Int, y: Int): Node[T] = {
    val (ix, iy) = toInternal(x, y)
    nodes(ix)(iy)
  }

  def apply(x: Int, y: Int): T = node(x, y).value

  def update(x: Int, y: Int, value: T): Unit =
    node(x, y).value = value

  /** Logical coords; used by boundary conditions to fill the halo. */
  private[Core] def setValues(
      positions: List[(Int, Int)],
      values: List[T]
  ): Unit =
    positions.zip(values).foreach { case ((x, y), v) =>
      update(x, y, v)
    }

  /** Logical coords; used by boundary conditions to read wrap sources. */
  private[Core] def getValues(positions: List[(Int, Int)]): List[T] =
    positions.map { case (x, y) => apply(x, y) }

  def copyFromBoard(board: Board[T]): Unit = {
    for {
      x <- 0 until internalXSize
      y <- 0 until internalYSize
    } {
      nodes(x)(y).value = board.nodes(x)(y).value
    }
  }

  def width: Int = sizeX
  def height: Int = sizeY

  /** Interior values only (no halo). Fresh copy for views. */
  def snapshot(): Array[Array[T]] =
    Array.tabulate(sizeX, sizeY)((x, y) => apply(x, y))

  def setNeighborhood(neighborhood: Neighborhood): Unit = {
    for {
      x <- 0 until sizeX
      y <- 0 until sizeY
    } {
      node(x, y).neighbors = neighborhood.getNeighbors(x, y)
    }
  }
}

trait BoardInitializer[T] {
  def initialize(board: Board[T]): Unit
}

class RandomInitializer(maxValue: Int) extends BoardInitializer[Int] {
  override def initialize(board: Board[Int]): Unit = {
    val rand = new Random
    for {
      x <- 0 until board.width
      y <- 0 until board.height
    } {
      board(x, y) = rand.nextInt(maxValue)
    }
  }
}

class SquareInitializer(value: Int, size: Int) extends BoardInitializer[Int] {
  override def initialize(board: Board[Int]): Unit = {
    val middleX = board.width / 2
    val middleY = board.height / 2
    val squareIndexes =
      for {
        x <- List.range(middleX - size / 2, middleX + size / 2)
        y <- List.range(middleY - size / 2, middleY + size / 2)
      } yield (x, y)
    board.setValues(squareIndexes, squareIndexes.map(_ => value))
  }
}
