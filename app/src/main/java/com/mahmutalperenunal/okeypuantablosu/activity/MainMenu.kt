package com.mahmutalperenunal.okeypuantablosu.activity

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task
import com.mahmutalperenunal.okeypuantablosu.R
import com.mahmutalperenunal.okeypuantablosu.databinding.ActivityMainMenuBinding
import java.util.Locale


class MainMenu : AppCompatActivity() {

    private lateinit var binding: ActivityMainMenuBinding

    private lateinit var sharedPreferencesTheme: SharedPreferences
    private lateinit var sharedPreferencesLanguage: SharedPreferences

    private lateinit var editorTheme: SharedPreferences.Editor
    private lateinit var editorLanguage: SharedPreferences.Editor

    private var manager: AppUpdateManager? = null
    private var task: Task<AppUpdateInfo>? = null

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

        //language
        sharedPreferencesLanguage = getSharedPreferences("appLanguage", MODE_PRIVATE)
        editorLanguage = sharedPreferencesLanguage.edit()

        //check last theme
        checkLastTheme()

        //check app update
        checkAppUpdate()

        //navigate TeamOperations Activity
        binding.mainMenuNewGameButton.setOnClickListener { startNewGame() }

        //info about app
        binding.mainMenuInfoIcon.setOnClickListener { infoApp() }

        //change dark and light mode with button click
        binding.mainMenuAppThemeIcon.setOnClickListener { changeAppTheme() }

        //change app language
        binding.mainMenuAppLanguageIcon.setOnClickListener { changeAppLanguage() }
    }


    //change app language
    private fun changeAppLanguage() {

        AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle(R.string.app_language_text)
            .setMessage(getString(R.string.app_language_description_text))
            .setPositiveButton(
                R.string.tr_text
            ) { _: DialogInterface?, _: Int ->

                val locale = Locale("tr")
                Locale.setDefault(locale)

                val configuration = Configuration()
                configuration.locale = locale

                baseContext.resources.updateConfiguration(
                    configuration,
                    baseContext.resources.displayMetrics
                )

                editorLanguage.putString("language", "tr")
                editorLanguage.apply()

                recreate()

            }
            .setNegativeButton(
                R.string.eng_text
            ) { _: DialogInterface?, _: Int ->

                val locale = Locale("en")
                Locale.setDefault(locale)

                val configuration = Configuration()
                configuration.locale = locale

                baseContext.resources.updateConfiguration(
                    configuration,
                    baseContext.resources.displayMetrics
                )

                editorLanguage.putString("language", "en")
                editorLanguage.apply()

                recreate()

            }
            .show()

    }


    //check last theme
    private fun checkLastTheme() {
        themeCode = sharedPreferencesTheme.getInt("theme", 0)

        themeName = when (themeCode) {
            -1 -> getString(R.string.system_theme_text)
            1 -> getString(R.string.light_text)
            2 -> getString(R.string.dark_text)
            else -> {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
                    getString(R.string.light_text)
                } else {
                    getString(R.string.system_theme_text)
                }
            }
        }
    }


    //check app update
    private fun checkAppUpdate() {
        manager = AppUpdateManagerFactory.create(this)
        task = manager!!.appUpdateInfo
        task!!.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {

                AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .setTitle(R.string.update_available_text)
                    .setMessage(R.string.update_available_description_text)
                    .setPositiveButton(R.string.update_text) { dialog, _ ->

                        try {
                            val intent = Intent(Intent.ACTION_VIEW)
                            val uri = Uri.parse("market://details?id=$packageName")
                            intent.data = uri
                            intent.setPackage("com.android.vending")
                            startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            val intent = Intent(Intent.ACTION_VIEW)
                            val uri = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                            intent.data = uri
                            startActivity(intent)
                        }

                        dialog.dismiss()
                    }
                    .setNeutralButton(R.string.cancel_text) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()

            }
        }
    }


    //start new game
    private fun startNewGame() {
        val intentTeamOperations = Intent(applicationContext, TeamOperations::class.java)
        startActivity(intentTeamOperations)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }


    //info about app
    private fun infoApp() {
        AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle(R.string.info_text)
            .setMessage(R.string.info_description_text)
            .setPositiveButton(R.string.developer_text) { dialog, _ ->

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

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {

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
                .show()

        } else {

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

    }


    //on back pressed exit app
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }
}