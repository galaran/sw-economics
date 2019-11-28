package me.galaran.swe.overlay

import java.awt.*
import java.util.concurrent.ConcurrentLinkedQueue
import javax.swing.JComponent

object SweOverlay {

    private val overlayComponents = ConcurrentLinkedQueue<OverlayComponent>()

    private val drawSurface = object : JComponent() {
        override fun paintComponent(g: Graphics) = overlayComponents.forEach { it.drawOn(g as Graphics2D) }
        override fun getPreferredSize() = mainScreenSize
    }
    private val mainScreenSize: Dimension = Toolkit.getDefaultToolkit().screenSize

    fun init() {
        val w = Window(null)
        w.add(drawSurface)
        w.pack()
        w.setLocationRelativeTo(null)
        w.isVisible = true
        w.isAlwaysOnTop = true
        w.background = Color(0, 0, 0, 0)
    }

    private var nowUpdating = false

    fun <T> update(block: () -> T): T {
        overlayComponents.clear()
        nowUpdating = true
        val result = block()
        nowUpdating = false
        drawSurface.repaint()
        return result
    }

    fun addComponent(component: OverlayComponent) {
        if (nowUpdating) {
            overlayComponents += component
        } else throw IllegalStateException()
    }
}
