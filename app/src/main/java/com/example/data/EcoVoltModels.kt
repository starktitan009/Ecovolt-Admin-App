package com.example.data

enum class NavTab(val label: String) {
    HOME("Home"),
    RECYCLE("Recycle"),
    REWARDS("Rewards"),
    CHARGE("Charge"),
    EXPLORE("Explore")
}

enum class ExploreSubTab(val label: String) {
    KIOSKS("Kiosks"),
    IMPACT("Impact"),
    LEADERBOARD("Leaderboard"),
    SOLAR("Solar"),
    ADMIN("Admin")
}

enum class RecyclableMaterial(
    val displayName: String,
    val points: Int,
    val weightKg: Double,
    val co2FactorKg: Double
) {
    PLASTIC_BOTTLE("Plastic bottle", 20, 0.05, 0.15),
    ALUMINIUM_CAN("Aluminium can", 15, 0.03, 0.25),
    PAPER_BUNDLE("Paper bundle", 10, 0.10, 0.09)
}

enum class TransactionType(val label: String) {
    RECYCLE("Recycling Deposit"),
    COUPON("Coupon Redemption"),
    CHARGING("Mobile Charging")
}

data class EcoTransaction(
    val id: String,
    val timestampLabel: String,
    val type: TransactionType,
    val description: String,
    val pointsDelta: Int,
    val status: String = "Completed (Demo)"
)

data class CouponOffer(
    val id: String,
    val tag: String,
    val discount: String,
    val partner: String,
    val description: String,
    val codePrefix: String,
    val costPoints: Int
)

data class RedeemedCoupon(
    val id: String,
    val partner: String,
    val discount: String,
    val code: String,
    val redeemedAt: String
)

data class ChargingPort(
    val id: String,
    val name: String,
    val connectorType: String,
    val isAvailable: Boolean = true
)

data class ChargingPackage(
    val id: String,
    val label: String,
    val durationMinutes: Int,
    val pointsCost: Int,
    val demoPriceInr: Int
)

data class ActiveChargingSession(
    val sessionId: String,
    val portId: String,
    val portName: String,
    val packageLabel: String,
    val totalSeconds: Int,
    val remainingSeconds: Int,
    val paymentMethod: String
)

data class KioskLocation(
    val id: String,
    val name: String,
    val location: String,
    val isOnline: Boolean,
    val availablePorts: Int,
    val totalPorts: Int,
    val acceptedMaterials: List<String>,
    val distanceMeters: Int,
    val mapXFraction: Float,
    val mapYFraction: Float,
    val batteryPercent: Int,
    val solarWatts: Int
)

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val userId: String,
    val weeklyPoints: Int,
    val monthlyPoints: Int,
    val itemsRecycled: Int,
    val isCurrentUser: Boolean = false
)

data class AchievementBadge(
    val id: String,
    val title: String,
    val description: String,
    val unlocked: Boolean,
    val progressLabel: String
)

data class SolarTelemetry(
    val solarPowerWatts: Int,
    val dailyEnergyKwh: Double,
    val batteryPercent: Int,
    val batteryVoltage: Double,
    val chargingConsumedWh: Int,
    val availableStoredWh: Int,
    val kioskStatus: String,
    val hourlyGenerationWatts: List<Int>,
    val hourlyConsumptionWatts: List<Int>
)

data class EcoVoltState(
    val userId: String = "EV-USER-0248",
    val ecoPoints: Int = 250,
    val pointsEarnedTotal: Int = 350,
    val pointsRedeemedTotal: Int = 100,
    val itemsRecycled: Int = 12,
    val rewardsClaimed: Int = 2,
    val greenImpactKg: Double = 1.8,
    val plasticCount: Int = 6,
    val aluminiumCount: Int = 4,
    val paperCount: Int = 2,
    val chargingSessionsCount: Int = 1,
    val selectedKioskId: String = "EV-KIOSK-001",
    val transactions: List<EcoTransaction> = defaultTransactions(),
    val redeemedCoupons: List<RedeemedCoupon> = emptyList(),
    val processedDepositIds: Set<String> = emptySet(),
    val activeCharging: ActiveChargingSession? = null
)

