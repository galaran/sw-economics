package me.galaran.swe.capture.image

import java.awt.Dimension
import java.awt.image.BufferedImage


open class Point(open val x: Int, open val y: Int) {

    fun toAwt() = java.awt.Point(x, y)

    override fun toString() = "Point(x=$x, y=$y)"

    companion object {
        val ZERO = Point(0, 0)
    }
}

class MutablePoint(override var x: Int, override var y: Int) : Point(x, y) {

    constructor() : this(0, 0)

    fun move(x: Int, y: Int): MutablePoint {
        this.x = x
        this.y = y
        return this
    }

    fun translate(vector: Point): MutablePoint {
        x += vector.x
        y += vector.y
        return this
    }

    override fun toString() = "MutablePoint(x=$x, y=$y)"
}

///////////////////////////////////////////////////////////////////////////////////////////////////

val BufferedImage.size get() = Dimension(width, height)

fun BufferedImage.hasPoint(x: Int, y: Int) = x in 0 until width && y in 0 until height

fun BufferedImage.copySubimage(x: Int, y: Int, w: Int, h: Int): BufferedImage {
    val result = BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
    with(result.createGraphics()) {
        drawImage(this@copySubimage.getSubimage(x, y, w, h), 0, 0, null)
        dispose()
    }
    return result
}
