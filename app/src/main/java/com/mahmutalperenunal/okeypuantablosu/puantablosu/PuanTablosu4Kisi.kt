package com.mahmutalperenunal.okeypuantablosu.puantablosu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mahmutalperenunal.okeypuantablosu.R
import com.mahmutalperenunal.okeypuantablosu.databinding.ActivityPuanTablosu4KisiBinding
import com.mahmutalperenunal.okeypuantablosu.diceroller.DiceRoller
import com.mahmutalperenunal.okeypuantablosu.anamenu.AnaMenu
import com.mahmutalperenunal.okeypuantablosu.takimislemleri.TakimIslemleri
import com.mahmutalperenunal.okeypuantablosu.adapter.SkorAdapter4Kisi
import com.mahmutalperenunal.okeypuantablosu.data.SkorData4Kisi
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


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPuanTablosu4KisiBinding.inflate(layoutInflater)
        setContentView(binding.root)

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


        sharedPreferences = getSharedPreferences("clickCount4Kisi", Context.MODE_PRIVATE)


        //set list
        skorList = ArrayList()

        //set recyclerView
        recyclerView = findViewById(R.id.puanTablosu_recyclerView)

        //set adapter
        skorAdapter4Kisi = SkorAdapter4Kisi(skorList)

        //set recyclerView adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = skorAdapter4Kisi


        //edit process
        editProcess()


        //set skorEkle dialog
        binding.skorEkleButton.setOnClickListener { skorEkle() }

        //set delete dialog
        binding.deleteIcon.setOnClickListener { delete() }

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
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun skorEkle() {

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.add_item_4_kisi, null)

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

        oyuncu1Text.text = oyuncu1Ad
        oyuncu2Text.text = oyuncu2Ad
        oyuncu3Text.text = oyuncu3Ad
        oyuncu4Text.text = oyuncu4Ad

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        addDialog.setView(view)
        addDialog.setPositiveButton("Ekle") {
                dialog, _ ->

            //if score not entered
            if ( oyuncu1Skor!!.text.isEmpty() || oyuncu2Skor!!.text.isEmpty() || oyuncu3Skor!!.text.isEmpty() || oyuncu4Skor!!.text.isEmpty() ) {
                Toast.makeText(applicationContext, "Lütfen tüm oyuncuların skorlarını girin", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            //score entered
            else {

                //enterd scores to arraylist
                val yeniAnlikSkor1 = oyuncu1Skor!!.text.toString()
                val yeniAnlikSkor2 = oyuncu2Skor!!.text.toString()
                val yeniAnlikSkor3 = oyuncu3Skor!!.text.toString()
                val yeniAnlikSkor4 = oyuncu4Skor!!.text.toString()

                skorList.add(SkorData4Kisi(yeniAnlikSkor1, yeniAnlikSkor2, yeniAnlikSkor3, yeniAnlikSkor4, gameNumber))

                gameNumber++

                binding.gameNumberText.text = "$gameNumber. El"

                skorCount++

                //instant scores
                val eskiAnlikSkor1 = binding.oyuncu1AnlikSkor.text.toString()
                val eskiAnlikSkor2 = binding.oyuncu2AnlikSkor.text.toString()
                val eskiAnlikSkor3 = binding.oyuncu3AnlikSkor.text.toString()
                val eskiAnlikSkor4 = binding.oyuncu4AnlikSkor.text.toString()

                //sum of entered scores and instant scores
                val sonucAnlikSkor1 = yeniAnlikSkor1.toInt() + eskiAnlikSkor1.toInt()
                val sonucAnlikSkor2 = yeniAnlikSkor2.toInt() + eskiAnlikSkor2.toInt()
                val sonucAnlikSkor3 = yeniAnlikSkor3.toInt() + eskiAnlikSkor3.toInt()
                val sonucAnlikSkor4 = yeniAnlikSkor4.toInt() + eskiAnlikSkor4.toInt()

                binding.oyuncu1AnlikSkor.text = sonucAnlikSkor1.toString()
                binding.oyuncu2AnlikSkor.text = sonucAnlikSkor2.toString()
                binding.oyuncu3AnlikSkor.text = sonucAnlikSkor3.toString()
                binding.oyuncu4AnlikSkor.text = sonucAnlikSkor4.toString()

                skorAdapter4Kisi.notifyDataSetChanged()
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
            ( (oyuncu1Skor > oyuncu2Skor) && (oyuncu1Skor > oyuncu3Skor) && (oyuncu1Skor > oyuncu4Skor) )-> {
                kazananTakim.text = "$oyuncu1Ad Kazandı."
            }
            ( (oyuncu2Skor > oyuncu1Skor) && (oyuncu2Skor > oyuncu3Skor) && (oyuncu2Skor > oyuncu4Skor) )-> {
                kazananTakim.text = "$oyuncu2Ad Kazandı."
            }
            ( (oyuncu3Skor > oyuncu1Skor) && (oyuncu3Skor > oyuncu2Skor) && (oyuncu3Skor > oyuncu4Skor) )-> {
                kazananTakim.text = "$oyuncu3Ad Kazandı."
            }
            ( (oyuncu4Skor > oyuncu1Skor) && (oyuncu4Skor > oyuncu2Skor) && (oyuncu4Skor > oyuncu3Skor) )-> {
                kazananTakim.text = "$oyuncu4Ad Kazandı."
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
    private fun delete() {

        if ( skorCount <= -1 ) {
            Toast.makeText(this, "Silinecek herhangi bir el yok!", Toast.LENGTH_SHORT).show()
        }

        else {
            val sonSkor1 = oyuncu1Skor!!.text.toString()
            val sonSkor2 = oyuncu2Skor!!.text.toString()
            val sonSkor3 = oyuncu3Skor!!.text.toString()
            val sonSkor4 = oyuncu4Skor!!.text.toString()

            val toplamSkor1 = binding.oyuncu1AnlikSkor.text.toString()
            val toplamSkor2 = binding.oyuncu2AnlikSkor.text.toString()
            val toplamSkor3 = binding.oyuncu3AnlikSkor.text.toString()
            val toplamSkor4 = binding.oyuncu4AnlikSkor.text.toString()


            AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setTitle("Son Eli Sil")
                .setMessage("Son eli silmek istediğinizden emin misiniz?")
                .setPositiveButton("Sil") {
                        dialog, _ ->
                    if(skorCount <= -1) {
                        Toast.makeText(this, "Silinecek herhangi bir el yok!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }

                    else {

                        gameNumber--

                        binding.gameNumberText.text = "$gameNumber. El"

                        val sonucSkor1 = toplamSkor1.toInt() - sonSkor1.toInt()
                        val sonucSkor2 = toplamSkor2.toInt() - sonSkor2.toInt()
                        val sonucSkor3 = toplamSkor3.toInt() - sonSkor3.toInt()
                        val sonucSkor4 = toplamSkor4.toInt() - sonSkor4.toInt()

                        binding.oyuncu1AnlikSkor.text = sonucSkor1.toString()
                        binding.oyuncu2AnlikSkor.text = sonucSkor2.toString()
                        binding.oyuncu3AnlikSkor.text = sonucSkor3.toString()
                        binding.oyuncu4AnlikSkor.text = sonucSkor4.toString()

                        skorList.removeAt(skorCount)
                        skorCount--

                        skorAdapter4Kisi.notifyDataSetChanged()
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


    //edit process
    private fun editProcess() {
        skorAdapter4Kisi.setOnItemLongClickListener(object : SkorAdapter4Kisi.OnItemLongClickListener {
            @SuppressLint("SetTextI18n")
            override fun onItemLongClick(position: Int) {

                isSelected = sharedPreferences.getBoolean("selected", false)
                clickCount = sharedPreferences.getInt("count", 0)

                binding.baslikText.text = "1 İtem Seçili"
                binding.text.visibility = View.GONE
                binding.gameNumberText.visibility = View.GONE
                binding.backButton.visibility = View.VISIBLE
                binding.editIcon.visibility = View.VISIBLE
                binding.deleteIcon.visibility = View.GONE
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
                    binding.deleteIcon.visibility = View.VISIBLE
                    binding.diceIcon.visibility = View.VISIBLE
                    binding.calculatorIcon.visibility = View.VISIBLE
                }

                binding.editIcon.setOnClickListener {
                    val selectedGameNumber = skorList[position].gameNumber
                    editScore(position, selectedGameNumber)
                }

            }
        })
    }


    //edit score
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
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
            if ( oyuncu1Skor!!.text.isEmpty() || oyuncu2Skor!!.text.isEmpty() ) {
                Toast.makeText(applicationContext, "Lütfen tüm oyuncuların skorlarını girin", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            //score entered
            else {

                //entered scores to arraylist
                val yeniAnlikSkor1 = oyuncu1Skor.text.toString()
                val yeniAnlikSkor2 = oyuncu2Skor.text.toString()
                val yeniAnlikSkor3 = oyuncu3Skor.text.toString()
                val yeniAnlikSkor4 = oyuncu4Skor.text.toString()

                //set new score to arraylist
                skorList[position].oyuncu1_skor = yeniAnlikSkor1
                skorList[position].oyuncu2_skor = yeniAnlikSkor2
                skorList[position].oyuncu3_skor = yeniAnlikSkor3
                skorList[position].oyuncu4_skor = yeniAnlikSkor4
                skorList[position].gameNumber = selectedGameNumber

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

                val sonucYeniAnlikSkor1 = sonucEskiAnlikSkor1 + yeniAnlikSkor1.toInt()
                val sonucYeniAnlikSkor2 = sonucEskiAnlikSkor2 + yeniAnlikSkor2.toInt()
                val sonucYeniAnlikSkor3 = sonucEskiAnlikSkor3 + yeniAnlikSkor3.toInt()
                val sonucYeniAnlikSkor4 = sonucEskiAnlikSkor4 + yeniAnlikSkor4.toInt()

                binding.oyuncu1AnlikSkor.text = sonucYeniAnlikSkor1.toString()
                binding.oyuncu2AnlikSkor.text = sonucYeniAnlikSkor2.toString()
                binding.oyuncu3AnlikSkor.text = sonucYeniAnlikSkor3.toString()
                binding.oyuncu4AnlikSkor.text = sonucYeniAnlikSkor4.toString()

                skorAdapter4Kisi.notifyDataSetChanged()
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
                .create()
                .show()

        } else {

            Toast.makeText(applicationContext, "Lütfen seçili skora basılı tutun!", Toast.LENGTH_SHORT).show()

        }

    }
}