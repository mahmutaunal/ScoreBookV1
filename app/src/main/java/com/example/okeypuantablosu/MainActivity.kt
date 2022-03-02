package com.example.okeypuantablosu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.okeypuantablosu.anamenu.AnaMenuActivity
import com.example.okeypuantablosu.databinding.ActivityMainBinding
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
        intent = Intent(applicationContext, AnaMenuActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        val startActivityTimer = timerTask {
            startActivity(intent)
        }

        val timer = Timer()
        timer.schedule(startActivityTimer, 1000)

    }

}