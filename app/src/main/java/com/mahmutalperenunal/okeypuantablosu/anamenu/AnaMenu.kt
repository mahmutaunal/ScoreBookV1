package com.mahmutalperenunal.okeypuantablosu.anamenu

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
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


    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnaMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set admob banner
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.mainMenuAdView.loadAd(adRequest)

        //dark and night mode
        sharedPreferencesTheme = getSharedPreferences("appTheme", MODE_PRIVATE)
        editorTheme = sharedPreferencesTheme.edit()

        //set update manager
        checkUpdate()

        //review manager
        activateReviewInfo()

        //check last theme
        checkLastTheme()

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
                .setNeutralButton("Geliştirici") {
                    dialog, _ ->

                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Mahmut+Alperen+%C3%9Cnal")))
                    } catch (e: ActivityNotFoundException) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Mahmut+Alperen+%C3%9Cnal")))
                    }

                    dialog.dismiss()
                }
                .setNegativeButton("İptal") {
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


    //update manager
    private fun checkUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        val task = appUpdateManager.appUpdateInfo
        task.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo, AppUpdateType.FLEXIBLE,
                        this@AnaMenu, UPDATE_CODE
                    )
                } catch (e: SendIntentException) {
                    e.printStackTrace()
                    Log.e("Update Error", e.toString())
                }
            }
        }
        appUpdateManager.registerListener(listener)
    }

    private var listener =
        InstallStateUpdatedListener { installState: InstallState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                popUp()
            }
        }

    private fun popUp() {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            "App Update Almost Done.",
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction("Roload") { view: View? -> appUpdateManager.completeUpdate() }
        snackbar.setTextColor(Color.parseColor("#FF000"))
        snackbar.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_CODE) {
            if (resultCode != RESULT_OK) {
            }
        }
    }


    //app review
    private fun activateReviewInfo() {
        reviewManager = ReviewManagerFactory.create(this)
        val managerInfoTask: com.google.android.play.core.tasks.Task<ReviewInfo> = reviewManager.requestReviewFlow()
        managerInfoTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                reviewInfo = task.result
            } else {
                Toast.makeText(this, "Değerlendirme Başlatılamadı!", Toast.LENGTH_SHORT).show()
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