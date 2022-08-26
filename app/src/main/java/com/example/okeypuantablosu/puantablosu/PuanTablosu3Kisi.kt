package com.example.okeypuantablosu.puantablosu

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.okeypuantablosu.diceroller.DiceRoller
import com.example.okeypuantablosu.anamenu.AnaMenuActivity
import com.example.okeypuantablosu.R
import com.example.okeypuantablosu.adapter.SkorAdapter3Kisi
import com.example.okeypuantablosu.takimislemleri.TakimIslemleri
import com.example.okeypuantablosu.data.SkorData3Kisi
import com.example.okeypuantablosu.databinding.ActivityPuanTablosu3KisiBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

//operations such as entering scores, deleting players.
class PuanTablosu3Kisi : AppCompatActivity() {

    private  lateinit var binding: ActivityPuanTablosu3KisiBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var skorList3Kisi: ArrayList<SkorData3Kisi>
    private  lateinit var skorAdapter3Kisi: SkorAdapter3Kisi

    private var skorCount: Int = -1

    private var oyunIsmi: String? = null

    private var oyuncu1Ad: String? = null
    private var oyuncu2Ad: String? = null
    private var oyuncu3Ad: String? = null

    private var oyuncu1Skor: EditText? = null
    private var oyuncu2Skor: EditText? = null
    private var oyuncu3Skor: EditText? = null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPuanTablosu3KisiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set admob banner
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.puanTablosu3AdView.loadAd(adRequest)

        //game name
        oyunIsmi = intent.getStringExtra("Oyun İsmi").toString()

        if ( oyunIsmi == "" ) {
            binding.baslikText.text = "Yeni Oyun"
        }
        else {
            binding.baslikText.text = oyunIsmi
        }

        //player name
        oyuncu1Ad = intent.getStringExtra("Oyuncu-1 Ad").toString()
        oyuncu2Ad = intent.getStringExtra("Oyuncu-2 Ad").toString()
        oyuncu3Ad = intent.getStringExtra("Oyuncu-3 Ad").toString()

        binding.oyuncu1Text.text = oyuncu1Ad
        binding.oyuncu2Text.text = oyuncu2Ad
        binding.oyuncu3Text.text = oyuncu3Ad


        //set list
        skorList3Kisi = ArrayList()

        //set recyclerView
        recyclerView = findViewById(R.id.puanTablosu_recyclerView)

        //set adapter
        skorAdapter3Kisi = SkorAdapter3Kisi(skorList3Kisi)

        //set recyclerView adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = skorAdapter3Kisi


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

        //save game
        binding.saveIcon.setOnClickListener { saveGame() }

