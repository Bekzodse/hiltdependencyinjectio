package com.example.hiltnotes.experiments

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ViewModelScoped
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Question 16: Four counter classes comparing unscoped, @Singleton,
 * @ActivityRetainedScoped, and @ViewModelScoped lifetimes.
 */
class UnscopedCounter @Inject constructor() {
    val instanceId: Int = count.incrementAndGet()
    init { println("ScopeExperiment: UnscopedCounter #$instanceId") }
    companion object { val count = AtomicInteger(0) }
}

@Singleton
class SingletonCounter @Inject constructor() {
    val instanceId: Int = count.incrementAndGet()
    init { println("ScopeExperiment: SingletonCounter #$instanceId") }
    companion object { val count = AtomicInteger(0) }
}

@ActivityRetainedScoped
class ActivityRetainedCounter @Inject constructor() {
    val instanceId: Int = count.incrementAndGet()
    init { println("ScopeExperiment: ActivityRetainedCounter #$instanceId") }
    companion object { val count = AtomicInteger(0) }
}

@ViewModelScoped
class ViewModelScopedCounter @Inject constructor() {
    val instanceId: Int = count.incrementAndGet()
    init { println("ScopeExperiment: ViewModelScopedCounter #$instanceId") }
    companion object { val count = AtomicInteger(0) }
}

@HiltViewModel
class ScopeExperimentViewModel @Inject constructor(
    val unscoped: UnscopedCounter,
    val singleton: SingletonCounter,
    val activityRetained: ActivityRetainedCounter,
    val viewModelScoped: ViewModelScopedCounter
) : ViewModel()

/**
 * Second activity used in Question 16 alongside MainActivity to compare component scopes.
 * NOTE: Injecting ViewModelScopedCounter directly into an Activity fails at compile time
 * because ActivityComponent is NOT a child of ViewModelComponent!
 */
@AndroidEntryPoint
class ScopeExperimentActivity : ComponentActivity() {
    @Inject lateinit var unscoped: UnscopedCounter
    @Inject lateinit var singleton: SingletonCounter
    @Inject lateinit var activityRetained: ActivityRetainedCounter
    // @Inject lateinit var vmScoped: ViewModelScopedCounter // COMPILE ERROR: incompatible scope!

    private val viewModel: ScopeExperimentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println(
            "ScopeExperimentActivity: unscoped=#${unscoped.instanceId}, " +
                "singleton=#${singleton.instanceId}, " +
                "retained=#${activityRetained.instanceId}, " +
                "vm.unscoped=#${viewModel.unscoped.instanceId}, " +
                "vm.singleton=#${viewModel.singleton.instanceId}, " +
                "vm.retained=#${viewModel.activityRetained.instanceId}, " +
                "vm.vmScoped=#${viewModel.viewModelScoped.instanceId}"
        )
    }
}
