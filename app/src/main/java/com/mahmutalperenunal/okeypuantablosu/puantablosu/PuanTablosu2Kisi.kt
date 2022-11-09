package com.mahmutalperenunal.okeypuantablosu.puantablosu

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mahmutalperenunal.okeypuantablosu.R
import com.mahmutalperenunal.okeypuantablosu.databinding.ActivityPuanTablosu2KisiBinding
import com.mahmutalperenunal.okeypuantablosu.diceroller.DiceRoller
import com.mahmutalperenunal.okeypuantablosu.adapter.SkorAdapter2Kisi
import com.mahmutalperenunal.okeypuantablosu.anamenu.AnaMenu
import com.mahmutalperenunal.okeypuantablosu.data.SkorData2Kisi
import com.mahmutalperenunal.okeypuantablosu.takimislemleri.TakimIslemleri
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.mahmutalperenunal.okeypuantablosu.calculator.Calculator

//operations such as entering scores, deleting players.
class PuanTablosu2Kisi : AppCompatActivity() {

    private  lateinit var binding: ActivityPuanTablosu2KisiBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var skorList2Kisi: ArrayList<SkorData2Kisi>
    private  lateinit var skorAdapter2Kisi: SkorAdapter2Kisi

    private var skorCount: Int = -1

    private var oyunIsmi: String? = null

    private var oyuncu1Ad: String? = null
    private var oyuncu2Ad: String? = null

    private var oyuncu1Skor: EditText? = null
    private var oyuncu2Skor: EditText? = null

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


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPuanTablosu2KisiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set admob banner
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.takimIslemleriAdView.loadAd(adRequest)

        //game name
        oyunIsmi = intent.getStringExtra("Oyun İsmi").toString()

        //if game name not entered, subtitle is "Yeni Oyun"
        if ( oyunIsmi == "" ) {
            binding.baslikText.text = "Yeni Oyun"
        }
        else {
            binding.baslikText.text = oyunIsmi
        }

        //player names
        oyuncu1Ad = intent.getStringExtra("Oyuncu-1 Ad").toString()
        oyuncu2Ad = intent.getStringExtra("Oyuncu-2 Ad").toString()

