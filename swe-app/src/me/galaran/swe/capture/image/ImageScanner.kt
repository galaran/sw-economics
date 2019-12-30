package me.galaran.swe.capture.image

import java.awt.image.BufferedImage

interface ImageScanner<T> {

    fun findAt(target: BufferedImage, walker: ImageWalker = EntireImageWalker(target.size)): List<T>
}