        //dice roller
        binding.diceIcon.setOnClickListener { diceRoller() }
    }


    //add score
    @SuppressLint("NotifyDataSetChanged")
    private fun skorEkle() {

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.add_item_3_kisi, null)

        //set oyuncuSkor view
        oyuncu1Skor = view.findViewById(R.id.oyuncu1Skor_editText)
        oyuncu2Skor = view.findViewById(R.id.oyuncu2Skor_editText)
        oyuncu3Skor = view.findViewById(R.id.oyuncu3Skor_editText)

        //set player names
        val oyuncu1Text = view.findViewById<TextView>(R.id.oyuncu1Ekle_textView)
        val oyuncu2Text = view.findViewById<TextView>(R.id.oyuncu2Ekle_textView)
        val oyuncu3Text = view.findViewById<TextView>(R.id.oyuncu3Ekle_textView)

        oyuncu1Text.text = oyuncu1Ad
        oyuncu2Text.text = oyuncu2Ad
        oyuncu3Text.text = oyuncu3Ad

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        addDialog.setView(view)
        addDialog.setPositiveButton("Ekle") {
                dialog, _ ->

            //if score not entered
            if ( oyuncu1Skor!!.text.isEmpty() || oyuncu2Skor!!.text.isEmpty() || oyuncu3Skor!!.text.isEmpty() ) {
                Toast.makeText(applicationContext, "Lütfen tüm oyuncuların skorlarını girin", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            //score entered
            else {

                //entered scores to arraylist
                val yeniAnlikSkor1 = oyuncu1Skor!!.text.toString()
                val yeniAnlikSkor2 = oyuncu2Skor!!.text.toString()
                val yeniAnlikSkor3 = oyuncu3Skor!!.text.toString()

                skorList3Kisi.add(SkorData3Kisi(yeniAnlikSkor1, yeniAnlikSkor2, yeniAnlikSkor3))

                skorCount++

                //instant scores
                val eskiAnlikSkor1 = binding.oyuncu1AnlikSkor.text.toString()
                val eskiAnlikSkor2 = binding.oyuncu2AnlikSkor.text.toString()
                val eskiAnlikSkor3 = binding.oyuncu3AnlikSkor.text.toString()

                //sum of entered scores and instant scores
                val sonucAnlikSkor1 = yeniAnlikSkor1.toInt() + eskiAnlikSkor1.toInt()
                val sonucAnlikSkor2 = yeniAnlikSkor2.toInt() + eskiAnlikSkor2.toInt()
                val sonucAnlikSkor3 = yeniAnlikSkor3.toInt() + eskiAnlikSkor3.toInt()

                binding.oyuncu1AnlikSkor.text = sonucAnlikSkor1.toString()
                binding.oyuncu2AnlikSkor.text = sonucAnlikSkor2.toString()
                binding.oyuncu3AnlikSkor.text = sonucAnlikSkor3.toString()

                skorAdapter3Kisi.notifyDataSetChanged()
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
        val view = inflater.inflate(R.layout.skor_list_3_kisi, null)

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        val kazananTakim = view.findViewById<TextView>(R.id.kazananOyuncular_text)

        val oyuncu1SkorText = view.findViewById<TextView>(R.id.oyuncu1Skor_textView)
        val oyuncu2SkorText = view.findViewById<TextView>(R.id.oyuncu2Skor_textView)
        val oyuncu3SkorText = view.findViewById<TextView>(R.id.oyuncu3Skor_textView)

        val oyuncu1ToplamSkor = binding.oyuncu1AnlikSkor.text.toString()
        val oyuncu2ToplamSkor = binding.oyuncu2AnlikSkor.text.toString()
        val oyuncu3ToplamSkor = binding.oyuncu3AnlikSkor.text.toString()

        oyuncu1SkorText.text = oyuncu1ToplamSkor
        oyuncu2SkorText.text = oyuncu2ToplamSkor
        oyuncu3SkorText.text = oyuncu3ToplamSkor

        val oyuncu1SkorTabloAdText = view.findViewById<TextView>(R.id.oyuncu1SkorTabloAd_textView)
        val oyuncu2SkorTabloAdText = view.findViewById<TextView>(R.id.oyuncu2SkorTabloAd_textView)
        val oyuncu3SkorTabloAdText = view.findViewById<TextView>(R.id.oyuncu3SkorTabloAd_textView)

        oyuncu1SkorTabloAdText.text = oyuncu1Ad
        oyuncu2SkorTabloAdText.text = oyuncu2Ad
        oyuncu3SkorTabloAdText.text = oyuncu3Ad

        //show the leading team
        val oyuncu1Skor = oyuncu1ToplamSkor.toInt()
        val oyuncu2Skor = oyuncu2ToplamSkor.toInt()
        val oyuncu3Skor = oyuncu3ToplamSkor.toInt()

        when {
            ( (oyuncu1Skor > oyuncu2Skor) && (oyuncu1Skor > oyuncu3Skor) )-> {
                kazananTakim.text = "$oyuncu1Ad Önde."
            }
            ( (oyuncu2Skor > oyuncu1Skor) && (oyuncu2Skor > oyuncu3Skor) )-> {
                kazananTakim.text = "$oyuncu2Ad Önde."
            }
            ( (oyuncu3Skor > oyuncu1Skor) && (oyuncu3Skor > oyuncu2Skor) )-> {
                kazananTakim.text = "$oyuncu3Ad Önde."
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
        val view = inflater.inflate(R.layout.skor_list_3_kisi, null)

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        val kazananTakim = view.findViewById<TextView>(R.id.kazananOyuncular_text)

        val oyuncu1SkorText = view.findViewById<TextView>(R.id.oyuncu1Skor_textView)
        val oyuncu2SkorText = view.findViewById<TextView>(R.id.oyuncu2Skor_textView)
        val oyuncu3SkorText = view.findViewById<TextView>(R.id.oyuncu3Skor_textView)

        val oyuncu1ToplamSkor = binding.oyuncu1AnlikSkor.text.toString()
        val oyuncu2ToplamSkor = binding.oyuncu2AnlikSkor.text.toString()
        val oyuncu3ToplamSkor = binding.oyuncu3AnlikSkor.text.toString()

        oyuncu1SkorText.text = oyuncu1ToplamSkor
        oyuncu2SkorText.text = oyuncu2ToplamSkor
        oyuncu3SkorText.text = oyuncu3ToplamSkor

        val oyuncu1SkorTabloAdText = view.findViewById<TextView>(R.id.oyuncu1SkorTabloAd_textView)
        val oyuncu2SkorTabloAdText = view.findViewById<TextView>(R.id.oyuncu2SkorTabloAd_textView)
        val oyuncu3SkorTabloAdText = view.findViewById<TextView>(R.id.oyuncu3SkorTabloAd_textView)

        oyuncu1SkorTabloAdText.text = oyuncu1Ad
        oyuncu2SkorTabloAdText.text = oyuncu2Ad
        oyuncu3SkorTabloAdText.text = oyuncu3Ad

        //show the leading team
        val oyuncu1Skor = oyuncu1ToplamSkor.toInt()
        val oyuncu2Skor = oyuncu2ToplamSkor.toInt()
        val oyuncu3Skor = oyuncu3ToplamSkor.toInt()

        when {
            ( (oyuncu1Skor > oyuncu2Skor) && (oyuncu1Skor > oyuncu3Skor) )-> {
                kazananTakim.text = "$oyuncu1Ad Kazandı."
            }
            ( (oyuncu2Skor > oyuncu1Skor) && (oyuncu2Skor > oyuncu3Skor) )-> {
                kazananTakim.text = "$oyuncu2Ad Kazandı."
            }
            ( (oyuncu3Skor > oyuncu1Skor) && (oyuncu3Skor > oyuncu2Skor) )-> {
                kazananTakim.text = "$oyuncu3Ad Kazandı."
            }
            else -> {
                kazananTakim.text = "Beraberlik"
            }
        }

        addDialog.setView(view)
        addDialog.setPositiveButton("Yeni Oyun Başlat") {
                dialog, _ ->

            //turn back TakimIslemleri for start a new game
            val intentTakimIslemleri = Intent(applicationContext, TakimIslemleri::class.java)
            startActivity(intentTakimIslemleri)
            finish()

            //animation
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

            dialog.dismiss()
        }

        //turn back main menu
        addDialog.setNegativeButton("Ana Menü") {
                dialog, _ ->

            val intentAnaMenu = Intent(applicationContext, AnaMenuActivity::class.java)
            startActivity(intentAnaMenu)
            finish()

            //animation
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


    //save game
    private fun saveGame() {
        Toast.makeText(applicationContext, "Kaydetme özelliği geliştirilme aşamasındadır", Toast.LENGTH_SHORT).show()
    }


    //delete score
    @SuppressLint("NotifyDataSetChanged", "InflateParams")
    private fun delete() {

        if ( skorCount <= -1 ) {
            Toast.makeText(this, "Silinecek herhangi bir el yok!", Toast.LENGTH_SHORT).show()
        }

        else {
            val sonSkor1 = oyuncu1Skor!!.text.toString()
            val sonSkor2 = oyuncu2Skor!!.text.toString()
            val sonSkor3 = oyuncu3Skor!!.text.toString()

            val toplamSkor1 = binding.oyuncu1AnlikSkor.text.toString()
            val toplamSkor2 = binding.oyuncu2AnlikSkor.text.toString()
            val toplamSkor3 = binding.oyuncu3AnlikSkor.text.toString()

            AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setTitle("Son Eli Sil")
                .setMessage("Son eli silmek istediğinizden emin misiniz?")
                .setPositiveButton("Sil") {
                        dialog, _ ->
                    if(skorCount <= -1) {
                        Toast.makeText(this, "Silinecek herhangi bir el yok.", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    else {

                        val sonucSkor1 = toplamSkor1.toInt() - sonSkor1.toInt()
                        val sonucSkor2 = toplamSkor2.toInt() - sonSkor2.toInt()
                        val sonucSkor3 = toplamSkor3.toInt() - sonSkor3.toInt()

                        binding.oyuncu1AnlikSkor.text = sonucSkor1.toString()
                        binding.oyuncu2AnlikSkor.text = sonucSkor2.toString()
                        binding.oyuncu3AnlikSkor.text = sonucSkor3.toString()

                        skorList3Kisi.removeAt(skorCount)
                        skorCount--

                        skorAdapter3Kisi.notifyDataSetChanged()
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


    //exit main menu
    private fun exitMainMenu() {

        val intentMain = Intent(applicationContext, AnaMenuActivity::class.java)

        AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle("Çıkış Yap")
            .setMessage("Çıkış yapmak istediğinizden emin misiniz?")
            .setPositiveButton("Kaydetmeden Çık") {
                    dialog, _ ->
                startActivity(intentMain)
                finish()

                //animation
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

                dialog.dismiss()
            }
            .setNegativeButton("İptal Et") {
                    dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()

    }


    //save & and exit
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
    override fun onBackPressed() {
        AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle("Çıkış")
            .setMessage("Oyundan çıkmak istediğinize emin misiniz?")
            .setPositiveButton("Çıkış") {
                    dialog, _ ->

                val intentMain = Intent(applicationContext, AnaMenuActivity::class.java)
                startActivity(intentMain)
                finish()

                //animation
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

                dialog.dismiss()
            }
            .setNegativeButton("İptal") {
                    dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}