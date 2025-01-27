package com.jcrobsoft.geminitest.video.ui

import android.graphics.Bitmap

data class VideoUiState(
    val isLoading: Boolean = false,
    val response: String = "",
    val currentFrame: Bitmap? = null,
    val prompt: String = "",
)