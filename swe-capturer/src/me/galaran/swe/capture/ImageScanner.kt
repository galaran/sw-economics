package me.galaran.swe.capture

import java.awt.image.BufferedImage

interface ImageScanner<T> {

    fun findAt(target: BufferedImage, walker: ImageWalker): List<T>
}
