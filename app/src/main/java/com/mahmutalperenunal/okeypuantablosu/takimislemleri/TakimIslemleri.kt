package com.mahmutalperenunal.okeypuantablosu.takimislemleri

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.mahmutalperenunal.okeypuantablosu.R
import com.mahmutalperenunal.okeypuantablosu.databinding.ActivityTakimIslemleriBinding
import com.mahmutalperenunal.okeypuantablosu.anamenu.AnaMenu
import com.mahmutalperenunal.okeypuantablosu.puantablosu.PuanTablosu2Kisi
import com.mahmutalperenunal.okeypuantablosu.puantablosu.PuanTablosu3Kisi
import com.mahmutalperenunal.okeypuantablosu.puantablosu.PuanTablosu4Kisi
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

class TakimIslemleri : AppCompatActivity() {

    private lateinit var binding: ActivityTakimIslemleriBinding

    private var oyuncu1Ad: EditText? = null
    private var oyuncu2Ad: EditText? = null
    private var oyuncu3Ad: EditText? = null
    private var oyuncu4Ad: EditText? = null

    private var oyunIsmi: EditText? = null

    private var redValue: Int = 0
    private var blueValue: Int = 0
    private var yellowValue: Int = 0
    private var blackValue: Int = 0

    private var isColorsValueEntered: Boolean = false

    private var gameType: String = "Sayı Ekle"

    private var firstNumber: EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTakimIslemleriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set admob banner
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.takimIslemleriAdView.loadAd(adRequest)

        oyuncu1Ad = binding.oyuncu1EditText
        oyuncu2Ad = binding.oyuncu2EditText
        oyuncu3Ad = binding.oyuncu3EditText
        oyuncu4Ad = binding.oyuncu4EditText

        oyunIsmi = binding.oyunIsmiEditText

        firstNumber = binding.baslangicSayisiEditText

        oyuncuSayisiVisibility()
        oyuncuSayisi()
        oyuncuTuruneGoreTakimBelirleme()
        oyuncuTuru()
        oyunTuru()

        //navigate PuanTablosu Activity with number of player
        binding.baslatButton.setOnClickListener { setColorValue() }


        //on back pressed turn back to main menu
        binding.backButton.setOnClickListener {
            val intentMain = Intent(applicationContext, AnaMenu::class.java)
            startActivity(intentMain)
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

    }


    //set colors value
    @SuppressLint("InflateParams")
    private fun setColorValue() {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.colors_value, null)

