package me.galaran.swe.capture.image

import java.awt.Rectangle
import java.awt.image.BufferedImage

class ImageSubimageScanner(private val frameColor: Int) : ImageScanner<Rectangle> {

    override fun findAt(target: BufferedImage, walker: ImageWalker): List<Rectangle> {
        val result = mutableListOf<Rectangle>()

        val ignoredPoints = mutableSetOf<Point>()
        val currentPoint = MutablePoint()
        walker.walkToEnd { x, y ->
            currentPoint.move(x, y)
            if (currentPoint in ignoredPoints) return@walkToEnd

            if (target.getRGB(x, y) != frameColor) return@walkToEnd

            var lastX: Int = x
            FromPointToDirectionWalker(target.size, x, y, WalkDirection.RIGHT).walk { subX, subY ->
                if (target.getRGB(subX, subY) == frameColor) {
                    ignoredPoints += Point(subX, subY)
                    true
                } else {
                    lastX = subX - 1
                    false
                }
            }
            if (lastX - x < 2) return@walkToEnd

            var lastY: Int = y
            FromPointToDirectionWalker(target.size, lastX, y, WalkDirection.DOWN).walk { subX, subY ->
                if (target.getRGB(subX, subY) == frameColor) {
                    ignoredPoints += Point(subX, subY)
                    true
                } else {
                    lastY = subY - 1
                    false
                }
            }
            if (lastY - y < 2) return@walkToEnd

            var restMatched = true
            val restMatcher: (Int, Int) -> Unit = { subX, subY ->
                if (target.getRGB(subX, subY) == frameColor) {
                    ignoredPoints += Point(subX, subY)
                } else {
                    restMatched = false
                }
            }
            FromPointToDirectionWalker(target.size, x, y, WalkDirection.DOWN).walkSteps(lastY - y + 1, restMatcher)
            FromPointToDirectionWalker(target.size, x, lastY, WalkDirection.RIGHT).walkSteps(lastX - x + 1, restMatcher)

            if (restMatched) {
                result += Rectangle(x + 1, y + 1, lastX - x - 1, lastY - y - 1)
            }
        }

        return result
    }
}
