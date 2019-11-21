package me.galaran.swe.ocr

import net.sourceforge.tess4j.ITessAPI
import net.sourceforge.tess4j.ITesseract
import net.sourceforge.tess4j.Tesseract
import java.io.File
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO


object OcrTest {

    @JvmStatic
    fun main(args: Array<String>) {
        val tes: ITesseract = Tesseract()
        tes.setDatapath("C:\\dev\\SwEconomics\\swe-ocr\\tessdata")
        tes.setLanguage("eng")
        tes.setOcrEngineMode(ITessAPI.TessOcrEngineMode.OEM_LSTM_ONLY)

//        val imageFile = File("c:\\Users\\Galaran\\Desktop\\andrey.png")
        val imageFile = File("c:\\Users\\Galaran\\Desktop\\Shot.jpg")
        val image = ImageIO.read(imageFile)

        repeat(10) {
            val start = System.nanoTime()
            val result = tes.doOCR(image)
            val end = System.nanoTime()
            println("Done @ ${TimeUnit.NANOSECONDS.toMillis(end - start)}ms")
            println("==========================")
            println(result)
        }
    }
}
