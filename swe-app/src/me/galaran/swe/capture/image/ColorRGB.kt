package me.galaran.swe.capture.image

import java.awt.Color

inline class ColorRGB(val value: Int) {

    val red: Int get() = (value shr 16) and 0xFF
    val green: Int get() = (value shr 8) and 0xFF
    val blue: Int get() = value and 0xFF

    fun inverted() = ColorRGB(value xor 0x00FFFFFF)

    fun blackOrWhite(): ColorRGB = (if (red + green + blue > 384) Color.WHITE else Color.BLACK).fromAwt()
}

fun Color.fromAwt() = ColorRGB(this.rgb)
