package me.galaran.swe.capture.window

import me.galaran.swe.capture.image.Point
import me.galaran.swe.capture.image.WindowTypePattern
import java.awt.Color
import java.awt.Rectangle

class SellWindow private constructor(prototype: UnknownWindow, titleRegion: Rectangle, slots: List<Rectangle>)
    : WindowWithSlots(prototype, titleRegion, slots) {

    override val frameColor = Color(202, 109, 217)
    override val properties: List<Pair<String, String?>> get() = listOf("sellerName" to sellerName)

    lateinit var sellerName: String

    companion object Matcher : WindowTypeMatcher<SellWindow> {

        private val sellWindowPattern = WindowTypePattern("sell")

        override fun tryMatch(win: UnknownWindow): SellWindow? {
            if (sellWindowPattern.isAtImage(win.image, Point.ZERO, true, win.posAtScreen)) {
                return SellWindow(win, sellWindowPattern.titleRegion, sellWindowPattern.slots).also { it.sellerName = "ХЗ" }
            }
            return null
        }
    }
}
