package com.jcrobsoft.geminitest.video.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jcrobsoft.geminitest.video.domain.VideoAnalyser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class VideoViewModel(
    private val videoAnalyser: VideoAnalyser
) : ViewModel() {

    private val _uiState: MutableStateFlow<VideoUiState> = MutableStateFlow(VideoUiState())
    val uiState: StateFlow<VideoUiState> = _uiState.asStateFlow()

    fun onFrameChange(frame: Bitmap) {
        _uiState.value = _uiState.value.copy(currentFrame = frame)
    }

    fun onPromptChange(prompt: String) {
        _uiState.value = _uiState.value.copy(prompt = prompt)
    }

    fun onDismissResponse() {
        _uiState.value = _uiState.value.copy(response = "")
    }

    fun onClickSend() {
        viewModelScope.launch(Dispatchers.IO) {
            uiState.value.currentFrame?.let { frame ->
                _uiState.value = _uiState.value.copy(isLoading = true)
                val stream = ByteArrayOutputStream()
                frame.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val response = videoAnalyser.analyze(stream.toByteArray(), uiState.value.prompt)
                _uiState.value = _uiState.value.copy(isLoading = false, response = response)
            }
        }
    }


}