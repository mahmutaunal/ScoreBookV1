package com.mahmutalperenunal.okeypuantablosu.diceroller

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.mahmutalperenunal.okeypuantablosu.R
import com.mahmutalperenunal.okeypuantablosu.databinding.ActivityDiceRollerBinding
import kotlin.random.Random

class DiceRoller : AppCompatActivity() {

    private lateinit var binding: ActivityDiceRollerBinding

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiceRollerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //screen size
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        window.setLayout(((width * .7).toInt()), (height * .3).toInt())

        //random dice
        randomDice()

        //again dice roll
        binding.yenidenZarAtButton.setOnClickListener { randomDice() }

        //exit diceRoller
        binding.okButton.setOnClickListener { finish() }

    }


    //generate random number dice
    private fun randomDice() {
        diceAnimation()

        val diceResource = when (Random.nextInt(6) + 1) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }

        val diceImage = findViewById<ImageView>(R.id.dice_imageView)
        diceImage.setImageResource(diceResource)
    }


    //dice animation
    private fun diceAnimation() {
        binding.diceImageView.animate().apply {
            duration = 1000
            rotationXBy(360f)
        }.withEndAction {
            binding.diceImageView.animate().apply {
                duration = 1000
                rotationX(360f)
            }.start()
        }
    }
}