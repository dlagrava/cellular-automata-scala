package Core

import munit.FunSuite

class PeriodicBoundaryConditionTest extends FunSuite {

  test("halo edges and corners mirror opposite interior cells") {
    val sizeX = 10
    val sizeY = 10
    val board = Board[Int](sizeX, sizeY, haloSize = 1, initial = 0)
    var v = 1
    for {
      x <- 0 until sizeX
      y <- 0 until sizeY
    } {
      board.setNode(x, y, Node(v))
      v += 1
    }

    new PeriodicBoundaryCondition[Int]().apply(board)

    assertEquals(board.getNode(-1, 1).value, board.getNode(sizeX - 1, 1).value)
    assertEquals(board.getNode(sizeX, 1).value, board.getNode(0, 1).value)
    assertEquals(board.getNode(1, -1).value, board.getNode(1, sizeY - 1).value)
    assertEquals(board.getNode(1, sizeY).value, board.getNode(1, 0).value)
    assertEquals(
      board.getNode(-1, -1).value,
      board.getNode(sizeX - 1, sizeY - 1).value
    )
    assertEquals(board.getNode(sizeX, sizeY).value, board.getNode(0, 0).value)
  }
}