fun defaultTransactions(): List<EcoTransaction> = listOf(
    EcoTransaction(
        id = "TXN-DEMO-103",
        timestampLabel = "Today, 09:40 AM",
        type = TransactionType.RECYCLE,
        description = "Plastic bottle deposit · EV-KIOSK-001",
        pointsDelta = 20
    ),
    EcoTransaction(
        id = "TXN-DEMO-102",
        timestampLabel = "Yesterday, 04:15 PM",
        type = TransactionType.COUPON,
        description = "Campus Café 10% OFF redeemed",
        pointsDelta = -100
    ),
    EcoTransaction(
        id = "TXN-DEMO-101",
        timestampLabel = "2 days ago, 11:20 AM",
        type = TransactionType.RECYCLE,
        description = "Aluminium can deposit · EV-KIOSK-002",
        pointsDelta = 15
    )
)

object DemoCatalog {
    val coupons = listOf(
        CouponOffer(
            id = "cpn_cafe",
            tag = "POPULAR",
            discount = "10% OFF",
            partner = "Campus Café",
            description = "10% off one eligible café order (Demo Offer).",
            codePrefix = "ECOCAFE10",
            costPoints = 100
        ),
        CouponOffer(
            id = "cpn_store",
            tag = "STUDENT PICK",
            discount = "₹20 OFF",
            partner = "Campus Store",
            description = "Save ₹20 on a sample stationery purchase (Demo Offer).",
            codePrefix = "GREEN20",
            costPoints = 150
        ),
        CouponOffer(
            id = "cpn_refill",
            tag = "GREEN REWARD",
            discount = "FREE DRINK",
            partner = "Refill Station",
            description = "A sample reward for reusable habits (Demo Offer).",
            codePrefix = "REFILLFREE",
            costPoints = 200
        )
    )

    val chargingPorts = listOf(
        ChargingPort("PORT-A", "Port A", "USB-C", isAvailable = true),
        ChargingPort("PORT-B", "Port B", "USB-A", isAvailable = true),
        ChargingPort("PORT-C", "Port C", "USB-C", isAvailable = true)
    )

    val chargingPackages = listOf(
        ChargingPackage("pkg_15", "15 min Quick Boost", 15, 30, 10),
        ChargingPackage("pkg_30", "30 min Standard", 30, 50, 20),
        ChargingPackage("pkg_60", "60 min Full Charge", 60, 90, 35)
    )

    val kiosks = listOf(
        KioskLocation(
            id = "EV-KIOSK-001",
            name = "Main Library Plaza",
            location = "Central Quad · Near Entrance",
            isOnline = true,
            availablePorts = 3,
            totalPorts = 3,
            acceptedMaterials = listOf("Plastic", "Aluminium", "Paper"),
            distanceMeters = 85,
            mapXFraction = 0.35f,
            mapYFraction = 0.42f,
            batteryPercent = 84,
            solarWatts = 142
        ),
        KioskLocation(
            id = "EV-KIOSK-002",
            name = "Student Union Hub",
            location = "Food Court West Wing",
            isOnline = true,
            availablePorts = 2,
            totalPorts = 3,
            acceptedMaterials = listOf("Plastic", "Aluminium"),
            distanceMeters = 210,
            mapXFraction = 0.68f,
            mapYFraction = 0.28f,
            batteryPercent = 76,
            solarWatts = 128
        ),
        KioskLocation(
            id = "EV-KIOSK-003",
            name = "Engineering Block B",
            location = "Atrium Ground Floor",
            isOnline = true,
            availablePorts = 3,
            totalPorts = 3,
            acceptedMaterials = listOf("Plastic", "Aluminium", "Paper"),
            distanceMeters = 340,
            mapXFraction = 0.24f,
            mapYFraction = 0.74f,
            batteryPercent = 91,
            solarWatts = 156
        ),
        KioskLocation(
            id = "EV-KIOSK-004",
            name = "Sports Complex",
            location = "Gymnasium Lobby",
            isOnline = false,
            availablePorts = 0,
            totalPorts = 2,
            acceptedMaterials = listOf("Plastic", "Aluminium"),
            distanceMeters = 520,
            mapXFraction = 0.76f,
            mapYFraction = 0.72f,
            batteryPercent = 28,
            solarWatts = 18
        )
    )
}
