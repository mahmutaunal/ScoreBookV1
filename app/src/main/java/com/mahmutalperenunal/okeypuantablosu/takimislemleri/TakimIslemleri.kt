package com.mahmutalperenunal.okeypuantablosu.takimislemleri

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTakimIslemleriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set admob banner
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.takimIslemleriAdView.loadAd(adRequest)

        val intentPuanTablosu2 = Intent(applicationContext, PuanTablosu2Kisi::class.java)
        val intentPuanTablosu3 = Intent(applicationContext, PuanTablosu3Kisi::class.java)
        val intentPuanTablosu4 = Intent(applicationContext, PuanTablosu4Kisi::class.java)

        oyuncu1Ad = binding.oyuncu1EditText
        oyuncu2Ad = binding.oyuncu2EditText
        oyuncu3Ad = binding.oyuncu3EditText
        oyuncu4Ad = binding.oyuncu4EditText

        oyunIsmi = binding.oyunIsmiEditText

        oyuncuSayisiVisibility()
        oyuncuSayisi()
        oyunTuruneGoreTakimBelirleme()
        oyunTuru()

        //navigate PuanTablosu Activity with number of player
        binding.baslatButton.setOnClickListener {

            if ( oyuncuSayisi() == 2 ) {

                //player name must be entered
                if ( binding.oyuncu1EditText.text!!.isEmpty() || binding.oyuncu2EditText.text!!.isEmpty() ) {
                    Toast.makeText(applicationContext, "Lütfen oyuncu adlarını girin", Toast.LENGTH_SHORT).show()
                }

                else {

                    //send game name to PuanTablosu Activity
                    intentPuanTablosu2.putExtra("Oyun İsmi", oyunIsmi!!.text.toString())

                    //send player names to PuanTablosu Activity
                    intentPuanTablosu2.putExtra("Oyuncu-1 Ad", oyuncu1Ad!!.text.toString())
                    intentPuanTablosu2.putExtra("Oyuncu-2 Ad", oyuncu2Ad!!.text.toString())

                    startActivity(intentPuanTablosu2)
                    finish()

                    //animation
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                }

            }

            else if ( oyuncuSayisi() == 3 ) {

                //player name must be entered
                if ( binding.oyuncu1EditText.text!!.isEmpty() || binding.oyuncu2EditText.text!!.isEmpty() || binding.oyuncu3EditText.text!!.isEmpty() ) {
                    Toast.makeText(applicationContext, "Lütfen oyuncu adlarını girin", Toast.LENGTH_SHORT).show()
                }

                else {

                    //send game name to PuanTablosu Activity
                    intentPuanTablosu3.putExtra("Oyun İsmi", oyunIsmi!!.text.toString())

                    //send player names to PuanTablosu Activity
                    intentPuanTablosu3.putExtra("Oyuncu-1 Ad", oyuncu1Ad!!.text.toString())
                    intentPuanTablosu3.putExtra("Oyuncu-2 Ad", oyuncu2Ad!!.text.toString())
                    intentPuanTablosu3.putExtra("Oyuncu-3 Ad", oyuncu3Ad!!.text.toString())

                    startActivity(intentPuanTablosu3)
                    finish()

                    //animation
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                }

            }

            else if ( oyuncuSayisi() == 4 ) {

                //player name must be entered
                if ( binding.oyuncu1EditText.text!!.isEmpty() || binding.oyuncu2EditText.text!!.isEmpty() || binding.oyuncu3EditText.text!!.isEmpty() || binding.oyuncu4EditText.text!!.isEmpty() ) {
                    Toast.makeText(applicationContext, "Lütfen oyuncu adlarını girin", Toast.LENGTH_SHORT).show()
                }

                else {

                    //send game name to PuanTablosu Activity
                    intentPuanTablosu4.putExtra("Oyun İsmi", oyunIsmi!!.text.toString())

                    //send player names to PuanTablosu Activity
                    intentPuanTablosu4.putExtra("Oyuncu-1 Ad", oyuncu1Ad!!.text.toString())
                    intentPuanTablosu4.putExtra("Oyuncu-2 Ad", oyuncu2Ad!!.text.toString())
                    intentPuanTablosu4.putExtra("Oyuncu-3 Ad", oyuncu3Ad!!.text.toString())
                    intentPuanTablosu4.putExtra("Oyuncu-4 Ad", oyuncu4Ad!!.text.toString())

                    startActivity(intentPuanTablosu4)
                    finish()

                    //animation
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }

            }

        }


        //on back pressed turn back to main menu
        binding.backButton.setOnClickListener {
            val intentMain = Intent(applicationContext, AnaMenu::class.java)
            startActivity(intentMain)
            finish()

            //animation
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
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
    private fun oyunTuru(): String {

        return if ( binding.tekliRadioButton.isChecked ) {
            "Tekli"
        } else {
            "Eşli"
        }
    }


    //change hint font
    private fun oyunTuruneGoreTakimBelirleme() {

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


    //on back pressed turn back to main menu
    override fun onBackPressed() {
        val intentMain = Intent(applicationContext, AnaMenu::class.java)
        startActivity(intentMain)
        finish()

        //animation
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}