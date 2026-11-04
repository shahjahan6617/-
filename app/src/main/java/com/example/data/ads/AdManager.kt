package com.example.data.ads

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AdNetworkConfig(
    val providerName: String,
    val publisherId: String,
    val zoneId: String,
    val isEnabled: Boolean,
    val format: String, // Banner, Interstitial, Rewarded
    val dailyLimit: Int = 10,
    val rewardPoints: Long = 25L
)

class AdManager {

    private val _monetagConfig = MutableStateFlow(
        AdNetworkConfig(
            providerName = "Monetag",
            publisherId = "monetag_pub_882910",
            zoneId = "zone_inpage_rewarded_bd",
            isEnabled = true,
            format = "Rewarded Video / In-Page Push",
            dailyLimit = 10,
            rewardPoints = 25L
        )
    )
    val monetagConfig: StateFlow<AdNetworkConfig> = _monetagConfig.asStateFlow()

    private val _adverticaConfig = MutableStateFlow(
        AdNetworkConfig(
            providerName = "Advertica",
            publisherId = "adv_partner_9921",
            zoneId = "zone_native_interstitial_bd",
            isEnabled = true,
            format = "Direct Link / Rewarded Interstitial",
            dailyLimit = 10,
            rewardPoints = 20L
        )
    )
    val adverticaConfig: StateFlow<AdNetworkConfig> = _adverticaConfig.asStateFlow()

    private val _adsWatchedToday = MutableStateFlow(0)
    val adsWatchedToday: StateFlow<Int> = _adsWatchedToday.asStateFlow()

    private var lastWatchDate: String = ""

    fun checkAndResetDailyCounter() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        if (today != lastWatchDate) {
            lastWatchDate = today
            _adsWatchedToday.value = 0
        }
    }

    fun canWatchRewardedAd(): Boolean {
        checkAndResetDailyCounter()
        val limit = _monetagConfig.value.dailyLimit
        return _adsWatchedToday.value < limit
    }

    fun recordAdWatched() {
        checkAndResetDailyCounter()
        _adsWatchedToday.value += 1
    }

    fun updateMonetagConfig(newConfig: AdNetworkConfig) {
        _monetagConfig.value = newConfig
    }

    fun updateAdverticaConfig(newConfig: AdNetworkConfig) {
        _adverticaConfig.value = newConfig
    }
}
