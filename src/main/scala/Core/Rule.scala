package Core

trait Rule[T](val name: String) {
  def apply(neighbors: List[Node[T]]): T
}
