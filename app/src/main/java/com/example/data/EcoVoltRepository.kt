package com.example.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class EcoVoltRepository(context: Context) {
    private val prefs = context.getSharedPreferences("ecovolt_demo_prefs", Context.MODE_PRIVATE)

    fun loadState(): EcoVoltState {
        val raw = prefs.getString(KEY_STATE, null) ?: return EcoVoltState()
        return try {
            val json = JSONObject(raw)
            val txArray = json.optJSONArray("transactions") ?: JSONArray()
            val transactions = buildList {
                for (i in 0 until txArray.length()) {
                    val o = txArray.getJSONObject(i)
                    add(
                        EcoTransaction(
                            id = o.getString("id"),
                            timestampLabel = o.getString("timestampLabel"),
                            type = TransactionType.valueOf(o.getString("type")),
                            description = o.getString("description"),
                            pointsDelta = o.getInt("pointsDelta"),
                            status = o.optString("status", "Completed (Demo)")
                        )
                    )
                }
            }

            val cpnArray = json.optJSONArray("redeemedCoupons") ?: JSONArray()
            val coupons = buildList {
                for (i in 0 until cpnArray.length()) {
                    val o = cpnArray.getJSONObject(i)
                    add(
                        RedeemedCoupon(
                            id = o.getString("id"),
                            partner = o.getString("partner"),
                            discount = o.getString("discount"),
                            code = o.getString("code"),
                            redeemedAt = o.getString("redeemedAt")
                        )
                    )
                }
            }

            EcoVoltState(
                userId = json.optString("userId", "EV-USER-0248"),
                ecoPoints = json.optInt("ecoPoints", 250).coerceAtLeast(0),
                pointsEarnedTotal = json.optInt("pointsEarnedTotal", 350),
                pointsRedeemedTotal = json.optInt("pointsRedeemedTotal", 100),
                itemsRecycled = json.optInt("itemsRecycled", 12),
                rewardsClaimed = json.optInt("rewardsClaimed", 2),
                greenImpactKg = json.optDouble("greenImpactKg", 1.8),
                plasticCount = json.optInt("plasticCount", 6),
                aluminiumCount = json.optInt("aluminiumCount", 4),
                paperCount = json.optInt("paperCount", 2),
                chargingSessionsCount = json.optInt("chargingSessionsCount", 1),
                selectedKioskId = json.optString("selectedKioskId", "EV-KIOSK-001"),
                transactions = transactions.ifEmpty { defaultTransactions() },
                redeemedCoupons = coupons
            )
        } catch (_: Exception) {
            EcoVoltState()
        }
    }

    fun saveState(state: EcoVoltState) {
        val json = JSONObject().apply {
            put("userId", state.userId)
            put("ecoPoints", state.ecoPoints.coerceAtLeast(0))
            put("pointsEarnedTotal", state.pointsEarnedTotal)
            put("pointsRedeemedTotal", state.pointsRedeemedTotal)
            put("itemsRecycled", state.itemsRecycled)
            put("rewardsClaimed", state.rewardsClaimed)
            put("greenImpactKg", state.greenImpactKg)
            put("plasticCount", state.plasticCount)
            put("aluminiumCount", state.aluminiumCount)
            put("paperCount", state.paperCount)
            put("chargingSessionsCount", state.chargingSessionsCount)
            put("selectedKioskId", state.selectedKioskId)

            val txArray = JSONArray()
            state.transactions.take(50).forEach { tx ->
                txArray.put(JSONObject().apply {
                    put("id", tx.id)
                    put("timestampLabel", tx.timestampLabel)
                    put("type", tx.type.name)
                    put("description", tx.description)
                    put("pointsDelta", tx.pointsDelta)
                    put("status", tx.status)
                })
            }
            put("transactions", txArray)

            val cpnArray = JSONArray()
            state.redeemedCoupons.take(30).forEach { c ->
                cpnArray.put(JSONObject().apply {
                    put("id", c.id)
                    put("partner", c.partner)
                    put("discount", c.discount)
                    put("code", c.code)
                    put("redeemedAt", c.redeemedAt)
                })
            }
            put("redeemedCoupons", cpnArray)
        }
        prefs.edit().putString(KEY_STATE, json.toString()).apply()
    }

    fun resetDemo(): EcoVoltState {
        val initial = EcoVoltState()
        saveState(initial)
        return initial
    }

    companion object {
        private const val KEY_STATE = "ecovolt_state_v1"
    }
}
