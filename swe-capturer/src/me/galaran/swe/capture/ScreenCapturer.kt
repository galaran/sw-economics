package me.galaran.swe.capture

import me.galaran.swe.capture.window.UnknownWindow
import me.galaran.swe.capture.window.Window
import me.galaran.swe.capture.window.WindowScanner
import me.galaran.swe.overlay.OverlayRectangle
import me.galaran.swe.overlay.SweOverlay
import java.awt.image.BufferedImage
import java.util.concurrent.TimeUnit

object ScreenCapturer {

    var DEBUG = true
    var TRACE = true

    @JvmStatic
    fun main(args: Array<String>) {
        SweOverlay.init()

        while (true) {
            Thread.sleep(1000)
            val start = System.nanoTime()

            val windowImage: BufferedImage = Win32ForegroundWindowCapturer.capture()?.image ?: continue

            val detectedWindows = SweOverlay.update {
                val windows: List<UnknownWindow> = WindowScanner.findAt(windowImage, EntireImageWalker(windowImage.size))
                val detectedWindows: List<Window> = windows.map { Window.detectType(it) ?: it }

                detectedWindows
                    .map { OverlayRectangle(it.xAtScreen, it.yAtScreen, it.width, it.height, it.frameColor, 3) }
                    .forEach(SweOverlay::addComponent)

                detectedWindows
            }
            val end = System.nanoTime()

            if (DEBUG) {
                println("${windowImage.width}x${windowImage.height} @ ${TimeUnit.NANOSECONDS.toMillis(end - start)}ms")
                println(detectedWindows)
            }
        }
    }
}
