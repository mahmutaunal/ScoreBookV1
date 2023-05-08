package com.mahmutalperenunal.okeypuantablosu.puantablosu

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.mahmutalperenunal.okeypuantablosu.R
import com.mahmutalperenunal.okeypuantablosu.adapter.ScoreAdapter2Player
import com.mahmutalperenunal.okeypuantablosu.calculator.Calculator
import com.mahmutalperenunal.okeypuantablosu.databinding.ActivityScoreboard2PlayerBinding
import com.mahmutalperenunal.okeypuantablosu.diceroller.DiceRoller
import com.mahmutalperenunal.okeypuantablosu.mainmenu.MainMenu
import com.mahmutalperenunal.okeypuantablosu.model.ScoreData2Player
import com.mahmutalperenunal.okeypuantablosu.teamoperations.TeamOperations

//operations such as entering scores, deleting players.
class Scoreboard2Player : AppCompatActivity() {

    private lateinit var binding: ActivityScoreboard2PlayerBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var scoreList2Player: ArrayList<ScoreData2Player>
    private lateinit var scoreAdapter2Player: ScoreAdapter2Player

    private var scoreCount: Int = -1

    private var gameName: String? = null

    private var player1Name: String? = null
    private var player2Name: String? = null

    private var player1Score: EditText? = null
    private var player2Score: EditText? = null

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

    private var gameType: String = "Add Score"

    private var firstNumber: String = "0000"

    private var firstScore1: Int = 1
    private var firstScore2: Int = 1

    private lateinit var sharedPreferencesTheme: SharedPreferences


    @SuppressLint("SetTextI18n", "VisibleForTests", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreboard2PlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set screen orientation portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //set admob banner
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.takimIslemleriAdView.loadAd(adRequest)

        //game name
        gameName = intent.getStringExtra("Game Name").toString()

        //if game name not entered, subtitle is "New Game"
        if (gameName == "") {
            binding.baslikText.text = getString(R.string.new_game_text)
        } else {
            binding.baslikText.text = gameName
        }

        //get player names
        player1Name = intent.getStringExtra("Player-1 Name").toString()
        player2Name = intent.getStringExtra("Player-2 Name").toString()

        //set player names
        binding.oyuncu1Text.text = player1Name
        binding.oyuncu2Text.text = player2Name

        //get colors value
        redValue = intent.getIntExtra("Red Value", 0)
        blueValue = intent.getIntExtra("Blue Value", 0)
        yellowValue = intent.getIntExtra("Yellow Value", 0)
        blackValue = intent.getIntExtra("Black Value", 0)

        //get game type and first number
        gameType = intent.getStringExtra("Game Type").toString()
        firstNumber = intent.getStringExtra("Number of Starts").toString()

        if (firstNumber.isEmpty()) {
            binding.oyuncu1AnlikSkor.text = "0000"
            binding.oyuncu2AnlikSkor.text = "0000"
        } else {
            binding.oyuncu1AnlikSkor.text = firstNumber
            binding.oyuncu2AnlikSkor.text = firstNumber
        }


        //get click count
        sharedPreferences = getSharedPreferences("clickCount2Player", MODE_PRIVATE)

        //get theme
        sharedPreferencesTheme = getSharedPreferences("appTheme", MODE_PRIVATE)


        //set list
        scoreList2Player = ArrayList()

        //set recyclerView
        recyclerView = findViewById(R.id.puanTablosu_recyclerView)

        //set adapter
        scoreAdapter2Player = ScoreAdapter2Player(scoreList2Player)

        //set recyclerView adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = scoreAdapter2Player


        //onClick process
        onClickProcess()


        //set addScore dialog
        binding.skorEkleButton.setOnClickListener { addScore() }

        //set scoreboard dialog
        binding.skorTablosuButton.setOnClickListener { scoreboard() }

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
    private fun addScore() {

        multiplyNumber = 1

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.add_item_2_kisi, null)

        color = "White"

        //set playerScore view
        player1Score = view.findViewById(R.id.oyuncu1Skor_editText)
        player2Score = view.findViewById(R.id.oyuncu2Skor_editText)

        //set player names
        val player1Text = view.findViewById<TextView>(R.id.oyuncu1Ekle_textView)
        val player2Text = view.findViewById<TextView>(R.id.oyuncu2Ekle_textView)

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

        //set visibility
        noColorButton.setOnClickListener {
            cross.visibility = View.GONE
            multiply.visibility = View.GONE

            multiplyNumber = 1

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()

            color = "White"
        }

        redButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = redValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()

            color = "Red"
        }

        blueButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = blueValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()

            color = "Blue"
        }

        yellowButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = yellowValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()

            color = "Yellow"
        }

        blackButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = blackValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()

            color = "Black"
        }

        player1Text.text = player1Name
        player2Text.text = player2Name

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        addDialog.setView(view)
        addDialog.setCancelable(false)
        addDialog.setPositiveButton(R.string.add_text) { dialog, _ ->

            //if score not entered
            if (player1Score!!.text.isEmpty()) {
                player1Score!!.error = getString(R.string.compulsory_text)
                Toast.makeText(
                    applicationContext,
                    R.string.enter_all_scores_text,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (player2Score!!.text.isEmpty()) {
                player2Score!!.error = getString(R.string.compulsory_text)
                Toast.makeText(
                    applicationContext,
                    R.string.enter_all_scores_text,
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                if (gameType == "Add Score") {

                    //entered scores to arraylist
                    val newInstantScore1 = player1Score!!.text.toString()
                    val newInstantScore2 = player2Score!!.text.toString()

                    val newInstantScore1Multiply = newInstantScore1.toInt() * multiplyNumber
                    val newInstantScore2Multiply = newInstantScore2.toInt() * multiplyNumber

                    scoreList2Player.add(
                        ScoreData2Player(
                            newInstantScore1Multiply.toString(),
                            newInstantScore2Multiply.toString(),
                            gameNumber,
                            multiplyNumber,
                            color,
                            false
                        )
                    )

                    gameNumber++

                    binding.gameNumberText.text = "$gameNumber. ${getString(R.string.round_text)}"

                    scoreCount++

                    //instant score
                    val exInstantScore1 = binding.oyuncu1AnlikSkor.text.toString()
                    val exInstantScore2 = binding.oyuncu2AnlikSkor.text.toString()

                    //sum entered score and instant score
                    val resultInstantScore1 = newInstantScore1Multiply + exInstantScore1.toInt()
                    val resultInstantScore2 = newInstantScore2Multiply + exInstantScore2.toInt()

                    binding.oyuncu1AnlikSkor.text = resultInstantScore1.toString()
                    binding.oyuncu2AnlikSkor.text = resultInstantScore2.toString()

                    scoreAdapter2Player.notifyDataSetChanged()

                } else {

                    //entered scores to arraylist
                    val newInstantScore1 = player1Score!!.text.toString()
                    val newInstantScore2 = player2Score!!.text.toString()

                    val newInstantScore1Multiply = newInstantScore1.toInt() * multiplyNumber
                    val newInstantScore2Multiply = newInstantScore2.toInt() * multiplyNumber

                    scoreList2Player.add(
                        ScoreData2Player(
                            newInstantScore1Multiply.toString(),
                            newInstantScore2Multiply.toString(),
                            gameNumber,
                            multiplyNumber,
                            color,
                            false
                        )
                    )

                    gameNumber++

                    binding.gameNumberText.text = "$gameNumber. ${getString(R.string.round_text)}"

                    scoreCount++

                    //instant score
                    val exInstantScore1 = binding.oyuncu1AnlikSkor.text.toString()
                    val exInstantScore2 = binding.oyuncu2AnlikSkor.text.toString()

                    //sum entered score and instant score
                    val resultInstantScore1 = exInstantScore1.toInt() - newInstantScore1Multiply
                    val resultInstantScore2 = exInstantScore2.toInt() - newInstantScore2Multiply

                    binding.oyuncu1AnlikSkor.text = resultInstantScore1.toString()
                    binding.oyuncu2AnlikSkor.text = resultInstantScore2.toString()

                    scoreAdapter2Player.notifyDataSetChanged()

                }

                val score1 = binding.oyuncu1AnlikSkor.text.toString().toInt()
                val score2 = binding.oyuncu2AnlikSkor.text.toString().toInt()

                if (gameType == "Deduct from the number") {
                    if (score1 <= 0 || score2 <= 0) {
                        winnerTeam()
                    }
                }

                dialog.dismiss()
            }

        }
        addDialog.setNegativeButton(R.string.cancel_text) { dialog, _ ->
            dialog.dismiss()
        }
        addDialog.create()
        addDialog.show()
    }


    //scoreboard
    @SuppressLint("NotifyDataSetChanged", "CutPasteId", "SetTextI18n")
    private fun scoreboard() {

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.skor_list_2_kisi, null)

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        val winnerTeam = view.findViewById<TextView>(R.id.kazananOyuncular_text)

        val player1ScoreText = view.findViewById<TextView>(R.id.oyuncu1Skor_textView)
        val player2ScoreText = view.findViewById<TextView>(R.id.oyuncu2Skor_textView)

        val player1TotalScore = binding.oyuncu1AnlikSkor.text.toString()
        val player2TotalScore = binding.oyuncu2AnlikSkor.text.toString()

        player1ScoreText.text = player1TotalScore
        player2ScoreText.text = player2TotalScore

        val player1ScoreboardNameText =
            view.findViewById<TextView>(R.id.oyuncu1SkorTabloAd_textView)
        val player2ScoreboardNameText =
            view.findViewById<TextView>(R.id.oyuncu2SkorTabloAd_textView)

        player1ScoreboardNameText.text = player1Name
        player2ScoreboardNameText.text = player2Name

        //show the leading team
        val player1Score = player1TotalScore.toInt()
        val player2Score = player2TotalScore.toInt()

        when {
            ((player1Score < player2Score)) -> {
                winnerTeam.text = "$player1Name ${getString(R.string.ahead_text)}."
            }

            ((player2Score < player1Score)) -> {
                winnerTeam.text = "$player2Name ${getString(R.string.ahead_text)}."
            }

            else -> {
                winnerTeam.text = getString(R.string.tie_text)
            }
        }

        addDialog.setView(view)
        addDialog.setPositiveButton(R.string.ok_text) { dialog, _ ->
            dialog.dismiss()
        }
        addDialog.create()
        addDialog.show()
    }


    //winner team scoreboard
    @SuppressLint("NotifyDataSetChanged", "CutPasteId", "SetTextI18n")
    private fun winnerTeam() {

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.skor_list_2_kisi, null)

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        val winnerTeam = view.findViewById<TextView>(R.id.kazananOyuncular_text)

        val player1ScoreText = view.findViewById<TextView>(R.id.oyuncu1Skor_textView)
        val player2ScoreText = view.findViewById<TextView>(R.id.oyuncu2Skor_textView)

        val player1TotalScore = binding.oyuncu1AnlikSkor.text.toString()
        val player2TotalScore = binding.oyuncu2AnlikSkor.text.toString()

        player1ScoreText.text = player1TotalScore
        player2ScoreText.text = player2TotalScore

        val player1ScoreboardNameText =
            view.findViewById<TextView>(R.id.oyuncu1SkorTabloAd_textView)
        val player2ScoreboardNameText =
            view.findViewById<TextView>(R.id.oyuncu2SkorTabloAd_textView)

        player1ScoreboardNameText.text = player1Name
        player2ScoreboardNameText.text = player2Name

        //show the leading team
        val player1Score = player1TotalScore.toInt()
        val player2Score = player2TotalScore.toInt()

        when {
            ((player1Score < player2Score)) -> {
                winnerTeam.text = "$player1Name ${getString(R.string.won_text)}."
            }

            ((player2Score < player1Score)) -> {
                winnerTeam.text = "$player2Name ${getString(R.string.won_text)}."
            }

            else -> {
                winnerTeam.text = getString(R.string.tie_text)
            }
        }

        addDialog.setView(view)
        addDialog.setCancelable(false)
        addDialog.setPositiveButton(R.string.new_game_start_text) { dialog, _ ->

            //turn back teamOperations Activity for start a new game
            val intentTeamOperations = Intent(applicationContext, TeamOperations::class.java)
            startActivity(intentTeamOperations)
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

            dialog.dismiss()
        }

        //turn back main menu
        addDialog.setNegativeButton(R.string.main_menu_text) { dialog, _ ->

            val intentMainMenu = Intent(applicationContext, MainMenu::class.java)
            startActivity(intentMainMenu)
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
        intentCalculator.putExtra("Scoreboard", 2)
        intentCalculator.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intentCalculator)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }


    //delete score
    @SuppressLint("NotifyDataSetChanged", "InflateParams", "SetTextI18n")
    private fun delete(position: Int) {

        if (scoreCount <= -1) {
            Toast.makeText(this, R.string.no_round_to_delete_text, Toast.LENGTH_SHORT).show()
        } else {

            if (gameType == "Add Sore") {

                val totalScore1 = binding.oyuncu1AnlikSkor.text.toString()
                val totalScore2 = binding.oyuncu2AnlikSkor.text.toString()

                AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .setTitle(R.string.delete_round_text)
                    .setMessage(R.string.delete_round_description_text)
                    .setPositiveButton(R.string.delete_text) { dialog, _ ->
                        if (scoreCount <= -1) {
                            Toast.makeText(
                                this,
                                R.string.no_round_to_delete_text,
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                        } else {

                            gameNumber--

                            binding.gameNumberText.text =
                                "$gameNumber. ${getString(R.string.round_text)}"

                            val resultScore1 =
                                totalScore1.toInt() - scoreList2Player[position].player1_score.toInt()
                            val resultScore2 =
                                totalScore2.toInt() - scoreList2Player[position].player2_score.toInt()

                            binding.oyuncu1AnlikSkor.text = resultScore1.toString()
                            binding.oyuncu2AnlikSkor.text = resultScore2.toString()

                            scoreList2Player.removeAt(position)

                            scoreCount--

                            scoreAdapter2Player.notifyDataSetChanged()

                            val score1 = binding.oyuncu1AnlikSkor.text.toString().toInt()
                            val score2 = binding.oyuncu2AnlikSkor.text.toString().toInt()

                            if (gameType == "Deduct from the number") {
                                if (score1 <= 0 || score2 <= 0) {
                                    winnerTeam()
                                }
                            }

                            dialog.dismiss()
                        }
                    }
                    .setNegativeButton(R.string.cancel_text) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()

            } else {

                val totalScore1 = binding.oyuncu1AnlikSkor.text.toString()
                val totalScore2 = binding.oyuncu2AnlikSkor.text.toString()

                AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .setTitle(R.string.delete_round_text)
                    .setMessage(R.string.delete_round_description_text)
                    .setPositiveButton(R.string.delete_text) { dialog, _ ->
                        if (scoreCount <= -1) {
                            Toast.makeText(
                                this,
                                R.string.no_round_to_delete_text,
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                        } else {

                            gameNumber--

                            binding.gameNumberText.text =
                                "$gameNumber. ${getString(R.string.round_text)}."

                            val resultScore1 =
                                totalScore1.toInt() + scoreList2Player[position].player1_score.toInt()
                            val resultScore2 =
                                totalScore2.toInt() + scoreList2Player[position].player2_score.toInt()

                            binding.oyuncu1AnlikSkor.text = resultScore1.toString()
                            binding.oyuncu2AnlikSkor.text = resultScore2.toString()

                            scoreList2Player.removeAt(position)

                            scoreCount--

                            scoreAdapter2Player.notifyDataSetChanged()

                            val score1 = binding.oyuncu1AnlikSkor.text.toString().toInt()
                            val score2 = binding.oyuncu2AnlikSkor.text.toString().toInt()

                            if (gameType == "Deduct from the number") {
                                if (score1 <= 0 || score2 <= 0) {
                                    winnerTeam()
                                }
                            }

                            dialog.dismiss()
                        }
                    }
                    .setNegativeButton(R.string.cancel_text) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()

            }

        }

    }


    //onClick process
    private fun onClickProcess() {
        scoreAdapter2Player.setOnItemClickListener(object :
            ScoreAdapter2Player.OnItemClickListener {
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
        val linearLayout1 = view.findViewById<LinearLayout>(R.id.skorDetay3_linearLayout)
        val linearLayout2 = view.findViewById<LinearLayout>(R.id.skorDetay4_linearLayout)

        linearLayout1.visibility = View.GONE
        linearLayout2.visibility = View.GONE


        //set game number
        val gameNumberText = view.findViewById<TextView>(R.id.skorDetay_gameNumber_textView)

        gameNumberText.text =
            "${scoreList2Player[position].gameNumber}. ${getString(R.string.round_text)}"


        //set player names
        val playerName1 = view.findViewById<TextView>(R.id.skorDetay_name_textView)
        val playerName2 = view.findViewById<TextView>(R.id.skorDetay_name2_textView)

        playerName1.text = "$player1Name"
        playerName2.text = "$player2Name"


        //set first score
        val firstScore1Text = view.findViewById<TextView>(R.id.skorDetay_score_textView)
        val firstScore2Text = view.findViewById<TextView>(R.id.skorDetay_score2_textView)

        firstScore1 =
            scoreList2Player[position].player1_score.toInt() / scoreList2Player[position].multiplyNumber
        firstScore2 =
            scoreList2Player[position].player2_score.toInt() / scoreList2Player[position].multiplyNumber

        firstScore1Text.text = firstScore1.toString()
        firstScore2Text.text = firstScore2.toString()


        //set colors and colors value
        val color = view.findViewById<CardView>(R.id.selectedColor)

        val colorValue1 = view.findViewById<TextView>(R.id.skorDetay_multiply_textView)
        val colorValue2 = view.findViewById<TextView>(R.id.skorDetay_multiply2_textView)

        colorValue1.text = scoreList2Player[position].multiplyNumber.toString()
        colorValue2.text = scoreList2Player[position].multiplyNumber.toString()

        when (scoreList2Player[position].color) {
            "White" -> {
                colorValue1.setTextColor(getColor(R.color.siyah_tas_color))
                colorValue2.setTextColor(getColor(R.color.siyah_tas_color))

                color.setCardBackgroundColor(getColor(R.color.skor_detay_beyaz_tas_color))
                color.visibility = View.GONE
            }

            "Red" -> {
                colorValue1.setTextColor(getColor(R.color.red))
                colorValue2.setTextColor(getColor(R.color.red))

                color.setCardBackgroundColor(getColor(R.color.red))
            }

            "Blue" -> {
                colorValue1.setTextColor(getColor(R.color.blue))
                colorValue2.setTextColor(getColor(R.color.blue))

                color.setCardBackgroundColor(getColor(R.color.blue))
            }

            "Yellow" -> {
                colorValue1.setTextColor(getColor(R.color.yellow))
                colorValue2.setTextColor(getColor(R.color.yellow))

                color.setCardBackgroundColor(getColor(R.color.yellow))
            }

            "Black" -> {
                colorValue1.setTextColor(getColor(R.color.siyah_tas_color))
                colorValue2.setTextColor(getColor(R.color.siyah_tas_color))

                color.setCardBackgroundColor(getColor(R.color.siyah_tas_color))
            }
        }


        //set multiply score
        val lastScore1 = view.findViewById<TextView>(R.id.skorDetay_toplamSkor_textView)
        val lastScore2 = view.findViewById<TextView>(R.id.skorDetay_toplamSkor2_textView)

        val result1 = firstScore1 * scoreList2Player[position].multiplyNumber
        val result2 = firstScore2 * scoreList2Player[position].multiplyNumber

        lastScore1.text = result1.toString()
        lastScore2.text = result2.toString()


        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        addDialog.setView(view)
        addDialog.setPositiveButton(R.string.edit_text) { dialog, _ ->

            val selectedGameNumber = scoreList2Player[position].gameNumber
            editScore(position, selectedGameNumber)

            dialog.dismiss()
        }
        addDialog.setNegativeButton(R.string.delete_text) { dialog, _ ->

            delete(position)

            dialog.dismiss()
        }
        addDialog.setNeutralButton(R.string.cancel_text) { dialog, _ ->
            dialog.dismiss()
        }
        addDialog.create()
        addDialog.show()
    }


    //edit score
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n", "InflateParams")
    private fun editScore(position: Int, selectedGameNumber: Int) {

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.add_item_2_kisi, null)

        //set selected score
        val selectedScore1 = scoreList2Player[position].player1_score
        val selectedScore2 = scoreList2Player[position].player2_score

        //set playerScore view
        val player1Score = view.findViewById<EditText>(R.id.oyuncu1Skor_editText)
        val player2Score = view.findViewById<EditText>(R.id.oyuncu2Skor_editText)

        //set player names
        val player1Text = view.findViewById<TextView>(R.id.oyuncu1Ekle_textView)
        val player2Text = view.findViewById<TextView>(R.id.oyuncu2Ekle_textView)

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

        //set last color
        when (scoreList2Player[position].color) {
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

            color = "White"
        }

        redButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = redValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()

            color = "Red"
        }

        blueButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = blueValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()

            color = "Blue"
        }

        yellowButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = yellowValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()

            color = "Yellow"
        }

        blackButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = blackValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()

            color = "Black"
        }

        player1Text.text = player1Name
        player2Text.text = player2Name

        player1Score.setText(selectedScore1)
        player2Score.setText(selectedScore2)

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        addDialog.setView(view)
        addDialog.setCancelable(false)
        addDialog.setPositiveButton(R.string.edit_text) { dialog, _ ->

            //if score not entered
            if (player1Score!!.text.isEmpty()) {
                player1Score.error = getString(R.string.compulsory_text)
                Toast.makeText(
                    applicationContext,
                    R.string.enter_all_scores_text,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (player2Score!!.text.isEmpty()) {
                player2Score.error = getString(R.string.compulsory_text)
                Toast.makeText(
                    applicationContext,
                    R.string.enter_all_scores_text,
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                if (gameType == "Add Score") {

                    //entered scores to arraylist
                    val newInstantScore1 = player1Score.text.toString()
                    val newInstantScore2 = player2Score.text.toString()

                    val newInstantScore1Multiply = newInstantScore1.toInt() * multiplyNumber
                    val newInstantScore2Multiply = newInstantScore2.toInt() * multiplyNumber

                    //set new score to arraylist
                    scoreList2Player[position].player1_score = newInstantScore1Multiply.toString()
                    scoreList2Player[position].player2_score = newInstantScore2Multiply.toString()
                    scoreList2Player[position].gameNumber = selectedGameNumber
                    scoreList2Player[position].multiplyNumber = multiplyNumber
                    scoreList2Player[position].color = color

                    binding.gameNumberText.text = "$gameNumber. ${getString(R.string.round_text)}"

                    scoreCount++

                    //instant score
                    val exInstantScore1 = binding.oyuncu1AnlikSkor.text.toString()
                    val exInstantScore2 = binding.oyuncu2AnlikSkor.text.toString()

                    //sum entered score and instant score
                    val resultOldInstantScore1 = exInstantScore1.toInt() - selectedScore1.toInt()
                    val resultOldInstantScore2 = exInstantScore2.toInt() - selectedScore2.toInt()

                    val resultNewInstantScore1 = resultOldInstantScore1 + newInstantScore1Multiply
                    val resultNewInstantScore2 = resultOldInstantScore2 + newInstantScore2Multiply

                    binding.oyuncu1AnlikSkor.text = resultNewInstantScore1.toString()
                    binding.oyuncu2AnlikSkor.text = resultNewInstantScore2.toString()

                    scoreAdapter2Player.notifyDataSetChanged()

                } else {

                    //entered scores to arraylist
                    val newInstantScore1 = player1Score.text.toString()
                    val newInstantScore2 = player2Score.text.toString()

                    val newInstantScore1Multiply = newInstantScore1.toInt() * multiplyNumber
                    val newInstantScore2Multiply = newInstantScore2.toInt() * multiplyNumber

                    //set new score to arraylist
                    scoreList2Player[position].player1_score = newInstantScore1Multiply.toString()
                    scoreList2Player[position].player2_score = newInstantScore2Multiply.toString()
                    scoreList2Player[position].gameNumber = selectedGameNumber
                    scoreList2Player[position].multiplyNumber = multiplyNumber
                    scoreList2Player[position].color = color

                    binding.gameNumberText.text = "$gameNumber. ${getString(R.string.round_text)}"

                    scoreCount++

                    //instant score
                    val exInstantScore1 = binding.oyuncu1AnlikSkor.text.toString()
                    val exInstantScore2 = binding.oyuncu2AnlikSkor.text.toString()

                    //sum entered score and instant score
                    val resultOldInstantScore1 = exInstantScore1.toInt() + selectedScore1.toInt()
                    val resultOldInstantScore2 = exInstantScore2.toInt() + selectedScore2.toInt()

                    val resultNewInstantScore1 = resultOldInstantScore1 - newInstantScore1Multiply
                    val resultNewInstantScore2 = resultOldInstantScore2 - newInstantScore2Multiply

                    binding.oyuncu1AnlikSkor.text = resultNewInstantScore1.toString()
                    binding.oyuncu2AnlikSkor.text = resultNewInstantScore2.toString()

                    scoreAdapter2Player.notifyDataSetChanged()

                }

                val score1 = binding.oyuncu1AnlikSkor.text.toString().toInt()
                val score2 = binding.oyuncu2AnlikSkor.text.toString().toInt()

                if (gameType == "Deduct from the number") {
                    if (score1 <= 0 || score2 <= 0) {
                        winnerTeam()
                    }
                }

                dialog.dismiss()
            }

        }
        addDialog.setNegativeButton(R.string.cancel_text) { dialog, _ ->
            dialog.dismiss()
        }
        addDialog.create()
        addDialog.show()
    }


    //exit main menu
    private fun exitMainMenu() {

        if (!isSelected) {

            val intentMain = Intent(applicationContext, MainMenu::class.java)

            AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setTitle(R.string.exit_text)
                .setMessage(R.string.exit_description_text)
                .setPositiveButton(R.string.exit_without_save_text) { dialog, _ ->
                    startActivity(intentMain)
                    finish()
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel_text) { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .create()
                .show()

        }

    }


    //save & exit
    private fun saveExit() {
        AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle(R.string.finish_game_text)
            .setMessage(R.string.finish_game_description_text)
            .setPositiveButton(R.string.finish_game_text) { dialog, _ ->
                winnerTeam()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.back_text) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()
    }


    //on back pressed turn back main menu
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        if (!isSelected) {

            AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setTitle(R.string.exit_text)
                .setMessage(R.string.exit_description_text)
                .setPositiveButton(R.string.exit_text) { dialog, _ ->

                    val intentMain = Intent(applicationContext, MainMenu::class.java)
                    startActivity(intentMain)
                    finish()
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel_text) { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .create()
                .show()

        }

    }

}