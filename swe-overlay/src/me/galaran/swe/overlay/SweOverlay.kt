package me.galaran.swe.overlay

import java.awt.*
import java.util.concurrent.ConcurrentLinkedQueue
import javax.swing.JComponent

object SweOverlay {

    var DEBUG = true
    var TRACE = true

    private val overlayComponents = ConcurrentLinkedQueue<OverlayComponent>()

    private val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize

    private val screenSurface = object : JComponent() {
        override fun paintComponent(g: Graphics) = overlayComponents.forEach { it.drawOn(g as Graphics2D) }
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

    fun addComponent(component: OverlayComponent) {
        overlayComponents += component
    }

    fun clear() = overlayComponents.clear()

    fun update() = screenSurface.repaint()
}
