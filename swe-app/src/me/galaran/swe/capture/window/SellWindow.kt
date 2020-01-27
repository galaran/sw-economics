package me.galaran.swe.capture.window

import me.galaran.swe.capture.image.Point
import me.galaran.swe.capture.image.WindowTypePattern
import me.galaran.swe.capture.image.fromAwt
import me.galaran.swe.ocr.OCR
import java.awt.Color
import java.awt.Rectangle

class SellWindow private constructor(prototype: UnknownWindow, titleRegion: Rectangle, slots: List<Rectangle>)
    : WindowWithSlots(prototype, titleRegion, slots) {

    override val frameColor = Color(202, 109, 217).fromAwt()
    override val properties: List<Pair<String, String?>> get() = listOf("sellerName" to sellerName)

    private var sellerName: String

    init {
        title = OCR.ocrTitle(titleSubimage)
        sellerName = "???"
    }

    companion object Matcher : WindowTypeMatcher<SellWindow> {

        private val sellWindowPattern = WindowTypePattern("sell")

        override fun tryMatch(win: UnknownWindow): SellWindow? {
            if (sellWindowPattern.isAtImage(win.image, Point.ZERO, true, win.posAtScreen)) {
                return SellWindow(win, sellWindowPattern.titleRegion, sellWindowPattern.slots)
            }
            return null
        }
    }
}
