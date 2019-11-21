package me.galaran.swe.overlay

import java.awt.*
import java.awt.event.InputEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import java.util.concurrent.TimeUnit
import javax.swing.JComponent
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.system.exitProcess

object ScreenCapturer {

    private val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize

    @JvmStatic
    fun main(args: Array<String>) {
        val robot = Robot()
        robot.delay(5000)
        while (true) {
            val start = System.nanoTime()
            val screenshot = robot.createScreenCapture(Rectangle(screenSize))
            val end = System.nanoTime()
            println("${screenshot.hashCode()} ${screenshot.width}x${screenshot.height} @ ${TimeUnit.NANOSECONDS.toMillis(end - start)}ms")
            robot.delay(200)
        }
    }
}
