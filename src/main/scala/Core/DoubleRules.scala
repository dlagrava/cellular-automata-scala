package Core

class DiffusionRule extends Rule[Double]("Diffusion") {
  override def apply(neighbors: List[Node[Double]]): Double = {
    val sumNeighbors = neighbors.map(_.value).sum
    sumNeighbors / 5.0
  }
}
