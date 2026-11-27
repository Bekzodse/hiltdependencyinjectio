package com.example.hiltnotes.experiments

import javax.inject.Inject

/**
 * Question 9: Constructor injection chain A -> B -> C verifying creation order (C, B, A)
 * and unscoped behavior when injected twice.
 */
class C @Inject constructor() {
    init {
        println("ChainExperiment: created C (${System.identityHashCode(this)})")
    }
}

class B @Inject constructor(val c: C) {
    init {
        println("ChainExperiment: created B (${System.identityHashCode(this)})")
    }
}

class A @Inject constructor(val b: B) {
    init {
        println("ChainExperiment: created A (${System.identityHashCode(this)})")
    }
}