        binding.oyuncu1Text.text = oyuncu1Ad
        binding.oyuncu2Text.text = oyuncu2Ad

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
        } else {
            binding.oyuncu1AnlikSkor.text = firstNumber
            binding.oyuncu2AnlikSkor.text = firstNumber
        }


        //get click count
        sharedPreferences = getSharedPreferences("clickCount2Kisi", MODE_PRIVATE)


        //set list
        skorList2Kisi = ArrayList()

        //set recyclerView
        recyclerView = findViewById(R.id.puanTablosu_recyclerView)

        //set adapter
        skorAdapter2Kisi = SkorAdapter2Kisi(skorList2Kisi)

        //set recyclerView adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = skorAdapter2Kisi


        //edit process
        editProcess()


        //set skorEkle dialog
        binding.skorEkleButton.setOnClickListener { skorEkle() }

        //set puanTablosu dialog
        binding.skorTablosuButton.setOnClickListener { puanTablosu() }

        //on back pressed turn main menu
        binding.backButton.setOnClickListener { exitMainMenu() }

        //exit game
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
        val view = inflater.inflate(R.layout.add_item_2_kisi, null)
        val view2 = inflater.inflate(R.layout.list_skor_2_kisi, null)

        //set selected color
        val colorBackground = view2.findViewById<TextView>(R.id.color_background)

        colorBackground.text = ""
        colorBackground.setTextColor(resources.getColor(R.color.white))

        color = "White"

        //set oyuncuSkor view
        oyuncu1Skor = view.findViewById(R.id.oyuncu1Skor_editText)
        oyuncu2Skor = view.findViewById(R.id.oyuncu2Skor_editText)

        //set player names
        val oyuncu1Text = view.findViewById<TextView>(R.id.oyuncu1Ekle_textView)
        val oyuncu2Text = view.findViewById<TextView>(R.id.oyuncu2Ekle_textView)

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

        //set visibility
        noColorButton.setOnClickListener {
            cross.visibility = View.GONE
            multiply.visibility = View.GONE

            multiplyNumber = 1

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()

            colorBackground.text = ""
            colorBackground.setTextColor(resources.getColor(R.color.white))

            color = "White"
        }

        redButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = redValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()

            colorBackground.text = "K"
            colorBackground.setTextColor(resources.getColor(R.color.red))

            color = "Red"
        }

        blueButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = blueValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()

            colorBackground.text = "M"
            colorBackground.setTextColor(resources.getColor(R.color.blue))

            color = "Blue"
        }

        yellowButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = yellowValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()

            colorBackground.text = "S"
            colorBackground.setTextColor(resources.getColor(R.color.yellow))

            color = "Yellow"
        }

        blackButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = blackValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()

            colorBackground.text = "S"
            colorBackground.setTextColor(resources.getColor(R.color.black))

            color = "Black"
        }

        oyuncu1Text.text = oyuncu1Ad
        oyuncu2Text.text = oyuncu2Ad

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        addDialog.setView(view)
        addDialog.setPositiveButton("Ekle") {
                dialog, _ ->

            //if score not entered
            if ( oyuncu1Skor!!.text.isEmpty() || oyuncu2Skor!!.text.isEmpty() ) {
                Toast.makeText(applicationContext, "Lütfen tüm oyuncuların skorlarını girin", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            //score entered
            else {

                if (gameType == "Sayı Ekle") {

                    //entered scores to arraylist
                    val yeniAnlikSkor1 = oyuncu1Skor!!.text.toString()
                    val yeniAnlikSkor2 = oyuncu2Skor!!.text.toString()

                    val yeniAnlikSkor1Multiply = yeniAnlikSkor1.toInt() * multiplyNumber
                    val yeniAnlikSkor2Multiply = yeniAnlikSkor2.toInt() * multiplyNumber

                    skorList2Kisi.add(SkorData2Kisi(yeniAnlikSkor1Multiply.toString(), yeniAnlikSkor2Multiply.toString(), gameNumber, color))

                    gameNumber++

                    binding.gameNumberText.text = "$gameNumber. El"

                    skorCount++

                    //instant score
                    val eskiAnlikSkor1 = binding.oyuncu1AnlikSkor.text.toString()
                    val eskiAnlikSkor2 = binding.oyuncu2AnlikSkor.text.toString()

                    //sum entered score and instant score
                    val sonucAnlikSkor1 = yeniAnlikSkor1Multiply + eskiAnlikSkor1.toInt()
                    val sonucAnlikSkor2 = yeniAnlikSkor2Multiply + eskiAnlikSkor2.toInt()

                    binding.oyuncu1AnlikSkor.text = sonucAnlikSkor1.toString()
                    binding.oyuncu2AnlikSkor.text = sonucAnlikSkor2.toString()

                    skorAdapter2Kisi.notifyDataSetChanged()

                } else {

                    //entered scores to arraylist
                    val yeniAnlikSkor1 = oyuncu1Skor!!.text.toString()
                    val yeniAnlikSkor2 = oyuncu2Skor!!.text.toString()

                    val yeniAnlikSkor1Multiply = yeniAnlikSkor1.toInt() * multiplyNumber
                    val yeniAnlikSkor2Multiply = yeniAnlikSkor2.toInt() * multiplyNumber

                    skorList2Kisi.add(SkorData2Kisi(yeniAnlikSkor1Multiply.toString(), yeniAnlikSkor2Multiply.toString(), gameNumber, color))

                    gameNumber++

                    binding.gameNumberText.text = "$gameNumber. El"

                    skorCount++

                    //instant score
                    val eskiAnlikSkor1 = binding.oyuncu1AnlikSkor.text.toString()
                    val eskiAnlikSkor2 = binding.oyuncu2AnlikSkor.text.toString()

                    //sum entered score and instant score
                    val sonucAnlikSkor1 = eskiAnlikSkor1.toInt() - yeniAnlikSkor1Multiply
                    val sonucAnlikSkor2 = eskiAnlikSkor2.toInt() - yeniAnlikSkor2Multiply

                    binding.oyuncu1AnlikSkor.text = sonucAnlikSkor1.toString()
                    binding.oyuncu2AnlikSkor.text = sonucAnlikSkor2.toString()

                    skorAdapter2Kisi.notifyDataSetChanged()

                }

                val score1 = binding.oyuncu1AnlikSkor.text.toString().toInt()
                val score2 = binding.oyuncu2AnlikSkor.text.toString().toInt()

                if (gameType == "Sayıdan Düş") { if (score1 <= 0 || score2 <= 0) { kazananTakim() } }

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
        val view = inflater.inflate(R.layout.skor_list_2_kisi, null)

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        val kazananTakim = view.findViewById<TextView>(R.id.kazananOyuncular_text)

        val oyuncu1SkorText = view.findViewById<TextView>(R.id.oyuncu1Skor_textView)
        val oyuncu2SkorText = view.findViewById<TextView>(R.id.oyuncu2Skor_textView)

        val oyuncu1ToplamSkor = binding.oyuncu1AnlikSkor.text.toString()
        val oyuncu2ToplamSkor = binding.oyuncu2AnlikSkor.text.toString()

        oyuncu1SkorText.text = oyuncu1ToplamSkor
        oyuncu2SkorText.text = oyuncu2ToplamSkor

        val oyuncu1SkorTabloAdText = view.findViewById<TextView>(R.id.oyuncu1SkorTabloAd_textView)
        val oyuncu2SkorTabloAdText = view.findViewById<TextView>(R.id.oyuncu2SkorTabloAd_textView)

        oyuncu1SkorTabloAdText.text = oyuncu1Ad
        oyuncu2SkorTabloAdText.text = oyuncu2Ad

        //show the leading team
        val oyuncu1Skor = oyuncu1ToplamSkor.toInt()
        val oyuncu2Skor = oyuncu2ToplamSkor.toInt()

        when {
            ( (oyuncu1Skor < oyuncu2Skor) )-> {
                kazananTakim.text = "$oyuncu1Ad Önde."
            }
            ( (oyuncu2Skor < oyuncu1Skor) )-> {
                kazananTakim.text = "$oyuncu2Ad Önde."
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


    //winner team scoreboard
    @SuppressLint("NotifyDataSetChanged", "CutPasteId", "SetTextI18n")
    private fun kazananTakim() {

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.skor_list_2_kisi, null)

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        val kazananTakim = view.findViewById<TextView>(R.id.kazananOyuncular_text)

        val oyuncu1SkorText = view.findViewById<TextView>(R.id.oyuncu1Skor_textView)
        val oyuncu2SkorText = view.findViewById<TextView>(R.id.oyuncu2Skor_textView)

        val oyuncu1ToplamSkor = binding.oyuncu1AnlikSkor.text.toString()
        val oyuncu2ToplamSkor = binding.oyuncu2AnlikSkor.text.toString()

        oyuncu1SkorText.text = oyuncu1ToplamSkor
        oyuncu2SkorText.text = oyuncu2ToplamSkor

        val oyuncu1SkorTabloAdText = view.findViewById<TextView>(R.id.oyuncu1SkorTabloAd_textView)
        val oyuncu2SkorTabloAdText = view.findViewById<TextView>(R.id.oyuncu2SkorTabloAd_textView)

        oyuncu1SkorTabloAdText.text = oyuncu1Ad
        oyuncu2SkorTabloAdText.text = oyuncu2Ad

        //show the leading team
        val oyuncu1Skor = oyuncu1ToplamSkor.toInt()
        val oyuncu2Skor = oyuncu2ToplamSkor.toInt()

        when {
            ( (oyuncu1Skor < oyuncu2Skor) )-> {
                kazananTakim.text = "$oyuncu1Ad Kazandı."
            }
            ( (oyuncu2Skor < oyuncu1Skor) )-> {
                kazananTakim.text = "$oyuncu2Ad Kazandı."
            }
            else -> {
                kazananTakim.text = "Beraberlik"
            }
        }

        addDialog.setView(view)
        addDialog.setPositiveButton("Yeni Oyun Başlat") {
                dialog, _ ->

            //turn back takimIslemleri Activity for start a new game
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
        intentCalculator.putExtra("Puan Tablosu", 2)
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

                AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .setTitle("Seçili Eli Sil")
                    .setMessage("Seçili eli silmek istediğinizden emin misiniz?")
                    .setPositiveButton("Sil") {
                            dialog, _ ->
                        if(skorCount <= -1) {
                            Toast.makeText(this, "Silinecek herhangi bir el yok.", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                        else {

                            gameNumber--

                            binding.gameNumberText.text = "$gameNumber. El"

                            val sonucSkor1 = toplamSkor1.toInt() - skorList2Kisi[position].oyuncu1_skor.toInt()
                            val sonucSkor2 = toplamSkor2.toInt() - skorList2Kisi[position].oyuncu2_skor.toInt()

                            binding.oyuncu1AnlikSkor.text = sonucSkor1.toString()
                            binding.oyuncu2AnlikSkor.text = sonucSkor2.toString()

                            skorList2Kisi.removeAt(position)

                            skorCount--

                            skorAdapter2Kisi.notifyDataSetChanged()

                            val score1 = binding.oyuncu1AnlikSkor.text.toString().toInt()
                            val score2 = binding.oyuncu2AnlikSkor.text.toString().toInt()

                            if (gameType == "Sayıdan Düş") { if (score1 <= 0 || score2 <= 0) { kazananTakim() } }

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

                AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .setTitle("Seçili Eli Sil")
                    .setMessage("Seçili eli silmek istediğinizden emin misiniz?")
                    .setPositiveButton("Sil") {
                            dialog, _ ->
                        if(skorCount <= -1) {
                            Toast.makeText(this, "Silinecek herhangi bir el yok.", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                        else {

                            gameNumber--

                            binding.gameNumberText.text = "$gameNumber. El"

                            val sonucSkor1 = toplamSkor1.toInt() + skorList2Kisi[position].oyuncu1_skor.toInt()
                            val sonucSkor2 = toplamSkor2.toInt() + skorList2Kisi[position].oyuncu2_skor.toInt()

                            binding.oyuncu1AnlikSkor.text = sonucSkor1.toString()
                            binding.oyuncu2AnlikSkor.text = sonucSkor2.toString()

                            skorList2Kisi.removeAt(position)

                            skorCount--

                            skorAdapter2Kisi.notifyDataSetChanged()

                            val score1 = binding.oyuncu1AnlikSkor.text.toString().toInt()
                            val score2 = binding.oyuncu2AnlikSkor.text.toString().toInt()

                            if (gameType == "Sayıdan Düş") { if (score1 <= 0 || score2 <= 0) { kazananTakim() } }

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


    //edit process
    private fun editProcess() {
        skorAdapter2Kisi.setOnItemLongClickListener(object : SkorAdapter2Kisi.OnItemLongClickListener {
            @SuppressLint("SetTextI18n", "InflateParams")
            override fun onItemLongClick(position: Int) {

                isSelected = sharedPreferences.getBoolean("selected", false)
                clickCount = sharedPreferences.getInt("count", 0)

                binding.baslikText.text = "1 İtem Seçili"
                binding.text.visibility = View.GONE
                binding.gameNumberText.visibility = View.GONE
                binding.backButton.visibility = View.VISIBLE
                binding.editIcon.visibility = View.VISIBLE
                binding.deleteIcon.visibility = View.VISIBLE
                binding.diceIcon.visibility = View.GONE
                binding.calculatorIcon.visibility = View.GONE

                if (clickCount == 0) {

                    if (oyunIsmi == "") {
                        binding.baslikText.text = "Yeni Oyun"
                    } else {
                        binding.baslikText.text = "$oyunIsmi"
                    }

                    binding.text.visibility = View.VISIBLE
                    binding.gameNumberText.visibility = View.VISIBLE
                    binding.backButton.visibility = View.VISIBLE
                    binding.editIcon.visibility = View.GONE
                    binding.deleteIcon.visibility = View.GONE
                    binding.diceIcon.visibility = View.VISIBLE
                    binding.calculatorIcon.visibility = View.VISIBLE
                    binding.skorEkleButton.visibility = View.VISIBLE
                    binding.skorTablosuButton.visibility = View.VISIBLE
                    binding.oyunuBitirButton.visibility = View.VISIBLE
                }

                //edit selected score
                binding.editIcon.setOnClickListener {
                    val selectedGameNumber = skorList2Kisi[position].gameNumber
                    editScore(position, selectedGameNumber)
                }

                //delete selected score
                binding.deleteIcon.setOnClickListener { delete(position) }

            }
        })
    }


    //edit score
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n", "InflateParams")
    private fun editScore(position: Int, selectedGameNumber: Int) {

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.add_item_2_kisi, null)
        val view2 = inflater.inflate(R.layout.list_skor_2_kisi, null)

        //set selected color
        val colorBackground = view2.findViewById<TextView>(R.id.color_background)

        //set selected score
        val seciliSkor1 =  skorList2Kisi[position].oyuncu1_skor
        val seciliSkor2 =  skorList2Kisi[position].oyuncu2_skor

        //set oyuncuSkor view
        val oyuncu1Skor = view.findViewById<EditText>(R.id.oyuncu1Skor_editText)
        val oyuncu2Skor = view.findViewById<EditText>(R.id.oyuncu2Skor_editText)

        //set player names
        val oyuncu1Text = view.findViewById<TextView>(R.id.oyuncu1Ekle_textView)
        val oyuncu2Text = view.findViewById<TextView>(R.id.oyuncu2Ekle_textView)

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

        //set last color
        when (skorList2Kisi[position].color) {
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

                colorBackground.text = ""

                colorBackground.setBackgroundColor(getColor(R.color.white))
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

                colorBackground.text = "K"

                colorBackground.setBackgroundColor(getColor(R.color.red))

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

                colorBackground.text = "M"

                colorBackground.setBackgroundColor(getColor(R.color.blue))

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

                colorBackground.text = "S"

                colorBackground.setBackgroundColor(getColor(R.color.yellow))

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

                colorBackground.text = "S"

                colorBackground.setBackgroundColor(getColor(R.color.black))

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

            colorBackground.text = ""

            colorBackground.setBackgroundColor(getColor(R.color.white))

            color = "White"
        }

        redButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = redValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()

            colorBackground.text = "K"

            colorBackground.setBackgroundColor(getColor(R.color.red))

            color = "Red"
        }

        blueButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = blueValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()

            colorBackground.text = "M"

            colorBackground.setBackgroundColor(getColor(R.color.blue))

            color = "Blue"
        }

        yellowButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = yellowValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()

            colorBackground.text = "S"

            colorBackground.setBackgroundColor(getColor(R.color.yellow))

            color = "Yellow"
        }

        blackButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = blackValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()

            colorBackground.text = "S"

            colorBackground.setBackgroundColor(getColor(R.color.black))

            color = "Black"
        }

        oyuncu1Text.text = oyuncu1Ad
        oyuncu2Text.text = oyuncu2Ad

        oyuncu1Skor.setText(seciliSkor1)
        oyuncu2Skor.setText(seciliSkor2)

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        addDialog.setView(view)
        addDialog.setPositiveButton("Düzenle") {
                dialog, _ ->

            //if score not entered
            if ( oyuncu1Skor!!.text.isEmpty() || oyuncu2Skor!!.text.isEmpty() ) {
                Toast.makeText(applicationContext, "Lütfen tüm oyuncuların skorlarını girin", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            //score entered
            else {

                if (gameType == "Sayı Ekle") {

                    //entered scores to arraylist
                    val yeniAnlikSkor1 = oyuncu1Skor.text.toString()
                    val yeniAnlikSkor2 = oyuncu2Skor.text.toString()

                    val yeniAnlikSkor1Multiply = yeniAnlikSkor1.toInt() * multiplyNumber
                    val yeniAnlikSkor2Multiply = yeniAnlikSkor2.toInt() * multiplyNumber

                    //set new score to arraylist
                    skorList2Kisi[position].oyuncu1_skor = yeniAnlikSkor1Multiply.toString()
                    skorList2Kisi[position].oyuncu2_skor = yeniAnlikSkor2Multiply.toString()
                    skorList2Kisi[position].gameNumber = selectedGameNumber

                    binding.gameNumberText.text = "$gameNumber. El"

                    skorCount++

                    //instant score
                    val eskiAnlikSkor1 = binding.oyuncu1AnlikSkor.text.toString()
                    val eskiAnlikSkor2 = binding.oyuncu2AnlikSkor.text.toString()

                    //sum entered score and instant score
                    val sonucEskiAnlikSkor1 = eskiAnlikSkor1.toInt() - seciliSkor1.toInt()
                    val sonucEskiAnlikSkor2 = eskiAnlikSkor2.toInt() - seciliSkor2.toInt()

                    val sonucYeniAnlikSkor1 = sonucEskiAnlikSkor1 + yeniAnlikSkor1Multiply
                    val sonucYeniAnlikSkor2 = sonucEskiAnlikSkor2 + yeniAnlikSkor2Multiply

                    binding.oyuncu1AnlikSkor.text = sonucYeniAnlikSkor1.toString()
                    binding.oyuncu2AnlikSkor.text = sonucYeniAnlikSkor2.toString()

                    skorAdapter2Kisi.notifyDataSetChanged()

                } else {

                    //entered scores to arraylist
                    val yeniAnlikSkor1 = oyuncu1Skor.text.toString()
                    val yeniAnlikSkor2 = oyuncu2Skor.text.toString()

                    val yeniAnlikSkor1Multiply = yeniAnlikSkor1.toInt() * multiplyNumber
                    val yeniAnlikSkor2Multiply = yeniAnlikSkor2.toInt() * multiplyNumber

                    //set new score to arraylist
                    skorList2Kisi[position].oyuncu1_skor = yeniAnlikSkor1Multiply.toString()
                    skorList2Kisi[position].oyuncu2_skor = yeniAnlikSkor2Multiply.toString()
                    skorList2Kisi[position].gameNumber = selectedGameNumber

                    binding.gameNumberText.text = "$gameNumber. El"

                    skorCount++

                    //instant score
                    val eskiAnlikSkor1 = binding.oyuncu1AnlikSkor.text.toString()
                    val eskiAnlikSkor2 = binding.oyuncu2AnlikSkor.text.toString()

                    //sum entered score and instant score
                    val sonucEskiAnlikSkor1 = eskiAnlikSkor1.toInt() + seciliSkor1.toInt()
                    val sonucEskiAnlikSkor2 = eskiAnlikSkor2.toInt() + seciliSkor2.toInt()

                    val sonucYeniAnlikSkor1 = sonucEskiAnlikSkor1 - yeniAnlikSkor1Multiply
                    val sonucYeniAnlikSkor2 = sonucEskiAnlikSkor2 - yeniAnlikSkor2Multiply

                    binding.oyuncu1AnlikSkor.text = sonucYeniAnlikSkor1.toString()
                    binding.oyuncu2AnlikSkor.text = sonucYeniAnlikSkor2.toString()

                    skorAdapter2Kisi.notifyDataSetChanged()

                }

                val score1 = binding.oyuncu1AnlikSkor.text.toString().toInt()
                val score2 = binding.oyuncu2AnlikSkor.text.toString().toInt()

                if (gameType == "Sayıdan Düş") { if (score1 <= 0 || score2 <= 0) { kazananTakim() } }

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
            .create()
            .show()
    }


    //on back pressed turn back main menu
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
                .create()
                .show()

        } else {

            Toast.makeText(applicationContext, "Lütfen seçili skora basılı tutun!", Toast.LENGTH_SHORT).show()

        }

    }

}