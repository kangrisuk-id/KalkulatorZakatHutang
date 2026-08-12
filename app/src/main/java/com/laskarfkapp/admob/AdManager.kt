package com.laskarfkapp.zakathutang.admob

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object AdManager {
    private const val TAG = "AdManager"

    // Production AdMob Application ID & Unit IDs
    const val APP_ID = "ca-app-pub-9751177929120690~5950585812"
    const val BANNER_AD_UNIT_ID = "ca-app-pub-9751177929120690/7268225276"
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-9751177929120690/2364096960"
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-9751177929120690/5345483524"
    const val NATIVE_AD_UNIT_ID = "ca-app-pub-9751177929120690/6574804715"

    // Frequency capping configuration: Max 1 interstitial ad per 3 minutes
    private const val FREQUENCY_CAP_INTERVAL_MS = 3 * 60 * 1000L // 3 minutes
    private var lastInterstitialTimeMs: Long = 0L

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        try {
            MobileAds.initialize(context) {
                isInitialized = true
                Log.d(TAG, "AdMob SDK Initialized")
                preloadInterstitial(context)
                preloadRewardedAd(context)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize MobileAds", e)
        }
    }

    fun preloadInterstitial(context: Context) {
        if (interstitialAd != null) return

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    Log.d(TAG, "Interstitial Ad Loaded successfully")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    Log.w(TAG, "Interstitial Ad Failed to load: ${error.message}")
                }
            }
        )
    }

    fun showInterstitialIfAllowed(
        activity: Activity,
        onFinished: () -> Unit
    ) {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastAd = currentTime - lastInterstitialTimeMs

        // Check frequency capping constraint (1 ad per 3 minutes)
        if (timeSinceLastAd >= FREQUENCY_CAP_INTERVAL_MS && interstitialAd != null) {
            val ad = interstitialAd
            ad?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    lastInterstitialTimeMs = System.currentTimeMillis()
                    preloadInterstitial(activity)
                    onFinished()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    preloadInterstitial(activity)
                    onFinished()
                }
            }
            ad?.show(activity)
        } else {
            // Frequency capped or ad not ready -> execute callback immediately to keep app smooth
            if (timeSinceLastAd < FREQUENCY_CAP_INTERVAL_MS) {
                val remainingSec = (FREQUENCY_CAP_INTERVAL_MS - timeSinceLastAd) / 1000
                Log.d(TAG, "Interstitial frequency capped! Next ad allowed in ${remainingSec}s")
            }
            onFinished()
        }
    }

    fun preloadRewardedAd(context: Context) {
        if (rewardedAd != null) return

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            REWARDED_AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    Log.d(TAG, "Rewarded Ad Loaded successfully")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    Log.w(TAG, "Rewarded Ad Failed to load: ${error.message}")
                }
            }
        )
    }

    fun isRewardedAdReady(): Boolean = rewardedAd != null

    fun showRewardedAd(
        activity: Activity,
        onRewardEarned: () -> Unit,
        onClosedOrFailed: () -> Unit
    ) {
        val ad = rewardedAd
        if (ad != null) {
            var rewardEarned = false
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    preloadRewardedAd(activity)
                    if (rewardEarned) {
                        onRewardEarned()
                    } else {
                        onClosedOrFailed()
                    }
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    preloadRewardedAd(activity)
                    onClosedOrFailed()
                }
            }
            ad.show(activity) {
                rewardEarned = true
            }
        } else {
            // Reload and inform calling screen
            preloadRewardedAd(activity)
            onClosedOrFailed()
        }
    }
}

/**
 * Collapsible Banner Composable for AdMob.
 * Uses high eCPM Collapsible Banner format with 'collapsible' = 'bottom'.
 */
@Composable
fun CollapsibleBannerAd(
    modifier: Modifier = Modifier,
    adUnitId: String = AdManager.BANNER_AD_UNIT_ID
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    this.adUnitId = adUnitId

                    // Collapsible Banner extra parameter to boost eCPM up to 2.5x
                    val extras = Bundle().apply {
                        putString("collapsible", "bottom")
                    }
                    val adRequest = AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                        .build()

                    loadAd(adRequest)
                }
            }
        )
    }
}
