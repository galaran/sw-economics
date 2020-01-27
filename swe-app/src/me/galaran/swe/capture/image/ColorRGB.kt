package me.galaran.swe.capture.image

import java.awt.Color
import kotlin.math.absoluteValue
import kotlin.math.max

inline class ColorRGB(val value: Int) {

    private val red: Int get() = (value shr 16) and 0xFF
    private val green: Int get() = (value shr 8) and 0xFF
    private val blue: Int get() = value and 0xFF

    fun inverted() = ColorRGB(value xor 0x00FFFFFF)
    fun blackOrWhite(): ColorRGB = (if (red + green + blue > 384) Color.WHITE else Color.BLACK).fromAwt()

    fun maxRGBDifference(other: ColorRGB): Int {
        val rDiff = (red - other.red).absoluteValue
        val gDiff = (green - other.green).absoluteValue
        val bDiff = (blue - other.blue).absoluteValue
        return max(rDiff, max(gDiff, bDiff))
    }

    override fun toString() = Integer.toHexString(value).toUpperCase().substring(2)
}

fun Color.fromAwt() = ColorRGB(this.rgb)
