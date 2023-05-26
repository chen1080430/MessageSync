package com.mason.messagesync

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.mason.messagesync.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var binding: ActivityMainBinding
    private val REQUEST_CODE_SMS_PERMISSION = 101

    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_message, R.id.navigation_about_offline
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

//        grantSmsPermission()

        Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion())
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}

        // Set your test devices. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
        // to get test ads on this device."
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(listOf("ABCDEF012345")).build()
        )
//
//        MobileAds.initialize(this) {}
//        /*
        var adRequest = AdRequest.Builder().build()
        Log.d(TAG, "XXXXX> onCreate: isTestDevice: ${adRequest.isTestDevice(this)}")

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712",
            adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                adError.let { Log.d(TAG, it.toString()) }
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })

//        /*
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d(TAG, "Ad dismissed fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }

//         */
        binding.floatingActionButton.setOnClickListener {
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
                Log.d(TAG, "XXXXX> fab click: show ad.\n mInterstitialAd = $mInterstitialAd ")
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }
        }
    }

    private fun grantSmsPermission() : Boolean {
        // check sms permission
        val smsPermission = android.Manifest.permission.RECEIVE_SMS
        val grant = checkCallingOrSelfPermission(smsPermission)
        if (grant != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            // request permission
            requestPermissions(arrayOf(smsPermission), REQUEST_CODE_SMS_PERMISSION)
        }
        return grant == android.content.pm.PackageManager.PERMISSION_GRANTED
//        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_SMS_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 權限已獲得，執行相應操作
                    Log.d(TAG, "XXXXX> onRequestPermissionsResult: permission granted.")
                } else {
                    // 權限被拒絕
                    Log.d(TAG, "XXXXX> onRequestPermissionsResult: permission denied.")
                }
            }
            // ...
        }
    }
}