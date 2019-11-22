package me.galaran.swe.capture

import me.galaran.swe.overlay.SweOverlay
import java.awt.*
import java.awt.image.BufferedImage
import java.util.concurrent.TimeUnit

object ScreenCapturer {

    private val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize

    private val windowHeader = Pattern("window_header")

    @JvmStatic
    fun main(args: Array<String>) {
        SweOverlay.init()

        val robot = Robot()
        while (true) {
            val start = System.nanoTime()

            val screenshot: BufferedImage = robot.createScreenCapture(Rectangle(screenSize))
            val windows: List<Point> = windowHeader.findIn(screenshot)

            val end = System.nanoTime()
            if (SweOverlay.DEBUG_MODE) {
                println("${screenshot.width}x${screenshot.height} @ ${TimeUnit.NANOSECONDS.toMillis(end - start)}ms")
                println(windows)
            }

            SweOverlay.clearShapes()
            windows.map { Rectangle(it.x - 60, it.y - 20, 120, 40) }
                .forEach(SweOverlay::addShape)
            SweOverlay.update()

            robot.delay(1000)
        }
    }
}
