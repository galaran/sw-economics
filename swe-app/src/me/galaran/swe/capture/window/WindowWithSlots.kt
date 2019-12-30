package me.galaran.swe.capture.window

import me.galaran.swe.SweApplication
import me.galaran.swe.capture.image.WindowTypePattern
import me.galaran.swe.capture.image.getSubimage
import me.galaran.swe.capture.image.maxRGBDifference
import me.galaran.swe.capture.image.walkEntire
import me.galaran.swe.overlay.OverlayRectangle
import me.galaran.swe.overlay.SweOverlay
import java.awt.Color
import java.awt.Rectangle

private const val EMPTY_SLOT_MAX_DIFF_WITH_BLACK = 100

abstract class WindowWithSlots(prototype: UnknownWindow, titleRegion: Rectangle, slots: List<Rectangle>)
    : TitledWindow(prototype, titleRegion) {

    protected val filledSlots: List<Rectangle>

    init {
        val filled = mutableListOf<Rectangle>()

        for (slot in slots) {
            val slotImage = image.getSubimage(slot)
            var slotEmpty = true
            slotImage.walkEntire { _, _, rgb ->
                slotEmpty = Color.BLACK.maxRGBDifference(rgb) <= EMPTY_SLOT_MAX_DIFF_WITH_BLACK
                slotEmpty
            }

            if (!slotEmpty) {
                filled += slot
                if (SweApplication.DEBUG) {
                    SweOverlay.addComponent(OverlayRectangle(xAtScreen + slot.x, yAtScreen + slot.y,
                            slot.width, slot.height, WindowTypePattern.SLOT_RECTANGLE_COLOR, 1))
                }
            }
        }

        filledSlots = filled
    }
}
