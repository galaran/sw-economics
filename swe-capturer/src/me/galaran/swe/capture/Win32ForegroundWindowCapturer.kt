package me.galaran.swe.capture

import com.sun.jna.Memory
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.*
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt

data class Win32Window(val id: Long, val title: String, val image: BufferedImage)

object Win32ForegroundWindowCapturer {

    /** @return null, if there is no foreground window */
    fun capture(): Win32Window? {
        val windowHandle: WinDef.HWND? = User32.INSTANCE.GetForegroundWindow()
        if (windowHandle == null || windowHandle.pointer == null) return null

        val title = User32.INSTANCE.GetWindowTextLength(windowHandle).let {
            val chars = CharArray(it)
            User32.INSTANCE.GetWindowText(windowHandle, chars, it + 1)
            String(chars)
        }

        val windowBounds = WinDef.RECT()
        User32.INSTANCE.GetClientRect(windowHandle, windowBounds)
        val width = windowBounds.right - windowBounds.left
        val height = windowBounds.bottom - windowBounds.top
        if (width <= 0 || height <= 0) return null

        val windowDC: WinDef.HDC = User32.INSTANCE.GetDC(windowHandle) ?: return null
        val bitmap: WinDef.HBITMAP = GDI32.INSTANCE.CreateCompatibleBitmap(windowDC, width, height)

        GDI32.INSTANCE.CreateCompatibleDC(windowDC).let { memoryDC ->
            val prevMemoryDCObject: WinNT.HANDLE = GDI32.INSTANCE.SelectObject(memoryDC, bitmap)
            GDI32.INSTANCE.BitBlt(memoryDC, 0, 0, width, height, windowDC, 0, 0, WINGDI_SRCCOPY)
            GDI32.INSTANCE.SelectObject(memoryDC, prevMemoryDCObject)
            GDI32.INSTANCE.DeleteDC(memoryDC)
        }

        val bitmapInfo = WinGDI.BITMAPINFO().apply {
            bmiHeader.biWidth = width
            bmiHeader.biHeight = -height
            bmiHeader.biPlanes = 1
            bmiHeader.biBitCount = 32
            bmiHeader.biCompression = WinGDI.BI_RGB
        }
        val buffer = Memory((width * height * 4).toLong())
        GDI32.INSTANCE.GetDIBits(windowDC, bitmap, 0, height, buffer, bitmapInfo, WinGDI.DIB_RGB_COLORS)

        val resultImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        buffer.getIntArray(0, width * height).copyInto((resultImage.raster.dataBuffer as DataBufferInt).data)

        GDI32.INSTANCE.DeleteObject(bitmap)
        User32.INSTANCE.ReleaseDC(windowHandle, windowDC)

        return Win32Window(Pointer.nativeValue(windowHandle.pointer), title, resultImage)
    }
}

private const val WINGDI_SRCCOPY = 0x00CC0020
