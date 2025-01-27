package com.jcrobsoft.geminitest.video.domain

interface VideoAnalyser {
    suspend fun analyze(image: ByteArray, prompt: String): String
}