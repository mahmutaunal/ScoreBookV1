package com.mahmutalperenunal.okeypuantablosu.activity

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
import com.mahmutalperenunal.okeypuantablosu.databinding.ActivityMainMenuBinding

class MainMenu : AppCompatActivity() {

    private lateinit var binding: ActivityMainMenuBinding

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
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set screen orientation to portrait
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

        //set app update manager
        appUpdateManager = AppUpdateManagerFactory.create(this)

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks whether the platform allows the specified type of update,
        // and current version staleness.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.updatePriority() >= 4
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                // Request the update.
                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // The current activity making the update request.
                    this,
                    AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE)
                        .setAllowAssetPackDeletion(true)
                        .build(),
                    UPDATE_CODE
                )
            }
        }

        // Before starting an update, register a listener for updates.
        appUpdateManager.registerListener(listener)

        //navigate TeamOperations Activity
        binding.mainMenuNewGameButton.setOnClickListener { startNewGame() }

        //info about app
        binding.mainMenuInfoIcon.setOnClickListener { infoApp() }


        //change dark and light mode with button click
        binding.mainMenuDarkModeIcon.setOnClickListener { changeAppTheme() }
    }


    //check last theme
    private fun checkLastTheme() {
        themeCode = sharedPreferencesTheme.getInt("theme", 0)

        when (themeCode) {
            -1 -> themeName = getString(R.string.system_theme_text)
            1 -> themeName = getString(R.string.light_text)
            2 -> themeName = getString(R.string.dark_text)
        }
    }


    //start new game
    private fun startNewGame() {
        val intentTeamOperations = Intent(applicationContext, TeamOperations::class.java)
        startActivity(intentTeamOperations)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }


    //info about app
    private fun infoApp() {
        AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle(R.string.info_text)
            .setMessage(R.string.info_description_text)
            .setPositiveButton(R.string.rate_text) { dialog, _ ->

                startReviewFlow()

                dialog.dismiss()
            }
            .setNegativeButton(R.string.developer_text) { dialog, _ ->

                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/dev?id=5245599652065968716")
                        )
                    )
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/dev?id=5245599652065968716")
                        )
                    )
                }

                dialog.dismiss()
            }
            .setNeutralButton(R.string.cancel_text) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }


    //change app theme
    private fun changeAppTheme() {
        AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle(R.string.app_theme_text)
            .setMessage("${getString(R.string.app_theme_description_text)} \n\n${getString(R.string.current_theme_text)} $themeName")
            .setPositiveButton(
                R.string.light_text
            ) { _: DialogInterface?, _: Int ->
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
                editorTheme.putInt("theme", 1)
                editorTheme.apply()
            }
            .setNegativeButton(
                R.string.dark_text
            ) { _: DialogInterface?, _: Int ->
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
                editorTheme.putInt("theme", 2)
                editorTheme.apply()
            }
            .setNeutralButton(
                R.string.system_theme_text
            ) { _: DialogInterface?, _: Int ->
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                )
                editorTheme.putInt("theme", -1)
                editorTheme.apply()
            }
            .show()
    }


    //app review
    private fun activateReviewInfo() {
        reviewManager = ReviewManagerFactory.create(this)
        val managerInfoTask: com.google.android.play.core.tasks.Task<ReviewInfo> =
            reviewManager.requestReviewFlow()
        managerInfoTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                reviewInfo = task.result
            }
        }
    }

    private fun startReviewFlow() {
        val flow: com.google.android.play.core.tasks.Task<Void> =
            reviewManager.launchReviewFlow(this, reviewInfo)
        flow.addOnCompleteListener {
            Toast.makeText(
                this,
                R.string.rate_completed_text,
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    // Create a listener to track request state updates.
    private val listener = InstallStateUpdatedListener { state ->
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
            R.string.update_downloaded_text,
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction(R.string.restart_text) { appUpdateManager.completeUpdate() }
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
                Toast.makeText(applicationContext, R.string.cancel_update_text, Toast.LENGTH_SHORT)
                    .show()
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