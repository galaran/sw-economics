package me.galaran.swe.capture

import me.galaran.swe.overlay.SweOverlay
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.util.concurrent.TimeUnit

object ScreenCapturer {

    private val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize

    @JvmStatic
    fun main(args: Array<String>) {
        SweOverlay.init()

        val robot = Robot()
        while (true) {
            SweOverlay.clearShapes()
            val start = System.nanoTime()

            val screenshot: BufferedImage = robot.createScreenCapture(Rectangle(screenSize))
            val windows: List<Window> = WindowScanner.scan(screenshot, FullImage)

            windows.map { Rectangle(it.x - 10, it.y - 10, it.width + 20, it.height + 20) }.forEach(SweOverlay::addShape)

            val end = System.nanoTime()
            if (SweOverlay.DEBUG) {
                println("${screenshot.width}x${screenshot.height} @ ${TimeUnit.NANOSECONDS.toMillis(end - start)}ms")
                println(windows)
            }

            SweOverlay.update()

            robot.delay(250)
        }
    }
}
