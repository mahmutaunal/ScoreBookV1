package com.example.okeypuantablosu.anamenu

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.example.okeypuantablosu.R
import com.example.okeypuantablosu.databinding.ActivityAnaMenuBinding
import com.example.okeypuantablosu.takimislemleri.TakimIslemleri
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

class AnaMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnaMenuBinding


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
        val appSettingPreferences: SharedPreferences = getSharedPreferences("AppSettingsPreferences", 0)
        val sharedPreferencesEdit: SharedPreferences.Editor = appSettingPreferences.edit()
        val isNightModeOn: Boolean = appSettingPreferences.getBoolean("NightMode", false)

        if (isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }


        //navigate TakimIsimleri Activity
        binding.yeniOyunButton.setOnClickListener {
            val intentTakimIslemleri = Intent(applicationContext, TakimIslemleri::class.java)
            startActivity(intentTakimIslemleri)
            finish()

            //animation
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        //info about app
        binding.infoIcon.setOnClickListener {
            AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setTitle("Bilgilendirme")
                .setMessage("Bu uygulama ile herhangi bir okey oyunundaki puan tablonuzu tutabilirsiniz ve daha sonra bakmak veya devam etmek için kaydedebilirsiniz.")
                .setPositiveButton("Tamam") {
                        dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }


        //change dark and light mode with button click
        binding.darkModeIcon.setOnClickListener {

            if (isNightModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPreferencesEdit.putBoolean("NightMode", false)
                sharedPreferencesEdit.apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPreferencesEdit.putBoolean("NightMode", true)
                sharedPreferencesEdit.apply()
            }

        }
    }


    //on back pressed exit app
    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
        finish()

        //animation
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}