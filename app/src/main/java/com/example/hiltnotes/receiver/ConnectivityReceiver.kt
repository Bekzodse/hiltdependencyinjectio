package com.example.hiltnotes.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.CallSuper
import com.example.hiltnotes.logging.Logger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Intermediate base class allowing Kotlin subclasses to invoke super.onReceive(context, intent)
 * cleanly (since BroadcastReceiver.onReceive is abstract in Android SDK).
 * Hilt rewrites ConnectivityReceiver's superclass to Hilt_ConnectivityReceiver which overrides
 * onReceive() to run inject(context) before returning control here.
 */
abstract class HiltBroadcastReceiver : BroadcastReceiver() {
    @CallSuper
    override fun onReceive(context: Context, intent: Intent) {
        // Hilt injects fields inside generated Hilt_ConnectivityReceiver.onReceive(...)
    }
}

/**
 * Question 24: BroadcastReceiver with @AndroidEntryPoint.
 * Calling super.onReceive(context, intent) FIRST is mandatory so Hilt populates `logger`.
 */
@AndroidEntryPoint
class ConnectivityReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var logger: Logger

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        logger.log(
            "ConnectivityReceiver",
            "Received broadcast action=${intent.action}"
        )
    }
}