        //set values
        val redEditText = view.findViewById<EditText>(R.id.red_editText)
        val blueEditText = view.findViewById<EditText>(R.id.blue_editText)
        val yellowEditText = view.findViewById<EditText>(R.id.yellow_editText)
        val blackEditText = view.findViewById<EditText>(R.id.black_editText)

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        addDialog.setView(view)
        addDialog.setPositiveButton("Başla") {
                dialog, _ ->

            if (redEditText.text.isEmpty() || blueEditText.text.isEmpty() || yellowEditText.text.isEmpty() || blackEditText.text.isEmpty()) {

                Toast.makeText(applicationContext, "Lütfen Tüm Alanları Doldurun!", Toast.LENGTH_SHORT).show()
                isColorsValueEntered = false

            } else {

                redValue = redEditText.text.toString().toInt()
                blueValue = blueEditText.text.toString().toInt()
                yellowValue = yellowEditText.text.toString().toInt()
                blackValue = blackEditText.text.toString().toInt()

                isColorsValueEntered = true

                startGame()

            }

                dialog.dismiss()
        }
        addDialog.setNegativeButton("İptal Et") {
                dialog, _ ->

            isColorsValueEntered = false

            dialog.dismiss()
        }
        addDialog.create()
        addDialog.show()
    }


    //start game
    private fun startGame() {

        val intentPuanTablosu2 = Intent(applicationContext, PuanTablosu2Kisi::class.java)
        val intentPuanTablosu3 = Intent(applicationContext, PuanTablosu3Kisi::class.java)
        val intentPuanTablosu4 = Intent(applicationContext, PuanTablosu4Kisi::class.java)

        if ( oyuncuSayisi() == 2 ) {

            //player name must be entered
            if ( binding.oyuncu1EditText.text!!.isEmpty() || binding.oyuncu2EditText.text!!.isEmpty() ) {
                Toast.makeText(applicationContext, "Lütfen oyuncu adlarını girin", Toast.LENGTH_SHORT).show()
            }

            else {

                if (isColorsValueEntered) {

                    //send game name to PuanTablosu Activity
                    intentPuanTablosu2.putExtra("Oyun İsmi", oyunIsmi!!.text.toString())

                    //send player names to PuanTablosu Activity
                    intentPuanTablosu2.putExtra("Oyuncu-1 Ad", oyuncu1Ad!!.text.toString())
                    intentPuanTablosu2.putExtra("Oyuncu-2 Ad", oyuncu2Ad!!.text.toString())

                    //send colors value to PuanTablosu Activity
                    intentPuanTablosu2.putExtra("Kırmızı Değer", redValue)
                    intentPuanTablosu2.putExtra("Mavi Değer", blueValue)
                    intentPuanTablosu2.putExtra("Sarı Değer", yellowValue)
                    intentPuanTablosu2.putExtra("Siyah Değer", blackValue)

                    //send game type and first number
                    intentPuanTablosu2.putExtra("Oyun Türü", gameType)
                    intentPuanTablosu2.putExtra("Başlangıç Sayısı", firstNumber!!.text.toString())

                    startActivity(intentPuanTablosu2)
                    finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                }

            }

        }

        else if ( oyuncuSayisi() == 3 ) {

            //player name must be entered
            if ( binding.oyuncu1EditText.text!!.isEmpty() || binding.oyuncu2EditText.text!!.isEmpty() || binding.oyuncu3EditText.text!!.isEmpty() ) {
                Toast.makeText(applicationContext, "Lütfen oyuncu adlarını girin", Toast.LENGTH_SHORT).show()
            }

            else {

                if (isColorsValueEntered) {

                    //send game name to PuanTablosu Activity
                    intentPuanTablosu3.putExtra("Oyun İsmi", oyunIsmi!!.text.toString())

                    //send player names to PuanTablosu Activity
                    intentPuanTablosu3.putExtra("Oyuncu-1 Ad", oyuncu1Ad!!.text.toString())
                    intentPuanTablosu3.putExtra("Oyuncu-2 Ad", oyuncu2Ad!!.text.toString())
                    intentPuanTablosu3.putExtra("Oyuncu-3 Ad", oyuncu3Ad!!.text.toString())

                    //send colors value to PuanTablosu Activity
                    intentPuanTablosu3.putExtra("Kırmızı Değer", redValue)
                    intentPuanTablosu3.putExtra("Mavi Değer", blueValue)
                    intentPuanTablosu3.putExtra("Sarı Değer", yellowValue)
                    intentPuanTablosu3.putExtra("Siyah Değer", blackValue)

                    //send game type and first number
                    intentPuanTablosu2.putExtra("Oyun Türü", gameType)
                    intentPuanTablosu2.putExtra("Başlangıç Sayısı", firstNumber!!.text.toString())

                    startActivity(intentPuanTablosu3)
                    finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                }

            }

        }

        else if ( oyuncuSayisi() == 4 ) {

            //player name must be entered
            if ( binding.oyuncu1EditText.text!!.isEmpty() || binding.oyuncu2EditText.text!!.isEmpty() || binding.oyuncu3EditText.text!!.isEmpty() || binding.oyuncu4EditText.text!!.isEmpty() ) {
                Toast.makeText(applicationContext, "Lütfen oyuncu adlarını girin", Toast.LENGTH_SHORT).show()
            }

            else {

                if (isColorsValueEntered) {

                    //send game name to PuanTablosu Activity
                    intentPuanTablosu4.putExtra("Oyun İsmi", oyunIsmi!!.text.toString())

                    //send player names to PuanTablosu Activity
                    intentPuanTablosu4.putExtra("Oyuncu-1 Ad", oyuncu1Ad!!.text.toString())
                    intentPuanTablosu4.putExtra("Oyuncu-2 Ad", oyuncu2Ad!!.text.toString())
                    intentPuanTablosu4.putExtra("Oyuncu-3 Ad", oyuncu3Ad!!.text.toString())
                    intentPuanTablosu4.putExtra("Oyuncu-4 Ad", oyuncu4Ad!!.text.toString())

                    //send colors value to PuanTablosu Activity
                    intentPuanTablosu4.putExtra("Kırmızı Değer", redValue)
                    intentPuanTablosu4.putExtra("Mavi Değer", blueValue)
                    intentPuanTablosu4.putExtra("Sarı Değer", yellowValue)
                    intentPuanTablosu4.putExtra("Siyah Değer", blackValue)

                    //send game type and first number
                    intentPuanTablosu2.putExtra("Oyun Türü", gameType)
                    intentPuanTablosu2.putExtra("Başlangıç Sayısı", firstNumber!!.text.toString())

                    startActivity(intentPuanTablosu4)
                    finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                }
            }

        }

    }


    //control of player number
    private fun oyuncuSayisi(): Int {
        return when {
            binding.ikiOyunculuRadioButton.isChecked -> 2
            binding.ucOyunculuRadioButton.isChecked -> 3
            else -> 4
        }
    }


    //change edittext visibility
    private fun oyuncuSayisiVisibility() {

        binding.ikiOyunculuRadioButton.setOnClickListener {
            binding.oyuncu3EditTextLayout.visibility = View.GONE
            binding.oyuncu4EditTextLayout.visibility = View.GONE

            binding.esliRadioButton.visibility = View.GONE
        }

        binding.ucOyunculuRadioButton.setOnClickListener {
            binding.oyuncu3EditTextLayout.visibility = View.VISIBLE
            binding.oyuncu4EditTextLayout.visibility = View.GONE

            binding.esliRadioButton.visibility = View.GONE
        }

        binding.dortOyunculuRadioButton.setOnClickListener {
            binding.oyuncu3EditTextLayout.visibility = View.VISIBLE
            binding.oyuncu4EditTextLayout.visibility = View.VISIBLE

            binding.esliRadioButton.visibility = View.VISIBLE
        }
    }


    //control of game type
    private fun oyuncuTuru(): String {

        return if ( binding.tekliRadioButton.isChecked ) {
            "Tekli"
        } else {
            "Eşli"
        }
    }


    //change hint font
    private fun oyuncuTuruneGoreTakimBelirleme() {

        binding.tekliRadioButton.setOnClickListener {
            binding.oyuncu1EditText.hint = "Oyuncu 1"
            binding.oyuncu2EditText.hint = "Oyuncu 2"
            binding.oyuncu3EditText.hint = "Oyuncu 3"
            binding.oyuncu4EditText.hint = "Oyuncu 4"
        }

        binding.esliRadioButton.setOnClickListener {
            binding.oyuncu1EditText.hint = "Oyuncu 1 (Takım-1)"
            binding.oyuncu2EditText.hint = "Oyuncu 2 (Takım-1)"
            binding.oyuncu3EditText.hint = "Oyuncu 3 (Takım-2)"
            binding.oyuncu4EditText.hint = "Oyuncu 4 (Takım-2)"
        }
    }


    //set game type
    @SuppressLint("SetTextI18n")
    private fun oyunTuru() {
        binding.sayidanDusRadioButton.setOnClickListener {
            gameType = "Sayıdan Düş"
            binding.baslangicSayisiEditTextLayout.visibility = View.VISIBLE
        }

        binding.sayiEKleRadioButton.setOnClickListener {
            gameType = "Sayı Ekle"
            binding.baslangicSayisiEditTextLayout.visibility = View.GONE
            firstNumber!!.setText("0000")
        }
    }


    //on back pressed turn back to main menu
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intentMain = Intent(applicationContext, AnaMenu::class.java)
        startActivity(intentMain)
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}