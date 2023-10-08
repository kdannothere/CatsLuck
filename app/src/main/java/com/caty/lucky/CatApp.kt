package com.caty.lucky

import android.app.Application
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class CatApp: Application() {

    companion object {
        val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
        val dispatcherMain: CoroutineDispatcher = Dispatchers.Main
    }
}