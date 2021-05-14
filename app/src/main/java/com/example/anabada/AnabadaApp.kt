package com.example.anabada

import android.app.Application
import com.example.anabada.db.AnabadaDatabase
import com.example.anabada.db.BoardsDataDao
import com.example.anabada.network.ApiService
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class AnabadaApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@AnabadaApp)
            modules(
                    networkModule,
                    databaseModule
            )
        }
    }
}

val networkModule = module {
    single { ApiService.create(androidContext()) }
    single { ApiService.createImg() }
}

val databaseModule = module {
    single { AnabadaDatabase.create(androidContext()) }
}