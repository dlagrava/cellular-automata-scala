package view

trait CellColorMap[T]:
  /** ARGB int, shared by FX PixelWriter and future BufferedImage. */
  def toArgb(value: T): Int

class BinaryColorMap extends CellColorMap[Int]:
  def toArgb(value: Int): Int =
    if value != 0 then 0xff111111 else 0xffeeeeee

class JetColorMap(min: Double, max: Double) extends CellColorMap[Double]:
  def toArgb(value: Double): Int =
    val normalized = (value - min) / (max - min)
    val length = max - min
    val intervals = List(1.0 / 8.0, 5.0 / 16.0, 11.0 / 16.0, 14.0 / 16.0)
    val normalizedValue = (value - min) / length

    var r, g, b = 0
    normalizedValue match {
      case x if x < intervals(0) =>
        b = (255 * (normalizedValue + intervals(0)) / intervals(0) / 2).toInt
      case x if x < intervals(1) =>
        b = 255
        g = ((normalizedValue - intervals(0)) * 255.0 /
          (intervals(1) - intervals(0))).toInt
      case x if x < intervals(2) =>
        b = ((-normalizedValue + intervals(2)) * 255.0 /
          (intervals(2) - intervals(1))).toInt
        r = ((normalizedValue - intervals(2)) * 255.0 /
          (intervals(2) - intervals(1))).toInt
        g = 0xff
      case x if x < intervals(3) =>
        g = ((intervals(3) - normalizedValue) * 255.0 /
          (intervals(3) - intervals(2))).toInt
        r = 0xff
      case _ =>
        r = 0xff
    }
    (r << 16) | (g << 8) | b
