package me.galaran.swe.capture.image

import java.awt.image.BufferedImage

interface ImageMatcher {

    fun isAtImage(testImage: BufferedImage, zeroPointAtTestImage: Point,
                  showDismatches: Boolean, dismatchesDisplayOffset: Point = Point.ZERO): Boolean
}
