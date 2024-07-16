package id.yuana.passkeys.playground

import android.app.Application
import id.yuana.passkeys.playground.di.appModule
import id.yuana.passkeys.playground.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(dataModule, appModule)
        }
    }
}