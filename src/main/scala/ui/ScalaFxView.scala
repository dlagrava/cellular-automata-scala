package ui

import javafx.scene.image.PixelFormat
import scalafx.scene.image.{ImageView, WritableImage}
import view.{AutomataView, CellColorMap}

class ScalaFxView[T](
    colorMap: CellColorMap[T],
    width: Int,
    height: Int,
    cellPx: Int = 3
) extends AutomataView[T]:

  private val imgW = width * cellPx
  private val imgH = height * cellPx
  private val image = new WritableImage(imgW, imgH)
  private val pixelWriter = image.pixelWriter
  private val buffer = new Array[Int](imgW * imgH)
  private val pixelFormat = PixelFormat.getIntArgbInstance

  val imageView: ImageView = new ImageView(image)

  def render(generation: Int, grid: Array[Array[T]]): Unit =
    var row = 0
    while row < height do
      val gridRow = grid(row)
      var col = 0
      while col < width do
        val argb = colorMap.toArgb(gridRow(col))
        val baseY = row * cellPx
        val baseX = col * cellPx
        var dy = 0
        while dy < cellPx do
          val outRow = (baseY + dy) * imgW + baseX
          var dx = 0
          while dx < cellPx do
            buffer(outRow + dx) = argb
            dx += 1
          dy += 1
        col += 1
      row += 1
    pixelWriter.setPixels(0, 0, imgW, imgH, pixelFormat, buffer, 0, imgW)

  override def close(): Unit = ()
