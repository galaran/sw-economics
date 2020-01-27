package me.galaran.swe.capture.image

import java.awt.image.BufferedImage

fun BufferedImage.getColor(x: Int, y: Int): ColorRGB = ColorRGB(getRGB(x, y))

fun BufferedImage.filterColors(vararg filterFuns: (ColorRGB) -> ColorRGB): BufferedImage {
    for (y in 0 until height) {
        for (x in 0 until width) {
            val before = getColor(x, y)
            var after = before
            for (filterFun in filterFuns) {
                after = filterFun.invoke(after)
            }
            setRGB(x, y, after.value)
        }
    }
    return this
}
