package Core

class ConwayRule extends Rule[Int]("Conway") {
  override def apply(neighbors: List[Node[Int]]): Int = {
    val currentCell = neighbors.head
    val sumNeighbors = neighbors.tail.map(_.value).sum
    (currentCell.value, sumNeighbors) match {
      case (1, x) if x < 2 || x > 3 => 0
      case (0, 3)                   => 1
      case _                        => currentCell.value
    }
  }
}

class ParityRule extends Rule[Int]("Parity") {
  override def apply(neighbors: List[Node[Int]]): Int = {
    val sumNeighbors = neighbors.tail.map(_.value).sum
    if (sumNeighbors % 2 == 0) 0 else 1
  }
}
