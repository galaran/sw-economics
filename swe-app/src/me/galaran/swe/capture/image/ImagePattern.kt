package me.galaran.swe.capture.image

import me.galaran.swe.SweApplication
import me.galaran.swe.overlay.OverlayPixelPointer
import me.galaran.swe.overlay.OverlayRectangle
import me.galaran.swe.overlay.SweOverlay
import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

private val ZERO_POINT_CROSS_COLOR: Int = Color(255, 0, 110).rgb
private val CONTROL_POINT_EXACT_CROSS_COLOR: Int = Color(76, 255, 0).rgb
private val CONTROL_POINT_APPROX_CROSS_COLOR: Int = Color(255, 216, 0).rgb
private val CONTROL_IMAGE_RECTANGLE_COLOR: Int = Color(255, 0, 220).rgb

private const val APPROX_RGB_MAX_DIFFERENCE = 2

private interface ImageMatcher {
    fun isAtImage(testImage: BufferedImage, zeroPointAtTestImage: Point,
                  showDismatches: Boolean, dismatchesDisplayOffset: Point = Point.ZERO): Boolean
}

open class ImagePattern(val name: String) : ImageMatcher, ImageScanner<Point> {

    private val controlPoints: List<ControlPoint>
    private val controlImages: List<ControlImage>

    private val controls: List<ImageMatcher>

    init {
        val patternImage = ImageIO.read(ImagePattern::class.java.getResourceAsStream("/patterns/$name.png"))

        // Find zero point first
        var zeroPointX = -1
        var zeroPointY = -1
        EntireImageWalker(patternImage.size).walk { x, y ->
            if (patternImage.isCrossAround(x, y, ZERO_POINT_CROSS_COLOR)) {
                zeroPointX = x
                zeroPointY = y
                false
            } else {
                true
            }
        }
        if (zeroPointX == -1 || zeroPointY == -1) throw IllegalStateException("No Zero point in pattern image $name")

        val controlPoints = mutableListOf<ControlPoint>()
        EntireImageWalker(patternImage.size).walkToEnd { x, y ->
            if (patternImage.isCrossAround(x, y, CONTROL_POINT_EXACT_CROSS_COLOR)) {
                controlPoints += ControlPointExact(Point(x - zeroPointX, y - zeroPointY), Color(patternImage.getRGB(x, y)))
            } else if (patternImage.isCrossAround(x, y, CONTROL_POINT_APPROX_CROSS_COLOR)) {
                controlPoints += ControlPointApprox(Point(x - zeroPointX, y - zeroPointY), Color(patternImage.getRGB(x, y)))
            }
        }
        this.controlPoints = controlPoints

        controlImages = ImageSubimageScanner(CONTROL_IMAGE_RECTANGLE_COLOR)
            .findAt(patternImage, EntireImageWalker(patternImage.size))
            .map { ControlImage(Point(it.x - zeroPointX, it.y - zeroPointY), patternImage.copySubimage(it.x, it.y, it.width, it.height)) }

        controls = ArrayList<ImageMatcher>(controlPoints).also { it += controlImages }
    }

    /** @return zero points of found patterns at target image*/
    override fun findAt(target: BufferedImage, walker: ImageWalker): List<Point> {
        val result = mutableListOf<Point>()

        val firstDx = controlPoints[0].point.x
        val firstDy = controlPoints[0].point.y
        val firstColor: Int = controlPoints[0].controlColor.rgb
        val currentPoint = MutablePoint()
        walker.walkToEnd { x, y ->
            val firstX = x + firstDx
            val firstY = y + firstDy
            if (!target.hasPoint(firstX, firstY)) return@walkToEnd
            if (target.getRGB(firstX, firstY) != firstColor) return@walkToEnd

            currentPoint.move(x, y)
            if (isAtImage(target, currentPoint, SweApplication.TRACE)) {
                result += Point(x, y)
            }
        }

        return result
    }

    override fun isAtImage(testImage: BufferedImage, zeroPointAtTestImage: Point,
                           showDismatches: Boolean, dismatchesDisplayOffset: Point): Boolean {
        val mapFun: (ImageMatcher) -> Boolean = { it.isAtImage(testImage, zeroPointAtTestImage, showDismatches, dismatchesDisplayOffset) }
        return if (showDismatches) {
            controls.map(mapFun).all { it } // Do NOT returns after first dismatch
        } else {
            controls.all(mapFun) // Do returns after first dismatch
        }
    }

    private abstract class ControlPoint(val point: Point /* relative to Zero point */, val controlColor: Color) : ImageMatcher {
        operator fun component1() = point
        operator fun component2() = controlColor
    }

