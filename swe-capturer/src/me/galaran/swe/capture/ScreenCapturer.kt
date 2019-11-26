package me.galaran.swe.capture

import me.galaran.swe.capture.window.UnknownWindow
import me.galaran.swe.capture.window.Window
import me.galaran.swe.capture.window.WindowScanner
import me.galaran.swe.overlay.OverlayRectangle
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
            SweOverlay.clear()
            val start = System.nanoTime()

            val screenshot: BufferedImage = robot.createScreenCapture(Rectangle(screenSize))
            val windows: List<UnknownWindow> = WindowScanner.findAt(screenshot, EntireImageWalker(screenshot.size))

            val detectedWindows: List<Window> = windows.map { Window.detectType(it) ?: it }

            detectedWindows
                .map { OverlayRectangle(it.xAtScreen - 10, it.yAtScreen - 10, it.width + 20, it.height + 20, it.frameColor, 3) }
                .forEach(SweOverlay::addComponent)

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
