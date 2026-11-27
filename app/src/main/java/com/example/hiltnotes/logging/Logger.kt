package com.example.hiltnotes.logging

import android.util.Log
import javax.inject.Inject

/**
 * Initial concrete Logger with constructor injection (Question 8).
 * Evolved into interface Logger + @Binds LoggingModule in Question 11.
 */
class Logger @Inject constructor() {
    fun log(tag: String, message: String) {
        Log.d(tag, message)
        println("[$tag] $message")
    }
}
