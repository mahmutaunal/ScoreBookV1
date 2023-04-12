package com.mahmutalperenunal.okeypuantablosu.anamenu

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.mahmutalperenunal.okeypuantablosu.R
import com.mahmutalperenunal.okeypuantablosu.databinding.ActivityAnaMenuBinding
import com.mahmutalperenunal.okeypuantablosu.takimislemleri.TakimIslemleri

class AnaMenu : AppCompatActivity() {

    private lateinit var binding: ActivityAnaMenuBinding

    private lateinit var sharedPreferencesTheme: SharedPreferences

    private lateinit var editorTheme: SharedPreferences.Editor

    private lateinit var appUpdateManager: AppUpdateManager

    private lateinit var reviewInfo: ReviewInfo
    private lateinit var reviewManager: ReviewManager

    private val UPDATE_CODE = 22

    private var themeCode: Int = 0
    private var themeName: String = ""


    @SuppressLint("InflateParams", "VisibleForTests", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnaMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //set admob banner
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.mainMenuAdView.loadAd(adRequest)

        //dark and night mode
        sharedPreferencesTheme = getSharedPreferences("appTheme", MODE_PRIVATE)
        editorTheme = sharedPreferencesTheme.edit()

        //review manager
        activateReviewInfo()

        //check last theme
        checkLastTheme()

        appUpdateManager = AppUpdateManagerFactory.create(this)

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks whether the platform allows the specified type of update,
        // and current version staleness.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.updatePriority() >= 4
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                // Request the update.
                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // The current activity making the update request.
                    this,
                    AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE)
                        .setAllowAssetPackDeletion(true)
                        .build(),
                    UPDATE_CODE)
            }
        }

        // Before starting an update, register a listener for updates.
        appUpdateManager.registerListener(listener)

        //navigate TakimIsimleri Activity
        binding.yeniOyunButton.setOnClickListener {
            val intentTakimIslemleri = Intent(applicationContext, TakimIslemleri::class.java)
            startActivity(intentTakimIslemleri)
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        //info about app
        binding.infoIcon.setOnClickListener {
            AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setTitle("Bilgilendirme")
                .setMessage("Bu uygulama ile herhangi bir okey oyunundaki puan tablonuzu tutabilirsiniz ve daha sonra bakmak veya devam etmek için kaydedebilirsiniz.")
                .setPositiveButton("Derecelendir") {
                        dialog, _ ->

                    startReviewFlow()

                    dialog.dismiss()
                }
                .setNegativeButton("Geliştirici") {
                    dialog, _ ->

                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=5245599652065968716")))
                    } catch (e: ActivityNotFoundException) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=5245599652065968716")))
                    }

                    dialog.dismiss()
                }
                .setNeutralButton("İptal") {
                        dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }


        //change dark and light mode with button click
        binding.darkModeIcon.setOnClickListener {

            AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setTitle("Uygulama Teması")
                .setMessage("Uygulama temasını seçiniz.\n\nMevcut tema: $themeName")
                .setPositiveButton(
                    "Açık"
                ) { _: DialogInterface?, _: Int ->
                    AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                    )
                    editorTheme.putInt("theme", 1)
                    editorTheme.apply()
                }
                .setNegativeButton(
                    "Koyu"
                ) { _: DialogInterface?, _: Int ->
                    AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES
                    )
                    editorTheme.putInt("theme", 2)
                    editorTheme.apply()
                }
                .setNeutralButton(
                    "Sistem Teması"
                ) { _: DialogInterface?, _: Int ->
                    AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    )
                    editorTheme.putInt("theme", -1)
                    editorTheme.apply()
                }
                .show()

        }
    }


    //check last theme
    private fun checkLastTheme() {
        themeCode = sharedPreferencesTheme.getInt("theme", 0)

        when (themeCode) {
            -1 -> themeName = "Sistem Teması"
            1 -> themeName = "Açık"
            2 -> themeName = "Koyu"
        }
    }


    //app review
    private fun activateReviewInfo() {
        reviewManager = ReviewManagerFactory.create(this)
        val managerInfoTask: com.google.android.play.core.tasks.Task<ReviewInfo> = reviewManager.requestReviewFlow()
        managerInfoTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                reviewInfo = task.result
            }
        }
    }

    private fun startReviewFlow() {
        val flow: com.google.android.play.core.tasks.Task<Void> = reviewManager.launchReviewFlow(this, reviewInfo)
        flow.addOnCompleteListener {
            Toast.makeText(
                this,
                "Değerlendirme Tamamlandı!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    // Create a listener to track request state updates.
    val listener = InstallStateUpdatedListener { state ->
        // (Optional) Provide a download progress bar.
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            // After the update is downloaded, show a notification
            // and request user confirmation to restart the app.
            popupSnackbarForCompleteUpdate()
        }
        // Log state or install the update.
    }


    // Displays the snackbar notification and call to action.
    private fun popupSnackbarForCompleteUpdate() {
        Snackbar.make(
            findViewById(android.R.id.content),
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("RESTART") { appUpdateManager.completeUpdate() }
            setActionTextColor(resources.getColor(R.color.teal_200))
            show()
        }
    }

    override fun onStop() {
        appUpdateManager.registerListener { listener }
        super.onStop()
    }

    // Checks that the update is not stalled during 'onResume()'.
    // However, you should execute this check at all app entry points.
    override fun onResume() {
        super.onResume()

        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                // If the update is downloaded but not installed,
                // notify the user to complete the update.
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate()
                }
            }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e("MY_APP", "Update flow failed! Result code: $resultCode")
                // If the update is cancelled or fails,
                // you can request to start the update again.
                Toast.makeText(applicationContext, "Güncelleme İptal Edildi!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    //on back pressed exit app
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
        finish()
    }
}