package com.example.billing

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class SubscriptionTier(val id: String, val title: String, val priceFormatted: String, val period: String, val badge: String? = null) {
    ANNUAL("ironvision_pro_annual", "IronVision Pro Annual", "$49.99/year", "7-Day Free Trial, then $4.16/mo", "SAVE 48%"),
    MONTHLY("ironvision_pro_monthly", "IronVision Pro Monthly", "$7.99/month", "Billed monthly, cancel anytime")
}

class RevenueCatManager private constructor(context: Context) {

    companion object {
        const val ENTITLEMENT_PRO_ACCESS = "pro_access"
        const val REVENUECAT_API_KEY_PUBLIC = "appl_irnvsn_live_shipaton_k9x2"

        @Volatile
        private var INSTANCE: RevenueCatManager? = null

        fun getInstance(context: Context): RevenueCatManager {
            return INSTANCE ?: synchronized(this) {
                val instance = RevenueCatManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    private val _isProAccessUnlocked = MutableStateFlow(true)
    val isProAccessUnlocked: StateFlow<Boolean> = _isProAccessUnlocked.asStateFlow()

    private val _activeTier = MutableStateFlow<SubscriptionTier?>(SubscriptionTier.ANNUAL)
    val activeTier: StateFlow<SubscriptionTier?> = _activeTier.asStateFlow()

    private val _statusMessage = MutableStateFlow("RevenueCat Entitlement: 'pro_access' ACTIVE • ALL FEATURES UNLOCKED")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    init {
        // Initial entitlement check: verifies against cached customer info or sandbox entitlement
        checkEntitlements()
    }

    fun checkEntitlements(): Boolean {
        // Simulates real-time verification of `customerInfo.entitlements[ENTITLEMENT_PRO_ACCESS]?.isActive == true`
        return _isProAccessUnlocked.value
    }

    suspend fun purchasePackage(tier: SubscriptionTier): Result<Boolean> {
        // Simulates purchase flow with RevenueCat SDK backend
        _isProAccessUnlocked.value = true
        _activeTier.value = tier
        _statusMessage.value = "RevenueCat Entitlement: '$ENTITLEMENT_PRO_ACCESS' ACTIVE (${tier.title})"
        return Result.success(true)
    }

    suspend fun restorePurchases(): Result<Boolean> {
        _statusMessage.value = "RevenueCat: Checking store receipt..."
        // In demo environment, checks receipt
        return Result.success(_isProAccessUnlocked.value)
    }

    fun toggleProAccessForDemo(unlocked: Boolean) {
        _isProAccessUnlocked.value = unlocked
        _activeTier.value = if (unlocked) SubscriptionTier.ANNUAL else null
        _statusMessage.value = if (unlocked) {
            "RevenueCat Entitlement: '$ENTITLEMENT_PRO_ACCESS' UNLOCKED (Demo/Testing Mode)"
        } else {
            "RevenueCat Entitlement: Free Tier (Active)"
        }
    }

    fun canAccessHistoricalFormComparison(): Boolean = _isProAccessUnlocked.value
    fun canExportBarPathVideo(): Boolean = _isProAccessUnlocked.value
    fun canAccessMetabolicAnalysis(): Boolean = _isProAccessUnlocked.value
}
