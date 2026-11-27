package com.example.hiltnotes.logging

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Question 11: Logger interface with two concrete implementations bound via @Binds.
 */
interface Logger {
    fun log(tag: String, message: String)
}

@Singleton
class LogcatLogger @Inject constructor() : Logger {
    override fun log(tag: String, message: String) {
        Log.d(tag, message)
        println("[Logcat][$tag] $message")
    }
}

@Singleton
class FileLogger @Inject constructor(
    @ApplicationContext private val context: Context
) : Logger {
    override fun log(tag: String, message: String) {
        val logFile = File(context.filesDir, "app.log")
        logFile.appendText("[$tag] $message\n")
        println("[File][$tag] $message")
    }
}
