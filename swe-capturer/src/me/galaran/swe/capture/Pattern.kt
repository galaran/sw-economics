package me.galaran.swe.capture

import me.galaran.swe.overlay.SweOverlay
import java.awt.Color
import java.awt.Point
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class Pattern(name: String) {

    private lateinit var basePoint: Point
    private val controlPoints: MutableList<Pair<Point, Color>> = mutableListOf()

    /** Relative to base point */
    fun findIn(image: BufferedImage): List<Point> {
        val result = mutableListOf<Point>()

        val firstColor: Int = controlPoints[0].second.rgb
        val firstDx = controlPoints[0].first.x - basePoint.x
        val firstDy = controlPoints[0].first.y - basePoint.y
        for (y in 0 until image.height) {
            val firstY = y + firstDy
            if (firstY !in 0 until image.height) continue
            for (x in 0 until image.width) {
                val firstX = x + firstDx
                if (firstX !in 0 until image.width) continue
                if (image.getRGB(firstX, firstY) != firstColor) continue

                if (controlPoints.map {
                    val pointX = x + it.first.x - basePoint.x
                    val pointY = y + it.first.y - basePoint.y
                    if (pointX in 0 until image.width && pointY in 0 until image.height) {
                        return@map image.getRGB(pointX, pointY) == it.second.rgb
                    } else {
                        false
                    }
                }.also { if (SweOverlay.DEBUG_MODE) println(it) }.all { it }) {
                    result += Point(x, y)
                }
            }
        }

        return result
    }

    init {
        val patternImage = ImageIO.read(Pattern::class.java.getResourceAsStream("/patterns/$name.png"))

        val ignoredPoints = mutableSetOf<Point>()
        val workingPoint = Point()
        var basePointFound = false

        for (y in 0 until patternImage.height) {
            for (x in 0 until patternImage.width) {
                workingPoint.move(x, y)
                if (ignoredPoints.contains(workingPoint)) continue

                var isCross = false

                if (!basePointFound && patternImage.isCrossAt(x, y, MAIN_POINT_CROSS_COLOR)) {
                    isCross = true

                    basePoint = Point(x, y + 1)
                    basePointFound = true
                } else if (patternImage.isCrossAt(x, y, CONTROL_POINT_CROSS_COLOR)) {
                    isCross = true

                    controlPoints += Point(x, y + 1) to Color(patternImage.getRGB(x, y + 1))
                }

                if (isCross) {
                    ignoredPoints += Point(x - 1, y + 1)
                    ignoredPoints += Point(x + 1, y + 1)
                    ignoredPoints += Point(x, y + 2)
                }
            }
        }
    }
}

private fun BufferedImage.isCrossAt(x: Int, y: Int, crossColor: Int): Boolean {
    if (getRGB(x, y) != crossColor) return false
    if (getRGB(x - 1, y + 1) != crossColor) return false
    if (getRGB(x + 1, y + 1) != crossColor) return false
    if (getRGB(x, y + 2) != crossColor) return false
    return true
}

private const val MAIN_POINT_CROSS_COLOR: Int = 0xFFFF006E.toInt()
private const val CONTROL_POINT_CROSS_COLOR: Int = 0xFF4CFF00.toInt()
