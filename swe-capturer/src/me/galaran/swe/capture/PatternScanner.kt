package me.galaran.swe.capture

import me.galaran.swe.overlay.SweOverlay
import java.awt.Color
import java.awt.Point
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class Pattern(private val name: String) : ImageScanner<Point> {

    private lateinit var basePoint: Point
    private val controlPoints: MutableList<Pair<Point, Color>> = mutableListOf()

    /** Relative to base point */
    override fun scan(image: BufferedImage, walker: ImageWalker): List<Point> {
        val result = mutableListOf<Point>()

        val firstColor: Int = controlPoints[0].second.rgb
        val firstDx = controlPoints[0].first.x - basePoint.x
        val firstDy = controlPoints[0].first.y - basePoint.y
        walker.walk(image.width, image.height) { x, y ->
            val firstX = x + firstDx
            val firstY = y + firstDy
            if (firstX !in 0 until image.width || firstY !in 0 until image.height) return@walk
            if (image.getRGB(firstX, firstY) != firstColor) return@walk

            val match = controlPoints.map {
                val pointX = x + it.first.x - basePoint.x
                val pointY = y + it.first.y - basePoint.y
                if (pointX in 0 until image.width && pointY in 0 until image.height) {
                    return@map image.getRGB(pointX, pointY)
                } else {
                    null
                }
            }.also {
                if (SweOverlay.TRACE) {
                    println(name + "> " + it.map(::formatColor))
                }
            }.mapIndexed { index, color ->
                    color == controlPoints[index].second.rgb
            }.also { if (SweOverlay.TRACE) println("$name> $it")
            }.all { it }

            if (match) {
                result += Point(x, y)
            }
        }

        return result
    }

    fun getColorDismatches(image: BufferedImage, basePointAtScreen: Point): List<Point> {
        val result = mutableListOf<Point>()

        for ((point, expectedColor) in controlPoints) {
            val x = basePointAtScreen.x + point.x - basePoint.x
            val y = basePointAtScreen.y + point.y - basePoint.y
            val actualColor = image.getRGB(x, y)
            if (expectedColor.rgb != actualColor) {
                result += Point(x, y)
                if (SweOverlay.DEBUG) {
                    println("Dismatch @ $name> x=$x, y=$y: Expected=${formatColor(expectedColor.rgb)}, Actual=${formatColor(actualColor)}")
                }
            }
        }

        return result
    }

    private fun formatColor(color: Int?): String = color?.let { Integer.toHexString(it).toUpperCase().substring(2) } ?: "null"

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
