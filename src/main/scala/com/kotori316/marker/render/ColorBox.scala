package com.kotori316.marker.render

class ColorBox(val red: Int, val green: Int, val blue: Int, val alpha: Int)

object ColorBox {
  def apply(red: Int, green: Int, blue: Int, alpha: Int) = new ColorBox(red, green, blue, alpha)

  def unapply(arg: ColorBox): Option[(Int, Int, Int, Int)] = Some((arg.red, arg.green, arg.blue, arg.alpha))

  val redColor = ColorBox(0xFF, 0x3D, 0x63, 0xFF)
  val blueColor = ColorBox(0x75, 0xCC, 0xFF, 0xFF)
  val white = ColorBox(0xFF, 0xFF, 0xFF, 0xFF)
}
