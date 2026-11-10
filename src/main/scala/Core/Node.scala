package Core

class Node[T](var value: T):
  var neighbors: List[(Int, Int)] = Nil
