package me.galaran.swe.capture

import java.awt.Dimension

sealed class ImageWalker(protected val imageSize: Dimension) {

    abstract fun walk(action: (x: Int, y: Int) -> Boolean /* continue? */)

    inline fun walkToEnd(crossinline action: (x: Int, y: Int) -> Unit) {
        walk { x, y ->
            action(x, y)
            true
        }
    }

    inline fun walkSteps(steps: Int, crossinline action: (x: Int, y: Int) -> Unit) {
        var walked = 0
        walk { x, y ->
            action(x, y)
            walked++
            return@walk walked < steps
        }
    }
}

class EntireImageWalker(imageSize: Dimension) : ImageWalker(imageSize) {

    override fun walk(action: (x: Int, y: Int) -> Boolean) {
        for (y in 0 until imageSize.height) {
            for (x in 0 until imageSize.width) {
                if (!action(x, y)) return
            }
        }
    }
}

class FromPointToDirectionWalker(imageSize: Dimension, private val fromX: Int, private val fromY: Int,
                                 private val direction: WalkDirection) : ImageWalker(imageSize) {

    constructor(imageSize: Dimension, fromPoint: Point, direction: WalkDirection) : this(imageSize, fromPoint.x, fromPoint.y, direction)

    override fun walk(action: (x: Int, y: Int) -> Boolean) {
        var x = fromX
        var y = fromY
        while (x in 0 until imageSize.width && y in 0 until imageSize.height) {
            if (!action(x, y)) return
            x += direction.translation.x
            y += direction.translation.y
        }
    }
}

enum class WalkDirection {
    UP { override val translation = Point(0, -1) },
    DOWN { override val translation = Point(0, 1) },
    LEFT { override val translation = Point(-1, 0) },
    RIGHT { override val translation = Point(1, 0) };

    abstract val translation: Point
}
