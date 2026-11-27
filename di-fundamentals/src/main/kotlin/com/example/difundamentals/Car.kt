package com.example.difundamentals

/**
 * Abstraction representing an Engine that can be started.
 */
interface Engine {
    fun start(): Boolean
}

/**
 * Real implementation of Engine.
 */
class PetrolEngine : Engine {
    override fun start(): Boolean = true
}

/**
 * Tightly-coupled Car that instantiates its own PetrolEngine internally.
 * Impossible to substitute the engine in unit tests without reflection or mocking frameworks.
 */
class HardCar {
    private val engine = PetrolEngine()

    fun drive(): String {
        return if (engine.start()) "Driving" else "Cannot start: engine broken"
    }
}

/**
 * Loosely-coupled Car that receives its Engine dependency through its constructor.
 */
class Car(private val engine: Engine) {
    fun drive(): String {
        return if (engine.start()) "Driving" else "Cannot start: engine broken"
    }
}
