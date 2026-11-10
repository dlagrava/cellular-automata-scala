package Core

/** Neighborhood is a trait that defines the neighborhood of a cell. The cell is
  * always the first element of the list.
  *
  * @param name
  *   the name of the neighborhood
  */
trait Neighborhood {
  val name: String
  val halo: Int
  def getNeighbors(x: Int, y: Int): List[(Int, Int)]
}

class MooreNeighborhood extends Neighborhood {
  override val name: String = "Moore"
  override val halo: Int = 1
  override def getNeighbors(x: Int, y: Int): List[(Int, Int)] =
    List(
      (x, y),
      (x - 1, y - 1),
      (x - 1, y),
      (x - 1, y + 1),
      (x, y - 1),
      (x, y + 1),
      (x + 1, y - 1),
      (x + 1, y),
      (x + 1, y + 1)
    )
}

class VonNeumannNeighborhood extends Neighborhood {
  override val name: String = "VonNeumann"
  override val halo: Int = 1
  override def getNeighbors(x: Int, y: Int): List[(Int, Int)] =
    List(
      (x, y),
      (x - 1, y),
      (x + 1, y),
      (x, y - 1),
      (x, y + 1)
    )
}
