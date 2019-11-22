package me.galaran.swe.overlay

import java.awt.*
import java.util.concurrent.ConcurrentLinkedQueue
import javax.swing.JComponent

object SweOverlay {

    var DEBUG_MODE = true

    private val shapes = ConcurrentLinkedQueue<Rectangle>()

    private val screenSurface = object : JComponent() {
        override fun paintComponent(g: Graphics) {
            val g2d = g as Graphics2D
            g2d.stroke = BasicStroke(3f)
            g2d.color = Color(0, 255, 0, 150)

            shapes.forEach(g2d::draw)
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

    fun clearShapes() = shapes.clear()

    fun update() = screenSurface.repaint()
}
