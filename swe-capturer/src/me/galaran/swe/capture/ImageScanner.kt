package me.galaran.swe.capture

import java.awt.image.BufferedImage

interface ImageScanner<T> {

    fun scan(image: BufferedImage, walker: ImageWalker): List<T>
}
