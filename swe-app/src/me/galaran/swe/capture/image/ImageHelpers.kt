package me.galaran.swe.capture.image

import java.awt.Color
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.image.BufferedImage
import kotlin.math.absoluteValue
import kotlin.math.max


open class Point(open val x: Int, open val y: Int) {

    operator fun plus(vector: Point) = Point(x + vector.x, y + vector.y)
    operator fun minus(vector: Point) = Point(x - vector.x, y - vector.y)

    override fun toString() = "Point(x=$x, y=$y)"

    companion object {
        val ZERO = Point(0, 0)
    }
}

class MutablePoint(override var x: Int, override var y: Int) : Point(x, y) {

    constructor() : this(0, 0)

    fun set(x: Int, y: Int): MutablePoint {
        this.x = x
        this.y = y
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

fun BufferedImage.getSubimage(region: Rectangle): BufferedImage = getSubimage(region.x, region.y, region.width, region.height)

inline fun BufferedImage.walkEntire(crossinline action: (x: Int, y: Int, rgb: Int) -> Boolean /* continue? */) {
    EntireImageWalker(this.size).walk { x, y -> action(x, y, this.getRGB(x, y)) }
}

fun Color.maxRGBDifference(otherRGB: Int): Int {
    val rDiff = (red - ((otherRGB shr 16) and 0xFF)).absoluteValue
    val gDiff = (green - ((otherRGB shr 8) and 0xFF)).absoluteValue
    val bDiff = (blue - (otherRGB and 0xFF)).absoluteValue
    return max(rDiff, max(gDiff, bDiff))
}
