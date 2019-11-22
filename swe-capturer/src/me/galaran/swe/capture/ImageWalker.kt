package me.galaran.swe.capture

import java.awt.Point

sealed class ImageWalker {

    abstract fun walk(imageWidth: Int, imageHeight: Int, action: (x: Int, y: Int) -> Unit)
}

object FullImage : ImageWalker() {

    override fun walk(imageWidth: Int, imageHeight: Int, action: (x: Int, y: Int) -> Unit) {
        for (y in 0 until imageHeight) {
            for (x in 0 until imageWidth) {
                action(x, y)
            }
        }
    }
}

class FromPointToDirection(private val point: Point, private val direction: WalkDirection) : ImageWalker() {

    override fun walk(imageWidth: Int, imageHeight: Int, action: (x: Int, y: Int) -> Unit) {
        var x = point.x
        var y = point.y
        while (x in 0 until imageWidth && y in 0 until imageHeight) {
            action(x, y)
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
