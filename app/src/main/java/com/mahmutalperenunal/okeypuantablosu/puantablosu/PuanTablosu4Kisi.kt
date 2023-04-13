package com.mahmutalperenunal.okeypuantablosu.puantablosu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mahmutalperenunal.okeypuantablosu.R
import com.mahmutalperenunal.okeypuantablosu.databinding.ActivityPuanTablosu4KisiBinding
import com.mahmutalperenunal.okeypuantablosu.diceroller.DiceRoller
import com.mahmutalperenunal.okeypuantablosu.anamenu.AnaMenu
import com.mahmutalperenunal.okeypuantablosu.takimislemleri.TakimIslemleri
import com.mahmutalperenunal.okeypuantablosu.adapter.SkorAdapter4Kisi
import com.mahmutalperenunal.okeypuantablosu.model.SkorData4Kisi
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.mahmutalperenunal.okeypuantablosu.calculator.Calculator

//operations such as entering scores, deleting players.
class PuanTablosu4Kisi : AppCompatActivity() {

    private  lateinit var binding: ActivityPuanTablosu4KisiBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var skorList: ArrayList<SkorData4Kisi>
    private  lateinit var skorAdapter4Kisi: SkorAdapter4Kisi

    private var skorCount: Int = -1

    private var oyunIsmi: String? = null

    private var oyuncu1Ad: String? = null
    private var oyuncu2Ad: String? = null
    private var oyuncu3Ad: String? = null
    private var oyuncu4Ad: String? = null

    private var oyuncu1Skor: EditText? = null
    private var oyuncu2Skor: EditText? = null
    private var oyuncu3Skor: EditText? = null
    private var oyuncu4Skor: EditText? = null

    private var gameNumber: Int = 1

    private var clickCount: Int = 0

    private var isSelected: Boolean = false

    private lateinit var sharedPreferences: SharedPreferences

    private var multiplyNumber: Int = 1

    private var color: String = "White"

    private var redValue: Int = 0
    private var blueValue: Int = 0
    private var yellowValue: Int = 0
    private var blackValue: Int = 0

    private var gameType: String = "Sayı Ekle"

    private var firstNumber: String = "0000"

    private var firstScore1: Int = 1
    private var firstScore2: Int = 1
    private var firstScore3: Int = 1
    private var firstScore4: Int = 1

    private lateinit var sharedPreferencesTheme: SharedPreferences


    @SuppressLint("SetTextI18n", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPuanTablosu4KisiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //set admob banner
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.puanTablosu4AdView.loadAd(adRequest)

        //game name
        oyunIsmi = intent.getStringExtra("Oyun İsmi").toString()

        if ( oyunIsmi == "" ) {
            binding.baslikText.text = "Yeni Oyun"
        }
        else {
            binding.baslikText.text = oyunIsmi
        }

        //player names
        oyuncu1Ad = intent.getStringExtra("Oyuncu-1 Ad").toString()
        oyuncu2Ad = intent.getStringExtra("Oyuncu-2 Ad").toString()
        oyuncu3Ad = intent.getStringExtra("Oyuncu-3 Ad").toString()
        oyuncu4Ad = intent.getStringExtra("Oyuncu-4 Ad").toString()

        binding.oyuncu1Text.text = oyuncu1Ad
        binding.oyuncu2Text.text = oyuncu2Ad
        binding.oyuncu3Text.text = oyuncu3Ad
        binding.oyuncu4Text.text = oyuncu4Ad

        //get colors value
        redValue = intent.getIntExtra("Kırmızı Değer", 0)
        blueValue = intent.getIntExtra("Mavi Değer", 0)
        yellowValue = intent.getIntExtra("Sarı Değer", 0)
        blackValue = intent.getIntExtra("Siyah Değer", 0)

        //get game type and first number
        gameType = intent.getStringExtra("Oyun Türü").toString()
        firstNumber = intent.getStringExtra("Başlangıç Sayısı").toString()

        if (firstNumber.isEmpty()) {
            binding.oyuncu1AnlikSkor.text = "0000"
            binding.oyuncu2AnlikSkor.text = "0000"
            binding.oyuncu3AnlikSkor.text = "0000"
            binding.oyuncu4AnlikSkor.text = "0000"
        } else {
            binding.oyuncu1AnlikSkor.text = firstNumber
            binding.oyuncu2AnlikSkor.text = firstNumber
            binding.oyuncu3AnlikSkor.text = firstNumber
            binding.oyuncu4AnlikSkor.text = firstNumber
        }


        //get click count
        sharedPreferences = getSharedPreferences("clickCount4Kisi", Context.MODE_PRIVATE)

        //get theme
        sharedPreferencesTheme = getSharedPreferences("appTheme", MODE_PRIVATE)


        //set list
        skorList = ArrayList()

        //set recyclerView
        recyclerView = findViewById(R.id.puanTablosu_recyclerView)

        //set adapter
        skorAdapter4Kisi = SkorAdapter4Kisi(skorList)

        //set recyclerView adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = skorAdapter4Kisi


        //onClick process
        onClickProcess()


        //set skorEkle dialog
        binding.skorEkleButton.setOnClickListener { skorEkle() }

        //set puanTablosu dialog
        binding.skorTablosuButton.setOnClickListener { puanTablosu() }

        //exit main menu
        binding.backButton.setOnClickListener { exitMainMenu() }

        //save & exit
        binding.oyunuBitirButton.setOnClickListener { saveExit() }

        //dice roller
        binding.diceIcon.setOnClickListener { diceRoller() }

        //calculator
        binding.calculatorIcon.setOnClickListener { openCalculator() }
    }


    //add score
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n", "InflateParams")
    private fun skorEkle() {

        multiplyNumber = 1

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.add_item_4_kisi, null)

        color = "White"

        //set oyuncuSkor view
        oyuncu1Skor = view.findViewById(R.id.oyuncu1Skor_editText)
        oyuncu2Skor = view.findViewById(R.id.oyuncu2Skor_editText)
        oyuncu3Skor = view.findViewById(R.id.oyuncu3Skor_editText)
        oyuncu4Skor = view.findViewById(R.id.oyuncu4Skor_editText)

        //set player names
        val oyuncu1Text = view.findViewById<TextView>(R.id.oyuncu1Ekle_textView)
        val oyuncu2Text = view.findViewById<TextView>(R.id.oyuncu2Ekle_textView)
        val oyuncu3Text = view.findViewById<TextView>(R.id.oyuncu3Ekle_textView)
        val oyuncu4Text = view.findViewById<TextView>(R.id.oyuncu4Ekle_textView)

        //set colors layout visibility
        val colorLayout = view.findViewById<RadioGroup>(R.id.colors_radioGroup)

