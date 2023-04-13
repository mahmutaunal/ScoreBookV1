package com.mahmutalperenunal.okeypuantablosu.takimislemleri

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.mahmutalperenunal.okeypuantablosu.R
import com.mahmutalperenunal.okeypuantablosu.anamenu.AnaMenu
import com.mahmutalperenunal.okeypuantablosu.databinding.ActivityTakimIslemleriBinding
import com.mahmutalperenunal.okeypuantablosu.puantablosu.PuanTablosu2Kisi
import com.mahmutalperenunal.okeypuantablosu.puantablosu.PuanTablosu3Kisi
import com.mahmutalperenunal.okeypuantablosu.puantablosu.PuanTablosu4Kisi

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


    @SuppressLint("VisibleForTests", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTakimIslemleriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

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
        binding.baslatButton.setOnClickListener { controlUsernames() }


        //on back pressed turn back to main menu
        binding.backButton.setOnClickListener {
            val intentMain = Intent(applicationContext, AnaMenu::class.java)
            startActivity(intentMain)
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

    }


    //check usernames
    private fun controlUsernames() {

        if (oyuncuSayisi() == 2) {

            if (binding.oyuncu1EditText.text!!.isEmpty()) {
                binding.oyuncu1EditTextLayout.error = "Zorunlu"
                Toast.makeText(applicationContext, "Lütfen oyuncu adlarını girin", Toast.LENGTH_SHORT).show()
            } else if (binding.oyuncu2EditText.text!!.isEmpty()) {
                binding.oyuncu2EditTextLayout.error = "Zorunlu"
                Toast.makeText(applicationContext, "Lütfen oyuncu adlarını girin", Toast.LENGTH_SHORT).show()
            } else {
                if (gameType == "Sayıdan Düş") {
                    if (firstNumber!!.text.toString() == "") {
                        binding.baslangicSayisiEditTextLayout.error = "Zorunlu"
                        Toast.makeText(applicationContext, "Lütfen oyuncu adlarını girin", Toast.LENGTH_SHORT).show()
                    } else { setColorValue() }
                } else { setColorValue() }
            }

        } else if (oyuncuSayisi() == 3) {

            if (binding.oyuncu1EditText.text!!.isEmpty()) {
                binding.oyuncu1EditTextLayout.error = "Zorunlu"
                Toast.makeText(applicationContext, "Lütfen oyuncu adlarını girin", Toast.LENGTH_SHORT).show()
            } else if (binding.oyuncu2EditText.text!!.isEmpty()) {
                binding.oyuncu2EditTextLayout.error = "Zorunlu"
                Toast.makeText(applicationContext, "Lütfen oyuncu adlarını girin", Toast.LENGTH_SHORT).show()
            } else if (binding.oyuncu3EditText.text!!.isEmpty()) {
                binding.oyuncu3EditTextLayout.error = "Zorunlu"
                Toast.makeText(applicationContext, "Lütfen oyuncu adlarını girin", Toast.LENGTH_SHORT).show()
            } else {
                if (gameType == "Sayıdan Düş") {
                    if (firstNumber!!.text.toString() == "") {
                        binding.baslangicSayisiEditTextLayout.error = "Zorunlu"
                        Toast.makeText(applicationContext, "Lütfen oyuncu adlarını girin", Toast.LENGTH_SHORT).show()
                    } else { setColorValue() }
                } else { setColorValue() }
            }

        } else if (oyuncuSayisi() == 4) {

            if (binding.oyuncu1EditText.text!!.isEmpty()) {
                binding.oyuncu1EditTextLayout.error = "Zorunlu"
                Toast.makeText(applicationContext, "Lütfen oyuncu adlarını girin", Toast.LENGTH_SHORT).show()
            } else if (binding.oyuncu2EditText.text!!.isEmpty()) {
                binding.oyuncu2EditTextLayout.error = "Zorunlu"
                Toast.makeText(applicationContext, "Lütfen oyuncu adlarını girin", Toast.LENGTH_SHORT).show()
            } else if (binding.oyuncu3EditText.text!!.isEmpty()) {
                binding.oyuncu3EditTextLayout.error = "Zorunlu"
                Toast.makeText(applicationContext, "Lütfen oyuncu adlarını girin", Toast.LENGTH_SHORT).show()
            } else if (binding.oyuncu2EditText.text!!.isEmpty()) {
                binding.oyuncu3EditTextLayout.error = "Zorunlu"
                Toast.makeText(applicationContext, "Lütfen oyuncu adlarını girin", Toast.LENGTH_SHORT).show()
            } else {
                if (gameType == "Sayıdan Düş") {
                    if (firstNumber!!.text.toString() == "") {
                        binding.baslangicSayisiEditTextLayout.error = "Zorunlu"
                        Toast.makeText(applicationContext, "Lütfen oyuncu adlarını girin", Toast.LENGTH_SHORT).show()
                    } else { setColorValue() }
                } else { setColorValue() }
            }

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

        val redEditTextLayout = view.findViewById<TextInputLayout>(R.id.red_editTextLayout)
        val blueEditTextLayout = view.findViewById<TextInputLayout>(R.id.blue_editTextLayout)
        val yellowEditTextLayout = view.findViewById<TextInputLayout>(R.id.yellow_editTextLayout)
        val blackEditTextLayout = view.findViewById<TextInputLayout>(R.id.black_editTextLayout)

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        addDialog.setView(view)
        addDialog.setPositiveButton("Başla"
        ) { dialog, _ ->
            if (redEditText.text.isEmpty()) {

                redEditTextLayout.error = "Zorunlu"
                Toast.makeText(applicationContext, "Lütfen Renk Değerlerini Girin veya Atla Seçin!", Toast.LENGTH_SHORT).show()
                isColorsValueEntered = false

            } else if (blueEditText.text.isEmpty()) {

                blueEditTextLayout.error = "Zorunlu"
                Toast.makeText(applicationContext, "Lütfen Renk Değerlerini Girin veya Atla Seçin!", Toast.LENGTH_SHORT).show()
                isColorsValueEntered = false

            } else if (yellowEditText.text.isEmpty()) {

                yellowEditTextLayout.error = "Zorunlu"
                Toast.makeText(applicationContext, "Lütfen Renk Değerlerini Girin veya Atla Seçin!", Toast.LENGTH_SHORT).show()
                isColorsValueEntered = false

            } else if (blackEditText.text.isEmpty()) {

                blackEditTextLayout.error = "Zorunlu"
                Toast.makeText(applicationContext, "Lütfen Renk Değerlerini Girin veya Atla Seçin!", Toast.LENGTH_SHORT).show()
                isColorsValueEntered = false

            } else {

                redValue = redEditText.text.toString().toInt()
                blueValue = blueEditText.text.toString().toInt()
                yellowValue = yellowEditText.text.toString().toInt()
                blackValue = blackEditText.text.toString().toInt()

                isColorsValueEntered = true

                startGame()

                dialog.dismiss()
            }
        }
        addDialog.setNegativeButton("Atla"
        ) { dialog, _ ->
            redValue = 1
            blueValue = 1
            yellowValue = 1
            blackValue = 1

            isColorsValueEntered = true

            startGame()

            dialog.dismiss()
        }
        addDialog.setNeutralButton("İptal"
        ) { dialog, _ ->
            isColorsValueEntered = true
            dialog.dismiss()
        }
        addDialog.setCancelable(false)
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
                    intentPuanTablosu3.putExtra("Oyun Türü", gameType)
                    intentPuanTablosu3.putExtra("Başlangıç Sayısı", firstNumber!!.text.toString())

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

                    if (oyuncuTuru() == "Eşli") {

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

                        startActivity(intentPuanTablosu4)
                        finish()
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                    } else {

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
                        intentPuanTablosu4.putExtra("Oyun Türü", gameType)
                        intentPuanTablosu4.putExtra("Başlangıç Sayısı", firstNumber!!.text.toString())

                        startActivity(intentPuanTablosu4)
                        finish()
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                    }

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
            binding.oyuncu1EditTextLayout.hint = "Oyuncu 1"
            binding.oyuncu2EditTextLayout.hint = "Oyuncu 2"
            binding.oyuncu1EditTextLayout.visibility = View.VISIBLE
            binding.oyuncu2EditTextLayout.visibility = View.VISIBLE
            binding.oyuncu3EditTextLayout.visibility = View.VISIBLE
            binding.oyuncu4EditTextLayout.visibility = View.VISIBLE
        }

        binding.esliRadioButton.setOnClickListener {
            binding.oyuncu1EditTextLayout.hint = "Takım 1"
            binding.oyuncu2EditTextLayout.hint = "Takım 2"
            binding.oyuncu1EditTextLayout.visibility = View.VISIBLE
            binding.oyuncu2EditTextLayout.visibility = View.VISIBLE
            binding.oyuncu3EditTextLayout.visibility = View.GONE
            binding.oyuncu4EditTextLayout.visibility = View.GONE
        }
    }


    //set game type
    @SuppressLint("SetTextI18n")
    private fun oyunTuru() {
        binding.sayidanDusRadioButton.setOnClickListener {
            gameType = "Sayıdan Düş"
            binding.baslangicSayisiEditTextLayout.visibility = View.VISIBLE
            firstNumber!!.setText("")
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