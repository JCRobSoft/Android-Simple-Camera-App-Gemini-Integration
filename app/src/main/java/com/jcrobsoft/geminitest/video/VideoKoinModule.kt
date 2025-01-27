package com.jcrobsoft.geminitest.video

import com.google.ai.client.generativeai.GenerativeModel
import com.jcrobsoft.geminitest.BuildConfig
import com.jcrobsoft.geminitest.video.data.GeminiVideoAnalyser
import com.jcrobsoft.geminitest.video.domain.VideoAnalyser
import com.jcrobsoft.geminitest.video.ui.VideoViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val videoKoinModule = module {
    viewModelOf(::VideoViewModel)
    singleOf(::GeminiVideoAnalyser) bind VideoAnalyser::class
    single {
        GenerativeModel(
            modelName = "gemini-1.5-flash", apiKey = BuildConfig.apiKey
        )
    }
}