        if (redValue == 1 && blueValue == 1 && yellowValue == 1 && blackValue == 1) {
            colorLayout.visibility = View.GONE
        }

        //set colors
        val noColorButton = view.findViewById<RadioButton>(R.id.noColor_radioButton)
        val redButton = view.findViewById<RadioButton>(R.id.red_radioButton)
        val blueButton = view.findViewById<RadioButton>(R.id.blue_radioButton)
        val yellowButton = view.findViewById<RadioButton>(R.id.yellow_radioButton)
        val blackButton = view.findViewById<RadioButton>(R.id.black_radioButton)

        //set multiply
        val cross = view.findViewById<LinearLayout>(R.id.cross_linearLayout)
        val multiply = view.findViewById<LinearLayout>(R.id.multiply_linearLayout)

        val multiply1 = view.findViewById<TextView>(R.id.multiply1_text)
        val multiply2 = view.findViewById<TextView>(R.id.multiply2_text)
        val multiply3 = view.findViewById<TextView>(R.id.multiply3_text)
        val multiply4 = view.findViewById<TextView>(R.id.multiply4_text)

        //set visibility
        noColorButton.setOnClickListener {
            cross.visibility = View.GONE
            multiply.visibility = View.GONE

            multiplyNumber = 1

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()
            multiply3.text = multiplyNumber.toString()
            multiply4.text = multiplyNumber.toString()

            color = "White"
        }

        redButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = redValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()
            multiply3.text = multiplyNumber.toString()
            multiply4.text = multiplyNumber.toString()

