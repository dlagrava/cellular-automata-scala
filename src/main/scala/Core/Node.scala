package Core

class Node[T](var value: T) {
  var neighbors: List[(Int, Int)] = List()
  def getVal: T = value
  def setVal(v: T): Unit =
    value = v

  def setNeighbors(n: List[(Int, Int)]): Unit =
    neighbors = n

  def copy(otherNode: Node[T]): Node[T] =
    val node = Node[T](otherNode.value)
    node.neighbors = otherNode.neighbors
    node
}
