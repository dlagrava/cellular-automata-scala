package Core

trait Rule[T] {
  def apply(neighbors: List[Node[T]]): Node[T]

}

class ConwayRule extends Rule[Int] {
  override def apply(neighbors: List[Node[Int]]): Node[Int] = {
    val currentCell = neighbors.head
    val sumNeighbors = neighbors.tail.map(_.getVal).sum
    (currentCell.getVal, sumNeighbors) match {
      case (1, x) if x < 2 || x > 3 => Node[Int](0)
      case (0, 3)                   => Node[Int](1)
      case _                        => currentCell
    }
  }
}

class DiffusionRule extends Rule[Double] {
  override def apply(neighbors: List[Node[Double]]): Node[Double] = {
    val currentCell = neighbors.head
    val sumNeighbors = neighbors.tail.map(_.getVal).sum
    Node[Double](sumNeighbors / 4.0)
  }
}

class ParityRule extends Rule[Int] {
  override def apply(neighbors: List[Node[Int]]): Node[Int] = {
    val currentCell = neighbors.head
    val sumNeighbors = neighbors.tail.map(_.getVal).sum
    if (sumNeighbors % 2 == 0) Node[Int](0) else Node[Int](1)
  }
}