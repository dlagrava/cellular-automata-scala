package Core

import munit.FunSuite

class ConwayRuleTest extends FunSuite {

  test(
    "ConwayRule.apply should return 1 when cell is alive and has 2 alive neighbors"
  ) {
    val rule = new ConwayRule()
    val state = List(Node[Int](1), Node[Int](0), Node[Int](1), Node[Int](1))
    val result = rule.apply(state)
    assertEquals(result.value, 1)
  }

  test(
    "ConwayRule.apply should return 1 when cell is alive and has 3 alive neighbors"
  ) {
    val rule = new ConwayRule()
    val state =
      List(Node[Int](1), Node[Int](0), Node[Int](1), Node[Int](1), Node[Int](1))
    val result = rule.apply(state)
    assertEquals(result.value, 1)
  }

  test(
    "ConwayRule.apply should return 0 when cell is alive and has less than 2 alive neighbors"
  ) {
    val rule = new ConwayRule()
    val state = List(Node[Int](1), Node[Int](0), Node[Int](0), Node[Int](1))
    val result = rule.apply(state)
    assertEquals(result.value, 0)
  }

  test(
    "ConwayRule.apply should return 0 when cell is alive and has more than 3 alive neighbors"
  ) {
    val rule = new ConwayRule()
    val state = List(
      Node[Int](1),
      Node[Int](0),
      Node[Int](1),
      Node[Int](1),
      Node[Int](1),
      Node[Int](1)
    )
    val result = rule.apply(state)
    assertEquals(result.value, 0)
  }

  test(
    "ConwayRule.apply should return 1 when cell is dead and has exactly 3 alive neighbors"
  ) {
    val rule = new ConwayRule()
    val state =
      List(Node[Int](0), Node[Int](0), Node[Int](1), Node[Int](1), Node[Int](1))
    val result = rule.apply(state)
    assertEquals(result.value, 1)
  }

  test(
    "ConwayRule.apply should return 0 when cell is dead and has less than 3 alive neighbors"
  ) {
    val rule = new ConwayRule()
    val state = List(
      Node[Int](0),
      Node[Int](0),
      Node[Int](0),
      Node[Int](0),
      Node[Int](1),
      Node[Int](1)
    )
    val result = rule.apply(state)
    assertEquals(result.value, 0)
  }

  test(
    "ConwayRule.apply should return 0 when cell is dead and has more than 3 alive neighbors"
  ) {
    val rule = new ConwayRule()
    val state = List(
      Node[Int](0),
      Node[Int](0),
      Node[Int](1),
      Node[Int](1),
      Node[Int](1),
      Node[Int](1)
    )
    val result = rule.apply(state)
    assertEquals(result.value, 0)
  }

}
