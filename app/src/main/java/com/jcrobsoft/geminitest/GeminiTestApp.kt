package com.jcrobsoft.geminitest

import android.app.Application
import com.jcrobsoft.geminitest.video.videoKoinModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class GeminiTestApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@GeminiTestApp)
            modules(videoKoinModule)
        }
    }

}