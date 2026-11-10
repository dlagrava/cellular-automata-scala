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
      board(x, y) = v
      v += 1
    }

    new PeriodicBoundaryCondition[Int]().apply(board)

    assertEquals(board(-1, 1), board(sizeX - 1, 1))
    assertEquals(board(sizeX, 1), board(0, 1))
    assertEquals(board(1, -1), board(1, sizeY - 1))
    assertEquals(board(1, sizeY), board(1, 0))
    assertEquals(board(-1, -1), board(sizeX - 1, sizeY - 1))
    assertEquals(board(sizeX, sizeY), board(0, 0))
  }
}
