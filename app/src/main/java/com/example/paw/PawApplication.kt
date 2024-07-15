package com.example.paw

import android.app.Application
import com.example.paw.data.repository.PawApiRepository
import com.example.paw.db.FavoriteDogDatabaseProvider
import com.example.paw.utils.SharedPreferenceManager
import com.example.paw.viewmodel.MainActivityViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class PawApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        PawApiRepository.initializeApi()
        startKoin {
            androidContext(this@PawApplication)
            modules(appModule)
        }
    }
}

val appModule = module {
    single {
        FavoriteDogDatabaseProvider(get()).getFavoriteDogDao()
    }
    single { SharedPreferenceManager() }
    viewModel { MainActivityViewModel(get()) }
}