package me.galaran.swe.capture.image

import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.absoluteValue
import kotlin.math.max

fun Color.maxRGBDifference(other: ColorRGB): Int {
    val rDiff = (red - other.red).absoluteValue
    val gDiff = (green - other.green).absoluteValue
    val bDiff = (blue - other.blue).absoluteValue
    return max(rDiff, max(gDiff, bDiff))
}

fun BufferedImage.filterColors(vararg filterFuns: (ColorRGB) -> ColorRGB): BufferedImage {
    for (y in 0 until height) {
        for (x in 0 until width) {
            val before = ColorRGB(getRGB(x, y))
            var after = before
            for (filterFun in filterFuns) {
                after = filterFun.invoke(after)
            }
            setRGB(x, y, after.value)
        }
    }
    return this
}
