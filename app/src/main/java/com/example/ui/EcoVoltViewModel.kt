package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class EcoVoltViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = EcoVoltRepository(application.applicationContext)
    private val hardwareService: KioskHardwareService = SimulatedKioskHardwareService()

    private val _state = MutableStateFlow(repository.loadState())
    val state: StateFlow<EcoVoltState> = _state.asStateFlow()

    private val _isVerifyingDeposit = MutableStateFlow(false)
    val isVerifyingDeposit: StateFlow<Boolean> = _isVerifyingDeposit.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val toastMessage = _toastMessage.asSharedFlow()

    private var chargingTimerJob: Job? = null

    fun selectKiosk(kioskId: String) {
        _state.update { it.copy(selectedKioskId = kioskId) }
        repository.saveState(_state.value)
    }

    fun depositRecyclable(material: RecyclableMaterial, depositToken: String) {
        if (_isVerifyingDeposit.value) return
        if (_state.value.processedDepositIds.contains(depositToken)) {
            _toastMessage.tryEmit("Duplicate deposit prevented for this transaction.")
            return
        }

        viewModelScope.launch {
            _isVerifyingDeposit.value = true
            val verified = hardwareService.verifyDeposit(_state.value.selectedKioskId, material)
            _isVerifyingDeposit.value = false

            if (!verified) {
                _toastMessage.tryEmit("Simulated verification failed. Please retry.")
                return@launch
            }

            _state.update { current ->
                val newTxn = EcoTransaction(
                    id = depositToken,
                    timestampLabel = currentTimestamp(),
                    type = TransactionType.RECYCLE,
                    description = "${material.displayName} deposit · ${current.selectedKioskId}",
                    pointsDelta = material.points,
                    status = "Verified (Demo)"
                )
                current.copy(
                    ecoPoints = current.ecoPoints + material.points,
                    pointsEarnedTotal = current.pointsEarnedTotal + material.points,
                    itemsRecycled = current.itemsRecycled + 1,
                    greenImpactKg = current.greenImpactKg + material.weightKg,
                    plasticCount = current.plasticCount + if (material == RecyclableMaterial.PLASTIC_BOTTLE) 1 else 0,
                    aluminiumCount = current.aluminiumCount + if (material == RecyclableMaterial.ALUMINIUM_CAN) 1 else 0,
                    paperCount = current.paperCount + if (material == RecyclableMaterial.PAPER_BUNDLE) 1 else 0,
                    transactions = listOf(newTxn) + current.transactions,
                    processedDepositIds = current.processedDepositIds + depositToken
                )
            }
            repository.saveState(_state.value)
            _toastMessage.tryEmit("+${material.points} EcoPoints added (${material.displayName})!")
        }
    }

    fun redeemDepositQr(qrPayload: String) {
        if (_state.value.processedDepositIds.contains(qrPayload)) {
            _toastMessage.tryEmit("This QR code has already been credited.")
            return
        }

        // Parse demo QR payload: e.g. "ECOVOLT:3_BOTTLES:CASH_15:<token>"
        val bottlesCount = 3
        val pointsEarned = bottlesCount * RecyclableMaterial.PLASTIC_BOTTLE.points // 60 pts
        val weightKg = bottlesCount * RecyclableMaterial.PLASTIC_BOTTLE.weightKg // 0.15 kg
        val cashBonusInr = 15

        _state.update { current ->
            val newTxn = EcoTransaction(
                id = qrPayload,
                timestampLabel = currentTimestamp(),
                type = TransactionType.RECYCLE,
                description = "QR Deposit: 3 Plastic bottles + ₹$cashBonusInr Demo Cash · ${current.selectedKioskId}",
                pointsDelta = pointsEarned,
                status = "QR Verified (Demo)"
            )
            current.copy(
                ecoPoints = current.ecoPoints + pointsEarned,
                pointsEarnedTotal = current.pointsEarnedTotal + pointsEarned,
                itemsRecycled = current.itemsRecycled + bottlesCount,
                greenImpactKg = current.greenImpactKg + weightKg,
                plasticCount = current.plasticCount + bottlesCount,
                transactions = listOf(newTxn) + current.transactions,
                processedDepositIds = current.processedDepositIds + qrPayload
            )
        }
        repository.saveState(_state.value)
        _toastMessage.tryEmit("QR Recognized: 3 bottles (+$pointsEarned pts) & ₹$cashBonusInr Demo Cash!")
    }

    fun redeemCoupon(offer: CouponOffer) {
        val current = _state.value
        if (current.ecoPoints < offer.costPoints) {
            _toastMessage.tryEmit("Insufficient EcoPoints. Need ${offer.costPoints} pts.")
            return
        }

        val uniqueCode = "${offer.codePrefix}-${Random.nextInt(100, 999)}"
        val redeemed = RedeemedCoupon(
            id = "CPN-${System.currentTimeMillis()}",
            partner = offer.partner,
            discount = offer.discount,
            code = uniqueCode,
            redeemedAt = currentTimestamp()
        )
        val txn = EcoTransaction(
            id = "TXN-${Random.nextInt(1000, 9999)}",
            timestampLabel = currentTimestamp(),
            type = TransactionType.COUPON,
            description = "${offer.partner} (${offer.discount}) · Code $uniqueCode",
            pointsDelta = -offer.costPoints,
            status = "Redeemed (Demo)"
        )

        _state.update {
            it.copy(
                ecoPoints = (it.ecoPoints - offer.costPoints).coerceAtLeast(0),
                pointsRedeemedTotal = it.pointsRedeemedTotal + offer.costPoints,
                rewardsClaimed = it.rewardsClaimed + 1,
                redeemedCoupons = listOf(redeemed) + it.redeemedCoupons,
                transactions = listOf(txn) + it.transactions
            )
        }
        repository.saveState(_state.value)
        _toastMessage.tryEmit("Coupon redeemed! Code: $uniqueCode")
    }

    fun startChargingSession(
        port: ChargingPort,
        pkg: ChargingPackage,
        useEcoPoints: Boolean
    ) {
        val current = _state.value
        if (current.activeCharging != null) {
            _toastMessage.tryEmit("A demo charging session is already active.")
            return
        }
        if (useEcoPoints && current.ecoPoints < pkg.pointsCost) {
            _toastMessage.tryEmit("Not enough EcoPoints for ${pkg.label}.")
            return
        }

        viewModelScope.launch {
            hardwareService.startPortPower(current.selectedKioskId, port.id, pkg.durationMinutes)
            val totalSeconds = pkg.durationMinutes * 60
            val session = ActiveChargingSession(
                sessionId = "CHG-${Random.nextInt(1000, 9999)}",
                portId = port.id,
                portName = "${port.name} (${port.connectorType})",
                packageLabel = pkg.label,
                totalSeconds = totalSeconds,
                remainingSeconds = totalSeconds,
                paymentMethod = if (useEcoPoints) "${pkg.pointsCost} EcoPoints" else "₹${pkg.demoPriceInr} Demo Pay"
            )
            val pointsDelta = if (useEcoPoints) -pkg.pointsCost else 0
            val txn = EcoTransaction(
                id = session.sessionId,
                timestampLabel = currentTimestamp(),
                type = TransactionType.CHARGING,
                description = "${port.name} (${port.connectorType}) · ${pkg.label}",
                pointsDelta = pointsDelta,
                status = if (useEcoPoints) "Points Used (Demo)" else "Demo Payment ₹${pkg.demoPriceInr}"
            )

            _state.update {
                it.copy(
                    ecoPoints = (it.ecoPoints + pointsDelta).coerceAtLeast(0),
                    pointsRedeemedTotal = it.pointsRedeemedTotal + if (useEcoPoints) pkg.pointsCost else 0,
                    chargingSessionsCount = it.chargingSessionsCount + 1,
                    activeCharging = session,
                    transactions = listOf(txn) + it.transactions
                )
            }
            repository.saveState(_state.value)
            _toastMessage.tryEmit("Demo charging started on ${port.name}.")
            startCountdown()
        }
    }

    private fun startCountdown() {
        chargingTimerJob?.cancel()
        chargingTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val active = _state.value.activeCharging ?: break
                if (active.remainingSeconds <= 1) {
                    stopChargingSession(expired = true)
                    break
                } else {
                    _state.update {
                        it.copy(activeCharging = active.copy(remainingSeconds = active.remainingSeconds - 1))
                    }
                }
            }
        }
    }

    fun stopChargingSession(expired: Boolean = false) {
        chargingTimerJob?.cancel()
        val active = _state.value.activeCharging ?: return
        viewModelScope.launch {
            hardwareService.stopPortPower(_state.value.selectedKioskId, active.portId)
        }
        _state.update { it.copy(activeCharging = null) }
        repository.saveState(_state.value)
        _toastMessage.tryEmit(
            if (expired) "Demo charging session completed." else "Demo charging session ended."
        )
    }

    fun getSolarTelemetry(): SolarTelemetry {
        return hardwareService.getSolarTelemetry(_state.value.selectedKioskId)
    }

    fun resetDemo() {
        chargingTimerJob?.cancel()
        _state.value = repository.resetDemo()
        _toastMessage.tryEmit("Demo data restored to initial values.")
    }

    private fun currentTimestamp(): String {
        val sdf = SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }
}
