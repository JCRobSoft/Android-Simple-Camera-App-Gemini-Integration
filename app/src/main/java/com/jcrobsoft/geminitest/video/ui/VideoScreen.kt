package com.jcrobsoft.geminitest.video.ui

import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jcrobsoft.geminitest.R
import com.jcrobsoft.geminitest.core.ui.AnimatedBorderCard
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.Executors


@Composable
fun VideoRoute(viewModel: VideoViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    VideoScreen(
        modifier = Modifier.fillMaxSize(),
        prompt = uiState.prompt,
        response = uiState.response,
        isLoading = uiState.isLoading,
        onPromptChange = viewModel::onPromptChange,
        onFrameChange = viewModel::onFrameChange,
        onClickSend = viewModel::onClickSend,
        onDismissResponse = viewModel::onDismissResponse
    )
}

@Composable
fun VideoScreen(
    modifier: Modifier = Modifier,
    prompt: String,
    response: String,
    isLoading: Boolean,
    onPromptChange: (String) -> Unit,
    onFrameChange: (Bitmap) -> Unit,
    onClickSend: () -> Unit,
    onDismissResponse: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .padding(all = 16.dp)
                .weight(0.8f)
        ) {
            AnimatedBorderCard(
                shape = RoundedCornerShape(size = 5.dp),
                borderWidth = if (isLoading) 3.dp else 0.dp,
                gradient = Brush.sweepGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary
                    )
                ),
            ) {
                CameraScreen(
                    modifier = Modifier.fillMaxSize(), onFrameChange = onFrameChange
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(all = 16.dp)
                .weight(0.2f)
        ) {
            OutlinedTextField(
                value = prompt,
                label = { Text(stringResource(R.string.label_prompt)) },
                enabled = !isLoading,
                onValueChange = onPromptChange,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier
                    .weight(0.8f)
                    .padding(end = 16.dp)
                    .align(Alignment.CenterVertically)
            )

            Button(
                onClick = onClickSend,
                enabled = prompt.isNotEmpty() && !isLoading,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(text = stringResource(R.string.action_go))
            }
        }
    }
    if (response.isNotEmpty()) {
        AlertDialog(
            title = {
                Text(text = stringResource(R.string.title_response))
            },
            text = {
                Text(modifier = Modifier.verticalScroll(rememberScrollState()), text = response)
            },
            onDismissRequest = onDismissResponse,
            confirmButton = {
                TextButton(
                    onClick = onDismissResponse
                ) {
                    Text(stringResource(R.string.action_close))
                }
            },
        )
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier, onFrameChange: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    val previewView: PreviewView = remember { PreviewView(context) }
    val cameraController = remember { LifecycleCameraController(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    cameraController.bindToLifecycle(lifecycleOwner)
    cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    previewView.controller = cameraController

    val executor = remember { Executors.newSingleThreadExecutor() }

    Box(modifier = modifier) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        cameraController.setImageAnalysisAnalyzer(
            executor,
            ConvertImageToBitmapAnalyser(onFrameChange)
        )
    }
}

