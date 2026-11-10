import Core._

object Main {

  @main
  def run(): Unit = {
    val boundaryCondition = new PeriodicBoundaryCondition[Int]()
    val neighborhood = new MooreNeighborhood()
    val rule = new ConwayRule()
    val initializer = new RandomInitializer(2)
    val cellularAutomata = new CellularAutomata(
      30,
      30,
      neighborhood,
      rule,
      boundaryCondition,
      initializer,
      initial = 0
    )
    cellularAutomata.init()
    cellularAutomata.evolve(100)
  }

}
