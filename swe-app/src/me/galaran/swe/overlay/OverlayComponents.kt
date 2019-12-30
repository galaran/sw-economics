package me.galaran.swe.overlay

import me.galaran.swe.capture.image.Point
import java.awt.*
import java.awt.geom.Line2D

sealed class OverlayComponent(protected val position: Point, protected val mainColor: Color) {

    abstract fun drawOn(g2d: Graphics2D)
}

class OverlayRectangle(position: Point, private val dimension: Dimension, color: Color, private val strokeWidth: Int)
    : OverlayComponent(position, color) {

    constructor(x: Int, y: Int, width: Int, height: Int, color: Color, strokeWidth: Int)
            : this(Point(x, y), Dimension(width, height), color, strokeWidth)

    override fun drawOn(g2d: Graphics2D) {
        g2d.color = mainColor
        g2d.stroke = BasicStroke(strokeWidth.toFloat())
        g2d.draw(Rectangle(position.toAwt(), dimension))
    }
}

class OverlayFilledRectangle(position: Point, private val dimension: Dimension, color: Color) : OverlayComponent(position, color) {

    constructor(x: Int, y: Int, width: Int, height: Int, color: Color) : this(Point(x, y), Dimension(width, height), color)

    override fun drawOn(g2d: Graphics2D) {
        g2d.color = mainColor
        g2d.fill(Rectangle(position.toAwt(), dimension))
    }
}

class OverlayLine(pos1: Point, private val pos2: Point, private val width: Int, color: Color) : OverlayComponent(pos1, color) {

    override fun drawOn(g2d: Graphics2D) {
        g2d.color = mainColor
        g2d.stroke = BasicStroke(width.toFloat())
        g2d.draw(Line2D.Float(position.toAwt(), pos2.toAwt()))
    }
}

class OverlayPixelPointer(pixelPosition: Point, private val pointerLength: Int, color: Color) : OverlayComponent(pixelPosition, color) {

    override fun drawOn(g2d: Graphics2D) {
        g2d.color = mainColor
        g2d.stroke = BasicStroke(1f)

        OverlayLine(Point(position.x, position.y - 1 - pointerLength), Point(position.x, position.y - 1), 1, mainColor).drawOn(g2d)
        OverlayLine(Point(position.x, position.y + 1), Point(position.x, position.y + 1 + pointerLength), 1, mainColor).drawOn(g2d)
        OverlayLine(Point(position.x - 1 - pointerLength, position.y), Point(position.x - 1, position.y), 1, mainColor).drawOn(g2d)
        OverlayLine(Point(position.x + 1, position.y), Point(position.x + 1 + pointerLength, position.y), 1, mainColor).drawOn(g2d)
    }
}

private fun Point.toAwt() = java.awt.Point(x, y)
