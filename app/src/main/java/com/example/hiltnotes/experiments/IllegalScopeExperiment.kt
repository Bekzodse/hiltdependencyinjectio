package com.example.hiltnotes.experiments

import android.content.Context
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Question 18: Demonstrates why Hilt rejects illegal scope hierarchies at compile time.
 *
 * Uncommenting either illegal class below triggers a Dagger compile-time error
 * preventing an Activity memory leak (full errors recorded in NOTES.md).
 */
@ActivityScoped
class ActivityOnlyHelper @Inject constructor(
    @ActivityContext private val activityContext: Context
) {
    fun contextName(): String = activityContext.javaClass.simpleName
}

/*
// ILLEGAL CASE 1 — [Dagger/IncompatiblyScopedBindings]:
// @Singleton lives for the whole app process; injecting @ActivityScoped would trap a
// destroyed Activity graph inside the SingletonComponent forever.
@Singleton
class IllegalSingletonHoldingActivityScoped @Inject constructor(
    private val helper: ActivityOnlyHelper
)

// ILLEGAL CASE 2 — [Dagger/MissingBinding] @ActivityContext Context in SingletonComponent:
// @ActivityContext only exists in ActivityComponent and its children, never in SingletonComponent.
@Singleton
class IllegalSingletonHoldingActivityContext @Inject constructor(
    @ActivityContext private val context: Context
)
*/
