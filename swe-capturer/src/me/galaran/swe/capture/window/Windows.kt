package me.galaran.swe.capture.window

import me.galaran.swe.capture.Point
import me.galaran.swe.capture.WindowTypePattern
import java.awt.Color
import java.awt.image.BufferedImage

sealed class Window(val posAtScreen: Point, val image: BufferedImage) {

    constructor(prototype: UnknownWindow) : this(prototype.posAtScreen, prototype.image)

    val xAtScreen get() = posAtScreen.x
    val yAtScreen get() = posAtScreen.y
    val width get() = image.width
    val height get() = image.height

    abstract val frameColor: Color

    protected open val properties: List<Pair<String, String?>> get() = emptyList()

    override fun toString(): String {
        var propertiesString = properties.joinToString(", ") { "${it.first}=${it.second}" }
        if (propertiesString.isNotEmpty()) {
            propertiesString = ", $propertiesString"
        }
        return "${this::class.simpleName}(x=$xAtScreen, y=$yAtScreen, width=$width, height=$height$propertiesString)"
    }

    companion object {
        fun detectType(win: UnknownWindow): Window? {
            for (matcher in typeMatchers) {
                val matchedWindow = matcher.tryMatch(win)
                if (matchedWindow != null) {
                    return matchedWindow
                }
            }
            return null
        }
    }
}

class UnknownWindow(posAtScreen: Point, image: BufferedImage) : Window(posAtScreen, image) {
    override val frameColor = Color(200, 200, 200)
}

private interface WindowTypeMatcher<W : Window> {
    fun tryMatch(win: UnknownWindow): W?
}

private val typeMatchers = listOf<WindowTypeMatcher<*>>(
    SellWindow.Matcher
)

class SellWindow private constructor(prototype: UnknownWindow) : Window(prototype) {

    override val frameColor = Color(202, 109, 217)
    override val properties: List<Pair<String, String?>> get() = listOf("sellerName" to sellerName)

    var sellerName: String? = null

    companion object Matcher : WindowTypeMatcher<SellWindow> {

        private val sellWindowPattern = WindowTypePattern("sell")

        override fun tryMatch(win: UnknownWindow): SellWindow? {
            if (sellWindowPattern.isAtImage(win.image, Point.ZERO, true, win.posAtScreen)) {
                return SellWindow(win).also { it.sellerName = "ХЗ" }
            }
            return null
        }
    }
}
