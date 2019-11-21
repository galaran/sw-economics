package me.galaran.swe.overlay

import java.awt.*
import java.awt.event.InputEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import javax.swing.JComponent
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.system.exitProcess

object SweOverlay {

    private const val WIN_SIZE = 800
    private const val CIRCLE_RADIUS_MIN = 100
    private const val CENTER_RADIUS = 10

    private var circleRadius = 250

    private val w = Window(null)

    @JvmStatic
    fun main(args: Array<String>) {
        val c = object : JComponent() {
            override fun paintComponent(g: Graphics) {
                val g2d = g as Graphics2D
                g2d.stroke = BasicStroke(3f)

                g2d.color = Color(1f, 0f, 0f, 0.4f)
                g2d.drawOval(WIN_SIZE / 2 - circleRadius, WIN_SIZE / 2 - circleRadius, circleRadius * 2, circleRadius * 2)

                g2d.color = Color(0f, 1f, 0f, 0.7f)
                g2d.fillOval(WIN_SIZE / 2 - CENTER_RADIUS, WIN_SIZE / 2 - CENTER_RADIUS, CENTER_RADIUS * 2, CENTER_RADIUS * 2)
            }

            override fun getPreferredSize() = Dimension(WIN_SIZE, WIN_SIZE)
        }

        c.layout = GridBagLayout()
        c.add(object : JComponent() {
            init {
                object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent) {
                        when (e.button) {
                            MouseEvent.BUTTON1 -> exitProcess(0)
                            MouseEvent.BUTTON3 -> drawCirclePolygon()
                        }
                    }

                    override fun mouseWheelMoved(e: MouseWheelEvent) {
                        circleRadius = (circleRadius + e.wheelRotation * 4).coerceAtLeast(CIRCLE_RADIUS_MIN)
                        c.repaint()
                    }

                    var dragFrom: Point? = null

                    override fun mousePressed(e: MouseEvent) {
                        dragFrom = e.point
                    }
                    override fun mouseDragged(e: MouseEvent) {
                        dragFrom?.let {
                            w.location = Point(w.location.x + e.x - it.x, w.location.y + e.y - it.y)
                        }
                    }
                    override fun mouseReleased(e: MouseEvent) {
                        dragFrom = null
                    }
                }.also(::addMouseListener).also(::addMouseWheelListener).also(::addMouseMotionListener)
            }

            override fun getPreferredSize() = Dimension(CENTER_RADIUS * 2, CENTER_RADIUS * 2)
        })

        w.add(c)
        w.pack()
        w.setLocationRelativeTo(null)
        w.isVisible = true
        w.isAlwaysOnTop = true
        w.background = Color(0, 0, 0, 0)
    }

    private fun drawCirclePolygon() {
        val centerOnScreenX = w.locationOnScreen.x + w.width / 2
        val centerOnScreenY = w.locationOnScreen.y + w.height / 2

        w.isVisible = false

        val robot = Robot().apply { autoDelay = 200 }
        robot.delay(500)
        for (degrees in 0..360 step 15) {
            val radians = Math.toRadians(degrees.toDouble())
            val x = centerOnScreenX + circleRadius * cos(radians)
            val y = centerOnScreenY + circleRadius * sin(radians)
            robot.mouseMove(x.roundToInt(), y.roundToInt())
            robot.mousePress(InputEvent.getMaskForButton(MouseEvent.BUTTON1))
            robot.mouseRelease(InputEvent.getMaskForButton(MouseEvent.BUTTON1))
        }

        w.isVisible = true
    }
}
