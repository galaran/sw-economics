package me.galaran.swe.capture

import me.galaran.swe.overlay.SweOverlay
import java.awt.Point
import java.awt.image.BufferedImage

object WindowScanner : ImageScanner<Window> {

    private val windowHeader = Pattern("window_header")
    private val windowTopRight = Pattern("window_top_right")
    private val windowTopLeft = Pattern("window_top_left")
    private val windowBottomLeft = Pattern("window_bottom_left")

    override fun scan(image: BufferedImage, walker: ImageWalker): List<Window> {
        val result = mutableListOf<Window>()

        for (header in windowHeader.scan(image, walker)) {
            val topRight: Point? = windowTopRight.scan(image, FromPointToDirection(header, WalkDirection.RIGHT)).firstOrNull()
            val topLeft: Point? = windowTopLeft.scan(image, FromPointToDirection(header, WalkDirection.LEFT)).firstOrNull()

            if (SweOverlay.TRACE) {
                println("window> Header: $header | topLeft: $topLeft, topRight: $topRight")
            }

            if (topRight != null && topLeft != null) {
                val bottomLeft: Point? = windowBottomLeft.scan(image, FromPointToDirection(topLeft, WalkDirection.DOWN)).firstOrNull()
                if (bottomLeft != null) {
                    result += Window(topLeft.x, topLeft.y, topRight.x - topLeft.x, bottomLeft.y - topLeft.y)
                }
            } else {
                if (SweOverlay.DEBUG && header.x == 306 && header.y == 339) {
                    windowTopLeft.getColorDismatches(image, Point(51, 339)).forEach(SweOverlay::addPixelCross)
                    windowTopRight.getColorDismatches(image, Point(559, 339)).forEach(SweOverlay::addPixelCross)
                }
            }
        }

        return result
    }
}
