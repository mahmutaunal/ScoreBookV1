package com.mahmutalperenunal.okeypuantablosu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mahmutalperenunal.okeypuantablosu.databinding.ActivityMainBinding
import com.mahmutalperenunal.okeypuantablosu.anamenu.AnaMenu
import java.util.*
import kotlin.concurrent.timerTask

//app splash screen with timer
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        timer()

    }


    private fun timer() {
        intent = Intent(applicationContext, AnaMenu::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        val startActivityTimer = timerTask {
            startActivity(intent)
        }

        val timer = Timer()
        timer.schedule(startActivityTimer, 1000)

    }

}