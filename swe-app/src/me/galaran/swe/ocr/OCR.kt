package me.galaran.swe.ocr

import me.galaran.swe.capture.image.ColorRGB
import me.galaran.swe.capture.image.copy
import me.galaran.swe.capture.image.filterColors
import me.galaran.swe.capture.image.scaledCopy
import me.galaran.swe.util.runWithStopwatch
import net.sourceforge.tess4j.ITessAPI.TessOcrEngineMode
import net.sourceforge.tess4j.ITesseract
import net.sourceforge.tess4j.Tesseract
import java.awt.image.BufferedImage

object OCR {

    private val engine: ITesseract = Tesseract().apply {
        setDatapath("swe-app/tessdata")
        setOcrEngineMode(TessOcrEngineMode.OEM_LSTM_ONLY)
        setLanguage("eng+rus")
        setPageSegMode(7) // Treat the image as a single text line
    }

    fun ocrTitle(titleImage: BufferedImage): String {
        val inverted = titleImage.scaledCopy(4).filterColors(ColorRGB::inverted)

        val monochrome = inverted.copy().filterColors(ColorRGB::blackOrWhite)

        return runWithStopwatch {
            engine.doOCR(monochrome)
        }
    }
}
