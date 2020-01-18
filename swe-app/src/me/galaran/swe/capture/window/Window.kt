package me.galaran.swe.capture.window

import me.galaran.swe.SweApplication
import me.galaran.swe.capture.image.*
import me.galaran.swe.overlay.OverlayRectangle
import me.galaran.swe.overlay.SweOverlay
import java.awt.Color
import java.awt.Rectangle
import java.awt.image.BufferedImage

abstract class Window(val posAtScreen: Point, val image: BufferedImage) {

    constructor(prototype: UnknownWindow) : this(prototype.posAtScreen, prototype.image)

    val xAtScreen get() = posAtScreen.x
    val yAtScreen get() = posAtScreen.y
    val width get() = image.width
    val height get() = image.height

    abstract val frameColor: ColorRGB

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
    override val frameColor = Color(200, 200, 200).fromAwt()
}

abstract class TitledWindow(prototype: UnknownWindow, titleRegion: Rectangle) : Window(prototype) {

    val titleSubimage: BufferedImage = image.getSubimage(titleRegion)

    lateinit var title: String

    init {
        if (SweApplication.DEBUG) {
            SweOverlay.addComponent(
                OverlayRectangle(xAtScreen + titleRegion.x, yAtScreen + titleRegion.y,
                    titleRegion.width, titleRegion.height, WindowTypePattern.TITLE_RECTANGLE_COLOR, 1)
            )
        }
    }
}

interface WindowTypeMatcher<W : Window> {
    fun tryMatch(win: UnknownWindow): W?
}

private val typeMatchers = listOf<WindowTypeMatcher<*>>(
    SellWindow.Matcher
)