    private inner class ControlPointExact(point: Point, exactColor: Color) : ControlPoint(point, exactColor) {

        override fun isAtImage(testImage: BufferedImage, zeroPointAtTestImage: Point,
                               showDismatches: Boolean, dismatchesDisplayOffset: Point): Boolean {
            val x = zeroPointAtTestImage.x + point.x
            val y = zeroPointAtTestImage.y + point.y
            if (!testImage.hasPoint(x, y)) return false

            val actualColor: Int = testImage.getRGB(x, y)
            val isMatch = actualColor == controlColor.rgb

            if (showDismatches && !isMatch) {
                println("Dismatch Point Exact #${controlPoints.indexOf(this)} @ $name> "
                        + "x=${x + dismatchesDisplayOffset.x}, y=${y + dismatchesDisplayOffset.y}: "
                        + "Expected=${formatColor(controlColor.rgb)}, Actual=${formatColor(actualColor)}")
                SweOverlay.addComponent(OverlayPixelPointer(
                    MutablePoint(x, y).translate(dismatchesDisplayOffset).toAwt(), 5,
                    Color(CONTROL_POINT_EXACT_CROSS_COLOR)))
            }

            return isMatch
        }
    }

    private inner class ControlPointApprox(point: Point, approxColor: Color) : ControlPoint(point, approxColor) {

        override fun isAtImage(testImage: BufferedImage, zeroPointAtTestImage: Point,
                               showDismatches: Boolean, dismatchesDisplayOffset: Point): Boolean {
            val x = zeroPointAtTestImage.x + point.x
            val y = zeroPointAtTestImage.y + point.y
            if (!testImage.hasPoint(x, y)) return false

            val maxDifference: Int = controlColor.maxRGBDifference(testImage.getRGB(x, y))
            val isMatch = maxDifference <= APPROX_RGB_MAX_DIFFERENCE

            if (showDismatches && !isMatch) {
                println("Dismatch Point Approx #${controlPoints.indexOf(this)} @ $name> "
                        + "x=${x + dismatchesDisplayOffset.x}, y=${y + dismatchesDisplayOffset.y}: "
                        + "Expected=${formatColor(controlColor.rgb)} +/- $APPROX_RGB_MAX_DIFFERENCE, Actual diff=$maxDifference")
                SweOverlay.addComponent(OverlayPixelPointer(
                    MutablePoint(x, y).translate(dismatchesDisplayOffset).toAwt(), 5,
                    Color(CONTROL_POINT_APPROX_CROSS_COLOR)))
            }

            return isMatch
        }
    }

    private inner class ControlImage(val imageStart: Point /* relative to Zero point */, val controlImage: BufferedImage) : ImageMatcher {

        override fun isAtImage(testImage: BufferedImage, zeroPointAtTestImage: Point,
                               showDismatches: Boolean, dismatchesDisplayOffset: Point): Boolean {
            val startX = zeroPointAtTestImage.x + imageStart.x
            val startY = zeroPointAtTestImage.y + imageStart.y
            val endX = startX + controlImage.width - 1
            val endY = startY + controlImage.height - 1
            if (!testImage.hasPoint(startX, startY) || !testImage.hasPoint(endX, endY)) return false // out of bounds

            var isMatch = true
            EntireImageWalker(controlImage.size).walk { x, y ->
                if (controlImage.getRGB(x, y) != testImage.getRGB(startX + x, startY + y)) {
                    isMatch = false
                    return@walk false
                }
                return@walk true
            }

            if (showDismatches && !isMatch) {
                println("Dismatch Image #${controlImages.indexOf(this)} @ $name> "
                        + "x=${startX + dismatchesDisplayOffset.x}, y=${startY + dismatchesDisplayOffset.y}, "
                        + "size=${controlImage.size}")
                SweOverlay.addComponent(OverlayRectangle(startX + dismatchesDisplayOffset.x - 2, startY + dismatchesDisplayOffset.y - 2,
                    controlImage.width + 4, controlImage.height + 4, Color(CONTROL_IMAGE_RECTANGLE_COLOR), 2)
                )
            }

            return isMatch
        }
    }
}

private fun BufferedImage.isCrossAround(x: Int, y: Int, crossColor: Int): Boolean {
    return hasPoint(x - 1, y - 1) && hasPoint(x + 1, y + 1)
            && getRGB(x - 1, y) == crossColor && getRGB(x + 1, y) == crossColor
            && getRGB(x, y - 1) == crossColor && getRGB(x, y + 1) == crossColor
}

private fun formatColor(color: Int?): String = color?.let { Integer.toHexString(it).toUpperCase().substring(2) } ?: "null"
