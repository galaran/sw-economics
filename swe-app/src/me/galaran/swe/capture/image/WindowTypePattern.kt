package me.galaran.swe.capture.image

import java.awt.Color
import java.awt.Rectangle

class WindowTypePattern(name: String) : ImagePattern("window-types/$name") {

    val titleRegion: Rectangle = ImageRegionsFinder(TITLE_RECTANGLE_COLOR.rgb).findAt(patternImage).first().apply {
        x -= zeroPoint.x
        y -= zeroPoint.y
    }
    val slots: List<Rectangle> = ImageRegionsFinder(SLOT_RECTANGLE_COLOR.rgb).findAt(patternImage).map {
        it.x -= zeroPoint.x
        it.y -= zeroPoint.y
        it
    }

    companion object {
        val TITLE_RECTANGLE_COLOR = Color(0, 255, 255)
        val SLOT_RECTANGLE_COLOR = Color(0, 148, 255)
    }
}
