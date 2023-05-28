package com.mahmutalperenunal.okeypuantablosu.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.mahmutalperenunal.okeypuantablosu.databinding.ActivitySplashScreenBinding
import java.util.*
import kotlin.concurrent.timerTask

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    private lateinit var sharedPreferencesTheme: SharedPreferences
    private lateinit var sharedPreferencesLanguage: SharedPreferences

    private var theme: Int? = null

    private var language: String? = null


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set screen orientation to portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //get last theme and last language
        sharedPreferencesTheme = getSharedPreferences("appTheme", MODE_PRIVATE)
        sharedPreferencesLanguage = getSharedPreferences("appLanguage", MODE_PRIVATE)

        checkTheme()

        checkLanguage()

        timer()
    }


    //timer for splash screen
    private fun timer() {
        intent = Intent(
            applicationContext,
            MainMenu::class.java
        ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        val startActivityTimer = timerTask {
            startActivity(intent)
        }

        val timer = Timer()
        timer.schedule(startActivityTimer, 1000)

    }


    //check theme
    private fun checkTheme() {
        theme = sharedPreferencesTheme.getInt("theme", 0)

        val appTheme = when (theme) {
            -1 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM //-1
            1 -> AppCompatDelegate.MODE_NIGHT_NO //1
            2 -> AppCompatDelegate.MODE_NIGHT_YES //2
            else -> {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
                    AppCompatDelegate.MODE_NIGHT_NO //1
                } else {
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM //-1
                }
            }

        }

        Log.d("App Theme", "theme:$appTheme")
        AppCompatDelegate.setDefaultNightMode(appTheme)
    }


    private fun checkLanguage() {
        language = sharedPreferencesLanguage.getString("language", null)

        when (language) {
            "tr" -> {
                val locale = Locale("tr")
                Locale.setDefault(locale)

                val configuration = Configuration()
                configuration.locale = locale

                baseContext.resources.updateConfiguration(
                    configuration,
                    baseContext.resources.displayMetrics
                )
            }

            "en" -> {
                val locale = Locale("en")
                Locale.setDefault(locale)

                val configuration = Configuration()
                configuration.locale = locale

                baseContext.resources.updateConfiguration(
                    configuration,
                    baseContext.resources.displayMetrics
                )
            }
        }

    }

}