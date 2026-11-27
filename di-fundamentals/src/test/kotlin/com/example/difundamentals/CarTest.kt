package com.example.difundamentals

import org.junit.Assert.assertEquals
import org.junit.Test

class FakeEngine(private val working: Boolean) : Engine {
    var startCalls = 0
        private set

    override fun start(): Boolean {
        startCalls++
        return working
    }
}

class CarTest {

    @Test
    fun hardCar_canOnlyTestHappyPath_cannotSimulateBrokenEngine() {
        val hardCar = HardCar()
        // Because HardCar constructs PetrolEngine() directly inside the class,
        // we can only observe the default working behavior ("Driving").
        // There is no seam to inject a failing engine and verify the broken-engine branch.
        assertEquals("Driving", hardCar.drive())
    }

    @Test
    fun carWithDi_returnsErrorWhenEngineIsBroken() {
        val brokenEngine = FakeEngine(working = false)
        val car = Car(brokenEngine)

        assertEquals("Cannot start: engine broken", car.drive())
        assertEquals(1, brokenEngine.startCalls)
    }

    @Test
    fun carWithDi_drivesWhenEngineWorks() {
        val workingEngine = FakeEngine(working = true)
        val car = Car(workingEngine)

        assertEquals("Driving", car.drive())
        assertEquals(1, workingEngine.startCalls)
    }
}
