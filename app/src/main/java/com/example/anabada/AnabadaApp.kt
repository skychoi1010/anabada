package com.example.anabada

import android.app.Application
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class AnabadaApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@AnabadaApp)
            modules(appModule)
        }
    }
}

val appModule = module {

    single { ApiService.create(androidContext()) }

    viewModel {
        PostViewModel()
    }
}
