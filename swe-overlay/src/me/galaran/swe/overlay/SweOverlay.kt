package me.galaran.swe.overlay

import java.awt.*
import java.awt.geom.Line2D
import java.util.concurrent.ConcurrentLinkedQueue
import javax.swing.JComponent

object SweOverlay {

    var DEBUG = true
    var TRACE = true

    private val shapes = ConcurrentLinkedQueue<Shape>()

    private val screenSurface = object : JComponent() {
        override fun paintComponent(g: Graphics) {
            val g2d = g as Graphics2D

            for (shape in shapes) {
                g2d.stroke = when (shape) {
                    is Line2D -> BasicStroke(1f)
                    else -> BasicStroke(3f)
                }
                g2d.color = when (shape) {
                    is Line2D -> Color(255, 0, 0, 255)
                    else -> Color(0, 255, 0, 150)
                }

                g2d.draw(shape)
            }
        }

        private val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
        override fun getPreferredSize() = screenSize
    }

    fun init() {
        val w = Window(null)
        w.add(screenSurface)
        w.pack()
        w.setLocationRelativeTo(null)
        w.isVisible = true
        w.isAlwaysOnTop = true
        w.background = Color(0, 0, 0, 0)
    }

    fun addShape(rectangle: Rectangle) {
        shapes += rectangle
    }

    fun addPixelCross(pointAtScreen: Point) {
        shapes += Line(pointAtScreen.x, pointAtScreen.y - 3, pointAtScreen.x, pointAtScreen.y - 1).toShape()
        shapes += Line(pointAtScreen.x, pointAtScreen.y + 1, pointAtScreen.x, pointAtScreen.y + 3).toShape()
        shapes += Line(pointAtScreen.x - 3, pointAtScreen.y, pointAtScreen.x - 1, pointAtScreen.y).toShape()
        shapes += Line(pointAtScreen.x + 1, pointAtScreen.y, pointAtScreen.x + 3, pointAtScreen.y).toShape()
    }

    fun clearShapes() = shapes.clear()

    fun update() = screenSurface.repaint()
}

data class Line(val x1: Int, val y1: Int, val x2: Int, val y2: Int) {

    fun toShape(): Shape = Line2D.Float(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat())
}
