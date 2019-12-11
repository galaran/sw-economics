package me.galaran.swe.capture.window

import me.galaran.swe.SweApplication
import me.galaran.swe.capture.image.*
import java.awt.image.BufferedImage

object WindowScanner : ImageScanner<Window> {

    private val windowHeader = ImagePattern("window_header")
    private val windowTopRight = ImagePattern("window_top_right")
    private val windowTopLeft = ImagePattern("window_top_left")
    private val windowBottomLeft = ImagePattern("window_bottom_left")

    override fun findAt(target: BufferedImage, walker: ImageWalker): List<UnknownWindow> {
        val result = mutableListOf<UnknownWindow>()

        for (header in windowHeader.findAt(target, walker)) {
            val topRight: Point? = windowTopRight.findAt(target,
                FromPointToDirectionWalker(target.size, header, WalkDirection.RIGHT)).firstOrNull()
            val topLeft: Point? = windowTopLeft.findAt(target,
                FromPointToDirectionWalker(target.size, header, WalkDirection.LEFT)).firstOrNull()

            if (SweApplication.TRACE) {
                println("window> Header: $header | topLeft: $topLeft, topRight: $topRight")
            }

            if (topRight != null && topLeft != null) {
                val bottomLeft: Point? = windowBottomLeft.findAt(target,
                    FromPointToDirectionWalker(target.size, topLeft, WalkDirection.DOWN)).firstOrNull()
                if (bottomLeft != null) {
                    result += UnknownWindow(topLeft,
                        target.copySubimage(topLeft.x, topLeft.y, topRight.x - topLeft.x + 1, bottomLeft.y - topLeft.y + 1))
                }
            } else {
                if (SweApplication.DEBUG) {
                    windowTopLeft.isAtImage(target, Point(header.x - 255, header.y), true)
                    windowTopLeft.isAtImage(target, Point(header.x + 253, header.y), true)
                }
            }
        }

        return result
    }
}
