package com.example.anabada

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.example.anabada.repository.local.AnabadaDatabase
import com.example.anabada.network.ApiService
import com.example.anabada.repository.BoardsDataPagingSource
import com.example.anabada.repository.BoardsDataRepoImpl
import com.example.anabada.repository.BoardsRemoteMediator
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
                databaseModule,
                repositoryModule
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

val repositoryModule = module {
    factory { ViewModelProvider.AndroidViewModelFactory(get()) }
    factory { BoardsDataRepoImpl(get(), get(), get()) }
    factory { BoardsRemoteMediator(get(), get()) }
    factory { BoardsDataPagingSource(get()) }
}