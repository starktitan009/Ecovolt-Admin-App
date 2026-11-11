package com.example.data

import kotlinx.coroutines.delay

/**
 * Hardware abstraction layer for future ESP32 + FastAPI backend integration:
 * Mobile App -> Backend -> Kiosk Controller (ESP32) -> Hardware
 * Sensors -> ESP32 -> Backend -> Application Dashboard
 */
interface KioskHardwareService {
    suspend fun verifyDeposit(kioskId: String, material: RecyclableMaterial): Boolean
    suspend fun startPortPower(kioskId: String, portId: String, durationMinutes: Int): Boolean
    suspend fun stopPortPower(kioskId: String, portId: String): Boolean
    fun getSolarTelemetry(kioskId: String): SolarTelemetry
}

class SimulatedKioskHardwareService : KioskHardwareService {
    override suspend fun verifyDeposit(kioskId: String, material: RecyclableMaterial): Boolean {
        delay(450)
        return true
    }

    override suspend fun startPortPower(kioskId: String, portId: String, durationMinutes: Int): Boolean {
        delay(250)
        return true
    }

    override suspend fun stopPortPower(kioskId: String, portId: String): Boolean {
        delay(150)
        return true
    }

    override fun getSolarTelemetry(kioskId: String): SolarTelemetry {
        return SolarTelemetry(
            solarPowerWatts = 142,
            dailyEnergyKwh = 1.24,
            batteryPercent = 84,
            batteryVoltage = 13.4,
            chargingConsumedWh = 380,
            availableStoredWh = 1008,
            kioskStatus = "Online · Solar Charging (DEMO)",
            hourlyGenerationWatts = listOf(15, 48, 95, 138, 165, 152, 142, 110, 62, 20),
            hourlyConsumptionWatts = listOf(22, 30, 45, 68, 54, 72, 60, 48, 35, 25)
        )
    }
}
