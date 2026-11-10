package view

trait AutomataView[T]:
  def render(generation: Int, grid: Array[Array[T]]): Unit
  def close(): Unit = ()