            color = "Red"
        }

        blueButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = blueValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()
            multiply3.text = multiplyNumber.toString()
            multiply4.text = multiplyNumber.toString()

            color = "Blue"
        }

        yellowButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = yellowValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()
            multiply3.text = multiplyNumber.toString()
            multiply4.text = multiplyNumber.toString()

            color = "Yellow"
        }

        blackButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = blackValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()
            multiply3.text = multiplyNumber.toString()
            multiply4.text = multiplyNumber.toString()

            color = "Black"
        }

        oyuncu1Text.text = oyuncu1Ad
        oyuncu2Text.text = oyuncu2Ad
        oyuncu3Text.text = oyuncu3Ad
        oyuncu4Text.text = oyuncu4Ad

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        addDialog.setView(view)
        addDialog.setCancelable(false)
        addDialog.setPositiveButton("Ekle") {
                dialog, _ ->

            //if score not entered
            if (oyuncu1Skor!!.text.isEmpty()) {
                oyuncu1Skor!!.error = "Zorunlu"
                Toast.makeText(applicationContext, "Lütfen tüm oyuncuların skorlarını girin", Toast.LENGTH_SHORT).show()
            } else if (oyuncu2Skor!!.text.isEmpty()) {
                oyuncu2Skor!!.error = "Zorunlu"
                Toast.makeText(applicationContext, "Lütfen tüm oyuncuların skorlarını girin", Toast.LENGTH_SHORT).show()
            } else if (oyuncu3Skor!!.text.isEmpty()) {
                oyuncu3Skor!!.error = "Zorunlu"
                Toast.makeText(applicationContext, "Lütfen tüm oyuncuların skorlarını girin", Toast.LENGTH_SHORT).show()
            } else if (oyuncu4Skor!!.text.isEmpty()) {
                oyuncu4Skor!!.error = "Zorunlu"
                Toast.makeText(applicationContext, "Lütfen tüm oyuncuların skorlarını girin", Toast.LENGTH_SHORT).show()
            } else {

                if (gameType == "Sayı Ekle") {

                    //enterd scores to arraylist
                    val yeniAnlikSkor1 = oyuncu1Skor!!.text.toString()
                    val yeniAnlikSkor2 = oyuncu2Skor!!.text.toString()
                    val yeniAnlikSkor3 = oyuncu3Skor!!.text.toString()
                    val yeniAnlikSkor4 = oyuncu4Skor!!.text.toString()

                    val yeniAnlikSkor1Multiply = yeniAnlikSkor1.toInt() * multiplyNumber
                    val yeniAnlikSkor2Multiply = yeniAnlikSkor2.toInt() * multiplyNumber
                    val yeniAnlikSkor3Multiply = yeniAnlikSkor3.toInt() * multiplyNumber
                    val yeniAnlikSkor4Multiply = yeniAnlikSkor4.toInt() * multiplyNumber

                    skorList.add(SkorData4Kisi(yeniAnlikSkor1Multiply.toString(), yeniAnlikSkor2Multiply.toString(), yeniAnlikSkor3Multiply.toString(), yeniAnlikSkor4Multiply.toString(), gameNumber, multiplyNumber, color, false))

                    gameNumber++

                    binding.gameNumberText.text = "$gameNumber. El"

                    skorCount++

                    //instant scores
                    val eskiAnlikSkor1 = binding.oyuncu1AnlikSkor.text.toString()
                    val eskiAnlikSkor2 = binding.oyuncu2AnlikSkor.text.toString()
                    val eskiAnlikSkor3 = binding.oyuncu3AnlikSkor.text.toString()
                    val eskiAnlikSkor4 = binding.oyuncu4AnlikSkor.text.toString()

                    //sum of entered scores and instant scores
                    val sonucAnlikSkor1 = yeniAnlikSkor1Multiply + eskiAnlikSkor1.toInt()
                    val sonucAnlikSkor2 = yeniAnlikSkor2Multiply + eskiAnlikSkor2.toInt()
                    val sonucAnlikSkor3 = yeniAnlikSkor3Multiply + eskiAnlikSkor3.toInt()
                    val sonucAnlikSkor4 = yeniAnlikSkor4Multiply + eskiAnlikSkor4.toInt()

                    binding.oyuncu1AnlikSkor.text = sonucAnlikSkor1.toString()
                    binding.oyuncu2AnlikSkor.text = sonucAnlikSkor2.toString()
                    binding.oyuncu3AnlikSkor.text = sonucAnlikSkor3.toString()
                    binding.oyuncu4AnlikSkor.text = sonucAnlikSkor4.toString()

                    skorAdapter4Kisi.notifyDataSetChanged()

                } else {

                    //enterd scores to arraylist
                    val yeniAnlikSkor1 = oyuncu1Skor!!.text.toString()
                    val yeniAnlikSkor2 = oyuncu2Skor!!.text.toString()
                    val yeniAnlikSkor3 = oyuncu3Skor!!.text.toString()
                    val yeniAnlikSkor4 = oyuncu4Skor!!.text.toString()

                    val yeniAnlikSkor1Multiply = yeniAnlikSkor1.toInt() * multiplyNumber
                    val yeniAnlikSkor2Multiply = yeniAnlikSkor2.toInt() * multiplyNumber
                    val yeniAnlikSkor3Multiply = yeniAnlikSkor3.toInt() * multiplyNumber
                    val yeniAnlikSkor4Multiply = yeniAnlikSkor4.toInt() * multiplyNumber

                    skorList.add(SkorData4Kisi(yeniAnlikSkor1Multiply.toString(), yeniAnlikSkor2Multiply.toString(), yeniAnlikSkor3Multiply.toString(), yeniAnlikSkor4Multiply.toString(), gameNumber, multiplyNumber, color, false))

                    gameNumber++

                    binding.gameNumberText.text = "$gameNumber. El"

                    skorCount++

                    //instant scores
                    val eskiAnlikSkor1 = binding.oyuncu1AnlikSkor.text.toString()
                    val eskiAnlikSkor2 = binding.oyuncu2AnlikSkor.text.toString()
                    val eskiAnlikSkor3 = binding.oyuncu3AnlikSkor.text.toString()
                    val eskiAnlikSkor4 = binding.oyuncu4AnlikSkor.text.toString()

                    //sum of entered scores and instant scores
                    val sonucAnlikSkor1 = eskiAnlikSkor1.toInt() - yeniAnlikSkor1Multiply
                    val sonucAnlikSkor2 = eskiAnlikSkor2.toInt() - yeniAnlikSkor2Multiply
                    val sonucAnlikSkor3 = eskiAnlikSkor3.toInt() - yeniAnlikSkor3Multiply
                    val sonucAnlikSkor4 = eskiAnlikSkor4.toInt() - yeniAnlikSkor4Multiply

                    binding.oyuncu1AnlikSkor.text = sonucAnlikSkor1.toString()
                    binding.oyuncu2AnlikSkor.text = sonucAnlikSkor2.toString()
                    binding.oyuncu3AnlikSkor.text = sonucAnlikSkor3.toString()
                    binding.oyuncu4AnlikSkor.text = sonucAnlikSkor4.toString()

                    skorAdapter4Kisi.notifyDataSetChanged()

                }

                val score1 = binding.oyuncu1AnlikSkor.text.toString().toInt()
                val score2 = binding.oyuncu2AnlikSkor.text.toString().toInt()
                val score3 = binding.oyuncu3AnlikSkor.text.toString().toInt()
                val score4 = binding.oyuncu4AnlikSkor.text.toString().toInt()

                if (gameType == "Sayıdan Düş") { if (score1 <= 0 || score2 <= 0 || score3 <= 0 || score4 <= 0) { kazananTakim() } }

                dialog.dismiss()
            }

        }
        addDialog.setNegativeButton("İptal Et") {
                dialog, _ ->
            dialog.dismiss()
        }
        addDialog.create()
        addDialog.show()
    }


    //scoreboard
    @SuppressLint("NotifyDataSetChanged", "CutPasteId", "SetTextI18n")
    private fun puanTablosu() {

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.skor_list_4_kisi, null)

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        val kazananTakim = view.findViewById<TextView>(R.id.kazananOyuncular_text)

        val oyuncu1SkorText = view.findViewById<TextView>(R.id.oyuncu1Skor_textView)
        val oyuncu2SkorText = view.findViewById<TextView>(R.id.oyuncu2Skor_textView)
        val oyuncu3SkorText = view.findViewById<TextView>(R.id.oyuncu3Skor_textView)
        val oyuncu4SkorText = view.findViewById<TextView>(R.id.oyuncu4Skor_textView)

        val oyuncu1ToplamSkor = binding.oyuncu1AnlikSkor.text.toString()
        val oyuncu2ToplamSkor = binding.oyuncu2AnlikSkor.text.toString()
        val oyuncu3ToplamSkor = binding.oyuncu3AnlikSkor.text.toString()
        val oyuncu4ToplamSkor = binding.oyuncu4AnlikSkor.text.toString()

        oyuncu1SkorText.text = oyuncu1ToplamSkor
        oyuncu2SkorText.text = oyuncu2ToplamSkor
        oyuncu3SkorText.text = oyuncu3ToplamSkor
        oyuncu4SkorText.text = oyuncu4ToplamSkor

        val oyuncu1SkorTabloAdText = view.findViewById<TextView>(R.id.oyuncu1SkorTabloAd_textView)
        val oyuncu2SkorTabloAdText = view.findViewById<TextView>(R.id.oyuncu2SkorTabloAd_textView)
        val oyuncu3SkorTabloAdText = view.findViewById<TextView>(R.id.oyuncu3SkorTabloAd_textView)
        val oyuncu4SkorTabloAdText = view.findViewById<TextView>(R.id.oyuncu4SkorTabloAd_textView)

        oyuncu1SkorTabloAdText.text = oyuncu1Ad
        oyuncu2SkorTabloAdText.text = oyuncu2Ad
        oyuncu3SkorTabloAdText.text = oyuncu3Ad
        oyuncu4SkorTabloAdText.text = oyuncu4Ad

        //show the leading team
        val oyuncu1Skor = oyuncu1ToplamSkor.toInt()
        val oyuncu2Skor = oyuncu2ToplamSkor.toInt()
        val oyuncu3Skor = oyuncu3ToplamSkor.toInt()
        val oyuncu4Skor = oyuncu4ToplamSkor.toInt()

        when {
            ( (oyuncu1Skor < oyuncu2Skor) && (oyuncu1Skor < oyuncu3Skor) && (oyuncu1Skor < oyuncu4Skor) )-> {
                kazananTakim.text = "$oyuncu1Ad Önde."
            }
            ( (oyuncu2Skor < oyuncu1Skor) && (oyuncu2Skor < oyuncu3Skor) && (oyuncu2Skor < oyuncu4Skor) )-> {
                kazananTakim.text = "$oyuncu2Ad Önde."
            }
            ( (oyuncu3Skor < oyuncu1Skor) && (oyuncu3Skor < oyuncu2Skor) && (oyuncu3Skor < oyuncu4Skor) )-> {
                kazananTakim.text = "$oyuncu3Ad Önde."
            }
            ( (oyuncu4Skor < oyuncu1Skor) && (oyuncu4Skor < oyuncu2Skor) && (oyuncu4Skor < oyuncu3Skor) )-> {
                kazananTakim.text = "$oyuncu4Ad Önde."
            }
            else -> {
                kazananTakim.text = "Beraberlik"
            }
        }

        addDialog.setView(view)
        addDialog.setPositiveButton("Tamam") {
                dialog, _ ->
            dialog.dismiss()
        }
        addDialog.create()
        addDialog.show()
    }


    //scoreboard for winner team
    @SuppressLint("NotifyDataSetChanged", "CutPasteId", "SetTextI18n")
    private fun kazananTakim() {

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.skor_list_4_kisi, null)

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        val kazananTakim = view.findViewById<TextView>(R.id.kazananOyuncular_text)

        val oyuncu1SkorText = view.findViewById<TextView>(R.id.oyuncu1Skor_textView)
        val oyuncu2SkorText = view.findViewById<TextView>(R.id.oyuncu2Skor_textView)
        val oyuncu3SkorText = view.findViewById<TextView>(R.id.oyuncu3Skor_textView)
        val oyuncu4SkorText = view.findViewById<TextView>(R.id.oyuncu4Skor_textView)

        val oyuncu1ToplamSkor = binding.oyuncu1AnlikSkor.text.toString()
        val oyuncu2ToplamSkor = binding.oyuncu2AnlikSkor.text.toString()
        val oyuncu3ToplamSkor = binding.oyuncu3AnlikSkor.text.toString()
        val oyuncu4ToplamSkor = binding.oyuncu4AnlikSkor.text.toString()

        oyuncu1SkorText.text = oyuncu1ToplamSkor
        oyuncu2SkorText.text = oyuncu2ToplamSkor
        oyuncu3SkorText.text = oyuncu3ToplamSkor
        oyuncu4SkorText.text = oyuncu4ToplamSkor

        val oyuncu1SkorTabloAdText = view.findViewById<TextView>(R.id.oyuncu1SkorTabloAd_textView)
        val oyuncu2SkorTabloAdText = view.findViewById<TextView>(R.id.oyuncu2SkorTabloAd_textView)
        val oyuncu3SkorTabloAdText = view.findViewById<TextView>(R.id.oyuncu3SkorTabloAd_textView)
        val oyuncu4SkorTabloAdText = view.findViewById<TextView>(R.id.oyuncu4SkorTabloAd_textView)

        oyuncu1SkorTabloAdText.text = oyuncu1Ad
        oyuncu2SkorTabloAdText.text = oyuncu2Ad
        oyuncu3SkorTabloAdText.text = oyuncu3Ad
        oyuncu4SkorTabloAdText.text = oyuncu4Ad

        //show the leading team
        val oyuncu1Skor = oyuncu1ToplamSkor.toInt()
        val oyuncu2Skor = oyuncu2ToplamSkor.toInt()
        val oyuncu3Skor = oyuncu3ToplamSkor.toInt()
        val oyuncu4Skor = oyuncu4ToplamSkor.toInt()

        when {
            ( (oyuncu1Skor < oyuncu2Skor) && (oyuncu1Skor < oyuncu3Skor) && (oyuncu1Skor < oyuncu4Skor) )-> {
                kazananTakim.text = "$oyuncu1Ad Önde."
            }
            ( (oyuncu2Skor < oyuncu1Skor) && (oyuncu2Skor < oyuncu3Skor) && (oyuncu2Skor < oyuncu4Skor) )-> {
                kazananTakim.text = "$oyuncu2Ad Önde."
            }
            ( (oyuncu3Skor < oyuncu1Skor) && (oyuncu3Skor < oyuncu2Skor) && (oyuncu3Skor < oyuncu4Skor) )-> {
                kazananTakim.text = "$oyuncu3Ad Önde."
            }
            ( (oyuncu4Skor < oyuncu1Skor) && (oyuncu4Skor < oyuncu2Skor) && (oyuncu4Skor < oyuncu3Skor) )-> {
                kazananTakim.text = "$oyuncu4Ad Önde."
            }
            else -> {
                kazananTakim.text = "Beraberlik"
            }
        }

        addDialog.setView(view)
        addDialog.setPositiveButton("Yeni Oyun Başlat") {
                dialog, _ ->

            //turn back takimIslemleri for start a new game
            val intentTakimIslemleri = Intent(applicationContext, TakimIslemleri::class.java)
            startActivity(intentTakimIslemleri)
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

            dialog.dismiss()
        }

        //turn back main menu
        addDialog.setNegativeButton("Ana Menü") {
                dialog, _ ->

            val intentAnaMenu = Intent(applicationContext, AnaMenu::class.java)
            startActivity(intentAnaMenu)
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

            dialog.dismiss()

            dialog.dismiss()
        }
        addDialog.setCancelable(false)
        addDialog.create()
        addDialog.show()
    }


    //dice roller
    private fun diceRoller() {
        val intentDiceRoller = Intent(applicationContext, DiceRoller::class.java)
        startActivity(intentDiceRoller)
    }


    //open calculator
    private fun openCalculator() {
        val intentCalculator = Intent(applicationContext, Calculator::class.java)
        intentCalculator.putExtra("Puan Tablosu", 4)
        intentCalculator.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intentCalculator)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }


    //delete score
    @SuppressLint("NotifyDataSetChanged", "InflateParams", "SetTextI18n")
    private fun delete(position: Int) {

        if ( skorCount <= -1 ) {
            Toast.makeText(this, "Silinecek herhangi bir el yok!", Toast.LENGTH_SHORT).show()
        }

        else {

            if (gameType == "Sayı Ekle") {

                val toplamSkor1 = binding.oyuncu1AnlikSkor.text.toString()
                val toplamSkor2 = binding.oyuncu2AnlikSkor.text.toString()
                val toplamSkor3 = binding.oyuncu3AnlikSkor.text.toString()
                val toplamSkor4 = binding.oyuncu4AnlikSkor.text.toString()


                AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .setTitle("Seçili Eli Sil")
                    .setMessage("Seçili eli silmek istediğinizden emin misiniz?")
                    .setPositiveButton("Sil") {
                            dialog, _ ->
                        if(skorCount <= -1) {
                            Toast.makeText(this, "Silinecek herhangi bir el yok!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }

                        else {

                            gameNumber--

                            binding.gameNumberText.text = "$gameNumber. El"

                            val sonucSkor1 = toplamSkor1.toInt() - skorList[position].oyuncu1_skor.toInt()
                            val sonucSkor2 = toplamSkor2.toInt() - skorList[position].oyuncu2_skor.toInt()
                            val sonucSkor3 = toplamSkor3.toInt() - skorList[position].oyuncu3_skor.toInt()
                            val sonucSkor4 = toplamSkor4.toInt() - skorList[position].oyuncu4_skor.toInt()

                            binding.oyuncu1AnlikSkor.text = sonucSkor1.toString()
                            binding.oyuncu2AnlikSkor.text = sonucSkor2.toString()
                            binding.oyuncu3AnlikSkor.text = sonucSkor3.toString()
                            binding.oyuncu4AnlikSkor.text = sonucSkor4.toString()

                            skorList.removeAt(position)

                            skorCount--

                            skorAdapter4Kisi.notifyDataSetChanged()

                            val score1 = binding.oyuncu1AnlikSkor.text.toString().toInt()
                            val score2 = binding.oyuncu2AnlikSkor.text.toString().toInt()
                            val score3 = binding.oyuncu3AnlikSkor.text.toString().toInt()
                            val score4 = binding.oyuncu4AnlikSkor.text.toString().toInt()

                            if (gameType == "Sayıdan Düş") { if (score1 <= 0 || score2 <= 0 || score3 <= 0 || score4 <= 0) { kazananTakim() } }

                            dialog.dismiss()
                        }
                    }
                    .setNegativeButton("İptal Et") {
                            dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()

            } else {

                val toplamSkor1 = binding.oyuncu1AnlikSkor.text.toString()
                val toplamSkor2 = binding.oyuncu2AnlikSkor.text.toString()
                val toplamSkor3 = binding.oyuncu3AnlikSkor.text.toString()
                val toplamSkor4 = binding.oyuncu4AnlikSkor.text.toString()


                AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .setTitle("Seçili Eli Sil")
                    .setMessage("Seçili eli silmek istediğinizden emin misiniz?")
                    .setPositiveButton("Sil") {
                            dialog, _ ->
                        if(skorCount <= -1) {
                            Toast.makeText(this, "Silinecek herhangi bir el yok!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }

                        else {

                            gameNumber--

                            binding.gameNumberText.text = "$gameNumber. El"

                            val sonucSkor1 = toplamSkor1.toInt() + skorList[position].oyuncu1_skor.toInt()
                            val sonucSkor2 = toplamSkor2.toInt() + skorList[position].oyuncu2_skor.toInt()
                            val sonucSkor3 = toplamSkor3.toInt() + skorList[position].oyuncu3_skor.toInt()
                            val sonucSkor4 = toplamSkor4.toInt() + skorList[position].oyuncu4_skor.toInt()

                            binding.oyuncu1AnlikSkor.text = sonucSkor1.toString()
                            binding.oyuncu2AnlikSkor.text = sonucSkor2.toString()
                            binding.oyuncu3AnlikSkor.text = sonucSkor3.toString()
                            binding.oyuncu4AnlikSkor.text = sonucSkor4.toString()

                            skorList.removeAt(position)

                            skorCount--

                            skorAdapter4Kisi.notifyDataSetChanged()

                            val score1 = binding.oyuncu1AnlikSkor.text.toString().toInt()
                            val score2 = binding.oyuncu2AnlikSkor.text.toString().toInt()
                            val score3 = binding.oyuncu3AnlikSkor.text.toString().toInt()
                            val score4 = binding.oyuncu4AnlikSkor.text.toString().toInt()

                            if (gameType == "Sayıdan Düş") { if (score1 <= 0 || score2 <= 0 || score3 <= 0 || score4 <= 0) { kazananTakim() } }

                            dialog.dismiss()
                        }
                    }
                    .setNegativeButton("İptal Et") {
                            dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()

            }

        }

    }


    //onClick process
    private fun onClickProcess() {
        skorAdapter4Kisi.setOnItemClickListener(object : SkorAdapter4Kisi.OnItemClickListener {
            @SuppressLint("SetTextI18n")
            override fun onItemClick(position: Int) {

                isSelected = sharedPreferences.getBoolean("selected", false)
                clickCount = sharedPreferences.getInt("count", 0)

                //open score detail page
                scoreDetailPage(position)

            }
        })
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    private fun scoreDetailPage(position: Int) {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.skor_detay, null)


        //set linear layout visibility
        val linearLayout3 = view.findViewById<LinearLayout>(R.id.skorDetay3_linearLayout)
        val linearLayout4 = view.findViewById<LinearLayout>(R.id.skorDetay4_linearLayout)

        linearLayout3.visibility = View.VISIBLE
        linearLayout4.visibility = View.VISIBLE


        //set game number
        val gameNumberText = view.findViewById<TextView>(R.id.skorDetay_gameNumber_textView)

        gameNumberText.text = "${skorList[position].gameNumber}. El"


        //set player names
        val playerName1 = view.findViewById<TextView>(R.id.skorDetay_name_textView)
        val playerName2 = view.findViewById<TextView>(R.id.skorDetay_name2_textView)
        val playerName3 = view.findViewById<TextView>(R.id.skorDetay_name3_textView)
        val playerName4 = view.findViewById<TextView>(R.id.skorDetay_name4_textView)

        playerName1.text = "$oyuncu1Ad"
        playerName2.text = "$oyuncu2Ad"
        playerName3.text = "$oyuncu3Ad"
        playerName4.text = "$oyuncu4Ad"


        //set first score
        val firstScore1Text = view.findViewById<TextView>(R.id.skorDetay_score_textView)
        val firstScore2Text = view.findViewById<TextView>(R.id.skorDetay_score2_textView)
        val firstScore3Text = view.findViewById<TextView>(R.id.skorDetay_score3_textView)
        val firstScore4Text = view.findViewById<TextView>(R.id.skorDetay_score4_textView)

        firstScore1 = skorList[position].oyuncu1_skor.toInt() / skorList[position].multiplyNumber
        firstScore2 = skorList[position].oyuncu2_skor.toInt() / skorList[position].multiplyNumber
        firstScore3 = skorList[position].oyuncu3_skor.toInt() / skorList[position].multiplyNumber
        firstScore4 = skorList[position].oyuncu4_skor.toInt() / skorList[position].multiplyNumber

        firstScore1Text.text = firstScore1.toString()
        firstScore2Text.text = firstScore2.toString()
        firstScore3Text.text = firstScore3.toString()
        firstScore4Text.text = firstScore4.toString()


        //set colors and colors value
        val color = view.findViewById<CardView>(R.id.selectedColor)

        val colorValue1 = view.findViewById<TextView>(R.id.skorDetay_multiply_textView)
        val colorValue2 = view.findViewById<TextView>(R.id.skorDetay_multiply2_textView)
        val colorValue3 = view.findViewById<TextView>(R.id.skorDetay_multiply3_textView)
        val colorValue4 = view.findViewById<TextView>(R.id.skorDetay_multiply4_textView)

        colorValue1.text = skorList[position].multiplyNumber.toString()
        colorValue2.text = skorList[position].multiplyNumber.toString()
        colorValue3.text = skorList[position].multiplyNumber.toString()
        colorValue4.text = skorList[position].multiplyNumber.toString()

        when (skorList[position].color) {
            "White" -> {
                colorValue1.setTextColor(getColor(R.color.siyah_tas_color))
                colorValue2.setTextColor(getColor(R.color.siyah_tas_color))
                colorValue3.setTextColor(getColor(R.color.siyah_tas_color))
                colorValue4.setTextColor(getColor(R.color.siyah_tas_color))

                color.setCardBackgroundColor(getColor(R.color.skor_detay_beyaz_tas_color))
                color.visibility = View.GONE
            }
            "Red" -> {
                colorValue1.setTextColor(getColor(R.color.red))
                colorValue2.setTextColor(getColor(R.color.red))
                colorValue3.setTextColor(getColor(R.color.red))
                colorValue4.setTextColor(getColor(R.color.red))

                color.setCardBackgroundColor(getColor(R.color.red))
            }
            "Blue" -> {
                colorValue1.setTextColor(getColor(R.color.blue))
                colorValue2.setTextColor(getColor(R.color.blue))
                colorValue3.setTextColor(getColor(R.color.blue))
                colorValue4.setTextColor(getColor(R.color.blue))

                color.setCardBackgroundColor(getColor(R.color.blue))
            }
            "Yellow" -> {
                colorValue1.setTextColor(getColor(R.color.yellow))
                colorValue2.setTextColor(getColor(R.color.yellow))
                colorValue3.setTextColor(getColor(R.color.yellow))
                colorValue4.setTextColor(getColor(R.color.yellow))

                color.setCardBackgroundColor(getColor(R.color.yellow))
            }
            "Black" -> {
                colorValue1.setTextColor(getColor(R.color.siyah_tas_color))
                colorValue2.setTextColor(getColor(R.color.siyah_tas_color))
                colorValue3.setTextColor(getColor(R.color.siyah_tas_color))
                colorValue4.setTextColor(getColor(R.color.siyah_tas_color))

                color.setCardBackgroundColor(getColor(R.color.siyah_tas_color))
            }
        }


        //set multiply score
        val lastScore1 = view.findViewById<TextView>(R.id.skorDetay_toplamSkor_textView)
        val lastScore2 = view.findViewById<TextView>(R.id.skorDetay_toplamSkor2_textView)
        val lastScore3 = view.findViewById<TextView>(R.id.skorDetay_toplamSkor3_textView)
        val lastScore4 = view.findViewById<TextView>(R.id.skorDetay_toplamSkor4_textView)

        val result1 = firstScore1 * skorList[position].multiplyNumber
        val result2 = firstScore2 * skorList[position].multiplyNumber
        val result3 = firstScore3 * skorList[position].multiplyNumber
        val result4 = firstScore4 * skorList[position].multiplyNumber

        lastScore1.text = result1.toString()
        lastScore2.text = result2.toString()
        lastScore3.text = result3.toString()
        lastScore4.text = result4.toString()


        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        addDialog.setView(view)
        addDialog.setPositiveButton("Düzenle") {
                dialog, _ ->

            val selectedGameNumber = skorList[position].gameNumber
            editScore(position, selectedGameNumber)

            dialog.dismiss()
        }
        addDialog.setNegativeButton("Sil") {
                dialog, _ ->

            delete(position)

            dialog.dismiss()
        }
        addDialog.setNeutralButton("Tamam") {
                dialog, _ ->
            dialog.dismiss()
        }
        addDialog.create()
        addDialog.show()
    }


    //edit score
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n", "InflateParams")
    private fun editScore(position: Int, selectedGameNumber: Int) {

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.add_item_4_kisi, null)

        //set selected score
        val seciliSkor1 =  skorList[position].oyuncu1_skor
        val seciliSkor2 =  skorList[position].oyuncu2_skor
        val seciliSkor3 =  skorList[position].oyuncu3_skor
        val seciliSkor4 =  skorList[position].oyuncu4_skor

        //set oyuncuSkor view
        val oyuncu1Skor = view.findViewById<EditText>(R.id.oyuncu1Skor_editText)
        val oyuncu2Skor = view.findViewById<EditText>(R.id.oyuncu2Skor_editText)
        val oyuncu3Skor = view.findViewById<EditText>(R.id.oyuncu3Skor_editText)
        val oyuncu4Skor = view.findViewById<EditText>(R.id.oyuncu4Skor_editText)

        //set player names
        val oyuncu1Text = view.findViewById<TextView>(R.id.oyuncu1Ekle_textView)
        val oyuncu2Text = view.findViewById<TextView>(R.id.oyuncu2Ekle_textView)
        val oyuncu3Text = view.findViewById<TextView>(R.id.oyuncu3Ekle_textView)
        val oyuncu4Text = view.findViewById<TextView>(R.id.oyuncu4Ekle_textView)

        //set colors layout visibility
        val colorLayout = view.findViewById<RadioGroup>(R.id.colors_radioGroup)

        if (redValue == 1 && blueValue == 1 && yellowValue == 1 && blackValue == 1) {
            colorLayout.visibility = View.GONE
        }

        //set colors
        val noColorButton = view.findViewById<RadioButton>(R.id.noColor_radioButton)
        val redButton = view.findViewById<RadioButton>(R.id.red_radioButton)
        val blueButton = view.findViewById<RadioButton>(R.id.blue_radioButton)
        val yellowButton = view.findViewById<RadioButton>(R.id.yellow_radioButton)
        val blackButton = view.findViewById<RadioButton>(R.id.black_radioButton)

        //set multiply
        val cross = view.findViewById<LinearLayout>(R.id.cross_linearLayout)
        val multiply = view.findViewById<LinearLayout>(R.id.multiply_linearLayout)

        val multiply1 = view.findViewById<TextView>(R.id.multiply1_text)
        val multiply2 = view.findViewById<TextView>(R.id.multiply2_text)
        val multiply3 = view.findViewById<TextView>(R.id.multiply3_text)
        val multiply4 = view.findViewById<TextView>(R.id.multiply4_text)

        //set last color
        when (skorList[position].color) {
            "White" -> {
                noColorButton.isChecked = true
                redButton.isChecked = false
                blueButton.isChecked = false
                yellowButton.isChecked = false
                blackButton.isChecked = false

                multiplyNumber = 1

                cross.visibility = View.GONE
                multiply.visibility = View.GONE

                multiply1.text = multiplyNumber.toString()
                multiply2.text = multiplyNumber.toString()
                multiply3.text = multiplyNumber.toString()
                multiply4.text = multiplyNumber.toString()

                color = "White"
            }

            "Red" -> {
                redButton.isChecked = true
                noColorButton.isChecked = false
                blueButton.isChecked = false
                yellowButton.isChecked = false
                blackButton.isChecked = false

                multiplyNumber = redValue

                cross.visibility = View.VISIBLE
                multiply.visibility = View.VISIBLE

                multiply1.text = multiplyNumber.toString()
                multiply2.text = multiplyNumber.toString()
                multiply3.text = multiplyNumber.toString()
                multiply4.text = multiplyNumber.toString()

                color = "Red"
            }

            "Blue" -> {
                blueButton.isChecked = true
                noColorButton.isChecked = false
                redButton.isChecked = false
                yellowButton.isChecked = false
                blackButton.isChecked = false

                multiplyNumber = blueValue

                cross.visibility = View.VISIBLE
                multiply.visibility = View.VISIBLE

                multiply1.text = multiplyNumber.toString()
                multiply2.text = multiplyNumber.toString()
                multiply3.text = multiplyNumber.toString()
                multiply4.text = multiplyNumber.toString()

                color = "Blue"
            }

            "Yellow" -> {
                yellowButton.isChecked = true
                noColorButton.isChecked = false
                redButton.isChecked = false
                blueButton.isChecked = false
                blackButton.isChecked = false

                multiplyNumber = yellowValue

                cross.visibility = View.VISIBLE
                multiply.visibility = View.VISIBLE

                multiply1.text = multiplyNumber.toString()
                multiply2.text = multiplyNumber.toString()
                multiply3.text = multiplyNumber.toString()
                multiply4.text = multiplyNumber.toString()

                color = "Yellow"
            }

            "Black" -> {
                blackButton.isChecked = true
                noColorButton.isChecked = false
                redButton.isChecked = false
                blueButton.isChecked = false
                yellowButton.isChecked = false

                multiplyNumber = blackValue

                cross.visibility = View.VISIBLE
                multiply.visibility = View.VISIBLE

                multiply1.text = multiplyNumber.toString()
                multiply2.text = multiplyNumber.toString()
                multiply3.text = multiplyNumber.toString()
                multiply4.text = multiplyNumber.toString()

                color = "Black"
            }

        }

        //set visibility
        noColorButton.setOnClickListener {
            cross.visibility = View.GONE
            multiply.visibility = View.GONE

            multiplyNumber = 1

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()
            multiply3.text = multiplyNumber.toString()
            multiply4.text = multiplyNumber.toString()

            color = "White"
        }

        redButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = redValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()
            multiply3.text = multiplyNumber.toString()
            multiply4.text = multiplyNumber.toString()

            color = "Red"
        }

        blueButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = blueValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()
            multiply3.text = multiplyNumber.toString()
            multiply4.text = multiplyNumber.toString()

            color = "Blue"
        }

        yellowButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = yellowValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()
            multiply3.text = multiplyNumber.toString()
            multiply4.text = multiplyNumber.toString()

            color = "Yellow"
        }

        blackButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = blackValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()
            multiply3.text = multiplyNumber.toString()
            multiply4.text = multiplyNumber.toString()

            color = "Black"
        }

        oyuncu1Text.text = oyuncu1Ad
        oyuncu2Text.text = oyuncu2Ad
        oyuncu3Text.text = oyuncu3Ad
        oyuncu4Text.text = oyuncu4Ad

        oyuncu1Skor.setText(seciliSkor1)
        oyuncu2Skor.setText(seciliSkor2)
        oyuncu3Skor.setText(seciliSkor3)
        oyuncu4Skor.setText(seciliSkor4)

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        addDialog.setView(view)
        addDialog.setPositiveButton("Düzenle") {
                dialog, _ ->

            //if score not entered
            if (oyuncu1Skor!!.text.isEmpty()) {
                oyuncu1Skor.error = "Zorunlu"
                Toast.makeText(applicationContext, "Lütfen tüm oyuncuların skorlarını girin", Toast.LENGTH_SHORT).show()
            } else if (oyuncu2Skor!!.text.isEmpty()) {
                oyuncu2Skor.error = "Zorunlu"
                Toast.makeText(applicationContext, "Lütfen tüm oyuncuların skorlarını girin", Toast.LENGTH_SHORT).show()
            } else if (oyuncu3Skor!!.text.isEmpty()) {
                oyuncu3Skor.error = "Zorunlu"
                Toast.makeText(applicationContext, "Lütfen tüm oyuncuların skorlarını girin", Toast.LENGTH_SHORT).show()
            } else if (oyuncu4Skor!!.text.isEmpty()) {
                oyuncu4Skor.error = "Zorunlu"
                Toast.makeText(applicationContext, "Lütfen tüm oyuncuların skorlarını girin", Toast.LENGTH_SHORT).show()
            } else {

                if (gameType == "Sayı Ekle") {

                    //entered scores to arraylist
                    val yeniAnlikSkor1 = oyuncu1Skor.text.toString()
                    val yeniAnlikSkor2 = oyuncu2Skor.text.toString()
                    val yeniAnlikSkor3 = oyuncu3Skor.text.toString()
                    val yeniAnlikSkor4 = oyuncu4Skor.text.toString()

                    val yeniAnlikSkor1Multiply = yeniAnlikSkor1.toInt() * multiplyNumber
                    val yeniAnlikSkor2Multiply = yeniAnlikSkor2.toInt() * multiplyNumber
                    val yeniAnlikSkor3Multiply = yeniAnlikSkor3.toInt() * multiplyNumber
                    val yeniAnlikSkor4Multiply = yeniAnlikSkor4.toInt() * multiplyNumber

                    //set new score to arraylist
                    skorList[position].oyuncu1_skor = yeniAnlikSkor1Multiply.toString()
                    skorList[position].oyuncu2_skor = yeniAnlikSkor2Multiply.toString()
                    skorList[position].oyuncu3_skor = yeniAnlikSkor3Multiply.toString()
                    skorList[position].oyuncu4_skor = yeniAnlikSkor4Multiply.toString()
                    skorList[position].gameNumber = selectedGameNumber
                    skorList[position].multiplyNumber = multiplyNumber
                    skorList[position].color = color

                    binding.gameNumberText.text = "$gameNumber. El"

                    skorCount++

                    //instant score
                    val eskiAnlikSkor1 = binding.oyuncu1AnlikSkor.text.toString()
                    val eskiAnlikSkor2 = binding.oyuncu2AnlikSkor.text.toString()
                    val eskiAnlikSkor3 = binding.oyuncu3AnlikSkor.text.toString()
                    val eskiAnlikSkor4 = binding.oyuncu4AnlikSkor.text.toString()

                    //sum entered score and instant score
                    val sonucEskiAnlikSkor1 = eskiAnlikSkor1.toInt() - seciliSkor1.toInt()
                    val sonucEskiAnlikSkor2 = eskiAnlikSkor2.toInt() - seciliSkor2.toInt()
                    val sonucEskiAnlikSkor3 = eskiAnlikSkor3.toInt() - seciliSkor3.toInt()
                    val sonucEskiAnlikSkor4 = eskiAnlikSkor4.toInt() - seciliSkor4.toInt()

                    val sonucYeniAnlikSkor1 = sonucEskiAnlikSkor1 + yeniAnlikSkor1Multiply
                    val sonucYeniAnlikSkor2 = sonucEskiAnlikSkor2 + yeniAnlikSkor2Multiply
                    val sonucYeniAnlikSkor3 = sonucEskiAnlikSkor3 + yeniAnlikSkor3Multiply
                    val sonucYeniAnlikSkor4 = sonucEskiAnlikSkor4 + yeniAnlikSkor4Multiply

                    binding.oyuncu1AnlikSkor.text = sonucYeniAnlikSkor1.toString()
                    binding.oyuncu2AnlikSkor.text = sonucYeniAnlikSkor2.toString()
                    binding.oyuncu3AnlikSkor.text = sonucYeniAnlikSkor3.toString()
                    binding.oyuncu4AnlikSkor.text = sonucYeniAnlikSkor4.toString()

                    skorAdapter4Kisi.notifyDataSetChanged()

                } else {

                    //entered scores to arraylist
                    val yeniAnlikSkor1 = oyuncu1Skor.text.toString()
                    val yeniAnlikSkor2 = oyuncu2Skor.text.toString()
                    val yeniAnlikSkor3 = oyuncu3Skor.text.toString()
                    val yeniAnlikSkor4 = oyuncu4Skor.text.toString()

                    val yeniAnlikSkor1Multiply = yeniAnlikSkor1.toInt() * multiplyNumber
                    val yeniAnlikSkor2Multiply = yeniAnlikSkor2.toInt() * multiplyNumber
                    val yeniAnlikSkor3Multiply = yeniAnlikSkor3.toInt() * multiplyNumber
                    val yeniAnlikSkor4Multiply = yeniAnlikSkor4.toInt() * multiplyNumber

                    //set new score to arraylist
                    skorList[position].oyuncu1_skor = yeniAnlikSkor1Multiply.toString()
                    skorList[position].oyuncu2_skor = yeniAnlikSkor2Multiply.toString()
                    skorList[position].oyuncu3_skor = yeniAnlikSkor3Multiply.toString()
                    skorList[position].oyuncu4_skor = yeniAnlikSkor4Multiply.toString()
                    skorList[position].gameNumber = selectedGameNumber
                    skorList[position].multiplyNumber = multiplyNumber
                    skorList[position].color = color

                    binding.gameNumberText.text = "$gameNumber. El"

                    skorCount++

                    //instant score
                    val eskiAnlikSkor1 = binding.oyuncu1AnlikSkor.text.toString()
                    val eskiAnlikSkor2 = binding.oyuncu2AnlikSkor.text.toString()
                    val eskiAnlikSkor3 = binding.oyuncu3AnlikSkor.text.toString()
                    val eskiAnlikSkor4 = binding.oyuncu4AnlikSkor.text.toString()

                    //sum entered score and instant score
                    val sonucEskiAnlikSkor1 = eskiAnlikSkor1.toInt() + seciliSkor1.toInt()
                    val sonucEskiAnlikSkor2 = eskiAnlikSkor2.toInt() + seciliSkor2.toInt()
                    val sonucEskiAnlikSkor3 = eskiAnlikSkor3.toInt() + seciliSkor3.toInt()
                    val sonucEskiAnlikSkor4 = eskiAnlikSkor4.toInt() + seciliSkor4.toInt()

                    val sonucYeniAnlikSkor1 = sonucEskiAnlikSkor1 - yeniAnlikSkor1Multiply
                    val sonucYeniAnlikSkor2 = sonucEskiAnlikSkor2 - yeniAnlikSkor2Multiply
                    val sonucYeniAnlikSkor3 = sonucEskiAnlikSkor3 - yeniAnlikSkor3Multiply
                    val sonucYeniAnlikSkor4 = sonucEskiAnlikSkor4 - yeniAnlikSkor4Multiply

                    binding.oyuncu1AnlikSkor.text = sonucYeniAnlikSkor1.toString()
                    binding.oyuncu2AnlikSkor.text = sonucYeniAnlikSkor2.toString()
                    binding.oyuncu3AnlikSkor.text = sonucYeniAnlikSkor3.toString()
                    binding.oyuncu4AnlikSkor.text = sonucYeniAnlikSkor4.toString()

                    skorAdapter4Kisi.notifyDataSetChanged()

                }

                val score1 = binding.oyuncu1AnlikSkor.text.toString().toInt()
                val score2 = binding.oyuncu2AnlikSkor.text.toString().toInt()
                val score3 = binding.oyuncu3AnlikSkor.text.toString().toInt()
                val score4 = binding.oyuncu4AnlikSkor.text.toString().toInt()

                if (gameType == "Sayıdan Düş") { if (score1 <= 0 || score2 <= 0 || score3 <= 0 || score4 <= 0) { kazananTakim() } }

                dialog.dismiss()

            }

        }
        addDialog.setNegativeButton("İptal Et") {
                dialog, _ ->
            dialog.dismiss()
        }
        addDialog.setCancelable(false)
        addDialog.create()
        addDialog.show()
    }


    //exit main menu
    private fun exitMainMenu() {

        if (!isSelected) {

            val intentMain = Intent(applicationContext, AnaMenu::class.java)

            AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setTitle("Çıkış Yap")
                .setMessage("Çıkış yapmak istediğinizden emin misiniz?")
                .setPositiveButton("Kaydetmeden Çık") {
                        dialog, _ ->
                    startActivity(intentMain)
                    finish()
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

                    dialog.dismiss()
                }
                .setNegativeButton("İptal Et") {
                        dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .create()
                .show()

        } else {

            Toast.makeText(applicationContext, "Lütfen seçili skora basılı tutun!", Toast.LENGTH_SHORT).show()

        }

    }


    //save & exit
    private fun saveExit() {
        AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle("Oyunu Bitir")
            .setMessage("Oyunu bitirmek istediğinize emin misiniz?")
            .setPositiveButton("Oyunu Bitir") {
                    dialog, _ ->
                kazananTakim()
                dialog.dismiss()
            }
            .setNegativeButton("Geri Dön") {
                    dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()
    }


    //on back pressed main menu
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        if (!isSelected) {

            AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setTitle("Çıkış")
                .setMessage("Oyundan çıkmak istediğinize emin misiniz?")
                .setPositiveButton("Çıkış") {
                        dialog, _ ->

                    val intentMain = Intent(applicationContext, AnaMenu::class.java)
                    startActivity(intentMain)
                    finish()
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

                    dialog.dismiss()
                }
                .setNegativeButton("İptal") {
                        dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .create()
                .show()

        } else {

            Toast.makeText(applicationContext, "Lütfen seçili skora basılı tutun!", Toast.LENGTH_SHORT).show()

        }

    }

}