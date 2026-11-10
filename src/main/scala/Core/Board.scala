package Core

import scala.reflect.ClassTag
import scala.util.Random

class Board[T: ClassTag](sizeX: Int, sizeY: Int, haloSize: Int, initial: T) {
  private val internalXSize = sizeX + 2 * haloSize
  private val internalYSize = sizeY + 2 * haloSize
  private val nodes: Array[Array[Node[T]]] =
    Array.tabulate(internalXSize, internalYSize)((_, _) => Node[T](initial))

  /** Logical coords: interior is [0, sizeX) x [0, sizeY); halo uses negatives / >= size. */
  private def toInternal(x: Int, y: Int): (Int, Int) =
    (x + haloSize, y + haloSize)

  def getNode(x: Int, y: Int): Node[T] = {
    val (ix, iy) = toInternal(x, y)
    nodes(ix)(iy)
  }

  def setNode(x: Int, y: Int, node: Node[T]): Unit = {
    val (ix, iy) = toInternal(x, y)
    nodes(ix)(iy).setVal(node.value)
  }

  /** Logical coords; used by boundary conditions to fill the halo. */
  private[Core] def setNodes(
      positions: List[(Int, Int)],
      values: List[Node[T]]
  ): Unit = {
    positions.zip(values).foreach { case ((x, y), node) =>
      val (ix, iy) = toInternal(x, y)
      nodes(ix)(iy).setVal(node.value)
    }
  }

  /** Logical coords; used by boundary conditions to read wrap sources. */
  private[Core] def getNodes(nodeList: List[(Int, Int)]): List[Node[T]] = {
    nodeList.map { case (x, y) => getNode(x, y) }
  }

  def copyFromBoard(board: Board[T]): Unit = {
    for {
      x <- 0 until internalXSize
      y <- 0 until internalYSize
    } {
      nodes(x)(y).setVal(board.nodes(x)(y).value)
    }
  }

  def getXSize: Int = sizeX
  def getYSize: Int = sizeY

  /** Interior values only (no halo). Fresh copy for views. */
  def snapshot(): Array[Array[T]] =
    Array.tabulate(sizeX, sizeY)((x, y) => getNode(x, y).value)

  def setNeighborhood(neighborhood: Neighborhood): Unit = {
    for {
      x <- 0 until sizeX
      y <- 0 until sizeY
    } {
      val neighbors = neighborhood.getNeighbors(x, y)
      getNode(x, y).neighbors = neighbors
    }
  }

  def printBoard(): Unit = {
    var str = ""
    for {
      x <- 0 until sizeX
      y <- 0 until sizeY
    } {
      str += getNode(x, y).value.toString + " "
      if y == sizeY - 1
      then str += "\n"
    }
    println(str)
  }
}

trait BoardInitializer[T] {
  def initialize(board: Board[T]): Unit
}

class RandomInitializer(maxValue: Int) extends BoardInitializer[Int] {
  override def initialize(board: Board[Int]): Unit = {
    val rand = new Random
    for {
      x <- 0 until board.getXSize
      y <- 0 until board.getYSize
    } {
      board.setNode(x, y, Node[Int](rand.nextInt(maxValue)))
    }
  }
}

class SquareInitializer(value: Int, size: Int) extends BoardInitializer[Int] {
  override def initialize(board: Board[Int]): Unit = {
    val middleX = board.getXSize / 2
    val middleY = board.getYSize / 2
    val squareIndexes = 
      for {
        x <- List.range(middleX - size / 2, middleX + size / 2)
        y <- List.range(middleY - size / 2, middleY + size / 2)
      } yield (x, y)
    board.setNodes(squareIndexes, squareIndexes.map(x => Node[Int](value)))
  }
}
