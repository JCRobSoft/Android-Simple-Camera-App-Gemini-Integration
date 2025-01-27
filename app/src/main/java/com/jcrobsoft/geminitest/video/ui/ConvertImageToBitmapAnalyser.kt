package com.jcrobsoft.geminitest.video.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

class ConvertImageToBitmapAnalyser(
    private val analyze: (Bitmap) -> Unit
) : ImageAnalysis.Analyzer {
    override fun analyze(image: ImageProxy) {
        val yBuffer: ByteBuffer = image.planes[0].buffer //y
        val vuBuffer: ByteBuffer = image.planes[2].buffer //vu
        val ySize: Int = yBuffer.remaining()
        val vuSize: Int = vuBuffer.remaining()
        val nv21 = ByteArray(ySize + vuSize)
        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val outStream = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, outStream)
        val imageBytes: ByteArray = outStream.toByteArray()
        val bitmapImage: Bitmap =
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, null)
        analyze(bitmapImage)
    }
}