package com.jcrobsoft.geminitest.video.data

import android.graphics.BitmapFactory
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.jcrobsoft.geminitest.video.domain.VideoAnalyser


class GeminiVideoAnalyser(
    private val generativeModel: GenerativeModel
) : VideoAnalyser {

    override suspend fun analyze(image: ByteArray, prompt: String): String {
        val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
        return generativeModel.generateContent(content {
            image(bitmap)
            text(prompt)
        }).text ?: "<no response>"
    }
}