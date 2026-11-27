package com.example.hiltnotes

import android.app.Application
import com.example.hiltnotes.di.AppContainer

class NotesApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(applicationContext)
    }
}
