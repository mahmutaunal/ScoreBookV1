package com.mahmutalperenunal.okeypuantablosu.activity.scoreboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.mahmutalperenunal.okeypuantablosu.R
import com.mahmutalperenunal.okeypuantablosu.activity.Calculator
import com.mahmutalperenunal.okeypuantablosu.activity.DiceRoller
import com.mahmutalperenunal.okeypuantablosu.activity.MainMenu
import com.mahmutalperenunal.okeypuantablosu.activity.TeamOperations
import com.mahmutalperenunal.okeypuantablosu.adapter.ScoreAdapter3Player
import com.mahmutalperenunal.okeypuantablosu.databinding.ActivityScoreboard3PlayerBinding
import com.mahmutalperenunal.okeypuantablosu.model.ScoreData3Player

//operations such as entering scores, deleting players.
class Scoreboard3Player : AppCompatActivity() {

    private lateinit var binding: ActivityScoreboard3PlayerBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var scoreList3Player: ArrayList<ScoreData3Player>
    private lateinit var scoreAdapter3Player: ScoreAdapter3Player

    private var scoreCount: Int = -1

    private var gameName: String? = null

    private var player1Name: String? = null
    private var player2Name: String? = null
    private var player3Name: String? = null

    private var player1Score: EditText? = null
    private var player2Score: EditText? = null
    private var player3Score: EditText? = null

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
    private var firstScore3: Int = 1

    private lateinit var sharedPreferencesTheme: SharedPreferences


    @SuppressLint("SetTextI18n", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreboard3PlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //set admob banner
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.scoreBoard3PlayerAdView.loadAd(adRequest)

        //game name
        gameName = intent.getStringExtra("Game Name").toString()

        //set title
        if (gameName == "") {
            binding.scoreBoard3PlayerTitleText.text = getString(R.string.new_game_text)
        } else {
            binding.scoreBoard3PlayerTitleText.text = gameName
        }

        //player name
        player1Name = intent.getStringExtra("Player-1 Name").toString()
        player2Name = intent.getStringExtra("PLayer-2 Name").toString()
        player3Name = intent.getStringExtra("PLayer-3 Name").toString()

        binding.scoreBoard3PlayerPlayer1NameText.text = player1Name
        binding.scoreBoard3PlayerPlayer2NameText.text = player2Name
        binding.scoreBoard3PlayerPlayer3NameText.text = player3Name

        //get colors value
        redValue = intent.getIntExtra("Red Value", 0)
        blueValue = intent.getIntExtra("Blue Value", 0)
        yellowValue = intent.getIntExtra("Yellow Value", 0)
        blackValue = intent.getIntExtra("Black Value", 0)

        //get game type and first number
        gameType = intent.getStringExtra("Game Type").toString()
        firstNumber = intent.getStringExtra("Number of Starts").toString()

        if (firstNumber.isEmpty()) {
            binding.scoreBoard3PlayerPlayer1InstantScoreText.text = "0000"
            binding.scoreBoard3PlayerPlayer2InstantScoreText.text = "0000"
            binding.scoreBoard3PlayerPlayer3InstantScoreText.text = "0000"
        } else {
            binding.scoreBoard3PlayerPlayer1InstantScoreText.text = firstNumber
            binding.scoreBoard3PlayerPlayer2InstantScoreText.text = firstNumber
            binding.scoreBoard3PlayerPlayer3InstantScoreText.text = firstNumber
        }


        //get click count
        sharedPreferences = getSharedPreferences("clickCount3Player", Context.MODE_PRIVATE)

        //get theme
        sharedPreferencesTheme = getSharedPreferences("appTheme", MODE_PRIVATE)


        //set list
        scoreList3Player = ArrayList()

        //set recyclerView
        recyclerView = findViewById(R.id.scoreBoard3Player_recyclerView)

        //set adapter
        scoreAdapter3Player = ScoreAdapter3Player(scoreList3Player)

        //set recyclerView adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = scoreAdapter3Player


        //onClick process
        onClickProcess()


        //set addScore dialog
        binding.scoreBoard3PlayerAddScoreButton.setOnClickListener { addScore() }

        //set scoreboard dialog
        binding.scoreBoard3PlayerScoreboardButton.setOnClickListener { scoreboard() }

        //exit main menu
        binding.scoreBoard3PlayerBackButton.setOnClickListener { exitMainMenu() }

        //save & exit
        binding.scoreBoard3PlayerFinishGameButton.setOnClickListener { saveExit() }

        //dice roller
        binding.scoreBoard3PlayerDiceIcon.setOnClickListener { diceRoller() }

        //calculator
        binding.scoreBoard3PlayerCalculatorIcon.setOnClickListener { openCalculator() }
    }


    //add score
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n", "InflateParams")
    private fun addScore() {

        multiplyNumber = 1

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.add_score_3_player, null)

        color = "White"

        //set playerScore view
        player1Score = view.findViewById(R.id.addScore3Player_player1Score_editText)
        player2Score = view.findViewById(R.id.addScore3Player_player2Score_editText)
        player3Score = view.findViewById(R.id.addScore3Player_player3Score_editText)

        //set player names
        val player1Text = view.findViewById<TextView>(R.id.addScore3Player_player1Name_textView)
        val player2Text = view.findViewById<TextView>(R.id.addScore3Player_player2Name_textView)
        val player3Text = view.findViewById<TextView>(R.id.addScore3Player_player3Name_textView)

        //set colors layout visibility
        val colorLayout = view.findViewById<RadioGroup>(R.id.addScore3Player_colors_radioGroup)

        if (redValue == 1 && blueValue == 1 && yellowValue == 1 && blackValue == 1) {
            colorLayout.visibility = View.GONE
        }

        //set colors
        val noColorButton = view.findViewById<RadioButton>(R.id.addScore3Player_noColor_radioButton)
        val redButton = view.findViewById<RadioButton>(R.id.addScore3Player_red_radioButton)
        val blueButton = view.findViewById<RadioButton>(R.id.addScore3Player_blue_radioButton)
        val yellowButton = view.findViewById<RadioButton>(R.id.addScore3Player_yellow_radioButton)
        val blackButton = view.findViewById<RadioButton>(R.id.addScore3Player_black_radioButton)

        //set multiply
        val cross = view.findViewById<LinearLayout>(R.id.addScore3Player_cross_linearLayout)
        val multiply = view.findViewById<LinearLayout>(R.id.addScore3Player_multiply_linearLayout)

        val multiply1 = view.findViewById<TextView>(R.id.addScore3Player_multiplyPlayer1_text)
        val multiply2 = view.findViewById<TextView>(R.id.addScore3Player_multiplyPlayer2_text)
        val multiply3 = view.findViewById<TextView>(R.id.addScore3Player_multiplyPlayer3_text)

        //set visibility
        noColorButton.setOnClickListener {
            cross.visibility = View.GONE
            multiply.visibility = View.GONE

            multiplyNumber = 1

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()
            multiply3.text = multiplyNumber.toString()

            color = "White"
        }

        redButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = redValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()
            multiply3.text = multiplyNumber.toString()

            color = "Red"
        }

        blueButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = blueValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()
            multiply3.text = multiplyNumber.toString()

            color = "Blue"
        }

        yellowButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = yellowValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()
            multiply3.text = multiplyNumber.toString()

            color = "Yellow"
        }

        blackButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = blackValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()
            multiply3.text = multiplyNumber.toString()

            color = "Black"
        }

        player1Text.text = player1Name
        player2Text.text = player2Name
        player3Text.text = player3Name

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        addDialog.setView(view)
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
            } else if (player3Score!!.text.isEmpty()) {
                player3Score!!.error = getString(R.string.compulsory_text)
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
                    val newInstantScore3 = player3Score!!.text.toString()

                    val newInstantScore1Multiply = newInstantScore1.toInt() * multiplyNumber
                    val newInstantScore2Multiply = newInstantScore2.toInt() * multiplyNumber
                    val newInstantScore3Multiply = newInstantScore3.toInt() * multiplyNumber

                    scoreList3Player.add(
                        ScoreData3Player(
                            newInstantScore1Multiply.toString(),
                            newInstantScore2Multiply.toString(),
                            newInstantScore3Multiply.toString(),
                            gameNumber,
                            multiplyNumber,
                            color,
                            false
                        )
                    )

                    gameNumber++

                    binding.scoreBoard3PlayerRoundNumberText.text =
                        "$gameNumber. ${getString(R.string.round_text)}"

                    scoreCount++

                    //instant scores
                    val exInstantScore1 =
                        binding.scoreBoard3PlayerPlayer1InstantScoreText.text.toString()
                    val exInstantScore2 =
                        binding.scoreBoard3PlayerPlayer2InstantScoreText.text.toString()
                    val exInstantScore3 =
                        binding.scoreBoard3PlayerPlayer3InstantScoreText.text.toString()

                    //sum of entered scores and instant scores
                    val resultInstantScore1 = newInstantScore1Multiply + exInstantScore1.toInt()
                    val resultInstantScore2 = newInstantScore2Multiply + exInstantScore2.toInt()
                    val resultInstantScore3 = newInstantScore3Multiply + exInstantScore3.toInt()

                    binding.scoreBoard3PlayerPlayer1InstantScoreText.text =
                        resultInstantScore1.toString()
                    binding.scoreBoard3PlayerPlayer2InstantScoreText.text =
                        resultInstantScore2.toString()
                    binding.scoreBoard3PlayerPlayer3InstantScoreText.text =
                        resultInstantScore3.toString()

                    scoreAdapter3Player.notifyDataSetChanged()

                } else {

                    //entered scores to arraylist
                    val newInstantScore1 = player1Score!!.text.toString()
                    val newInstantScore2 = player2Score!!.text.toString()
                    val newInstantScore3 = player3Score!!.text.toString()

                    val newInstantScore1Multiply = newInstantScore1.toInt() * multiplyNumber
                    val newInstantScore2Multiply = newInstantScore2.toInt() * multiplyNumber
                    val newInstantScore3Multiply = newInstantScore3.toInt() * multiplyNumber

                    scoreList3Player.add(
                        ScoreData3Player(
                            newInstantScore1Multiply.toString(),
                            newInstantScore2Multiply.toString(),
                            newInstantScore3Multiply.toString(),
                            gameNumber,
                            multiplyNumber,
                            color,
                            false
                        )
                    )

                    gameNumber++

                    binding.scoreBoard3PlayerRoundNumberText.text =
                        "$gameNumber. ${getString(R.string.round_text)}"

                    scoreCount++

                    //instant scores
                    val exInstantScore1 =
                        binding.scoreBoard3PlayerPlayer1InstantScoreText.text.toString()
                    val exInstantScore2 =
                        binding.scoreBoard3PlayerPlayer2InstantScoreText.text.toString()
                    val exInstantScore3 =
                        binding.scoreBoard3PlayerPlayer3InstantScoreText.text.toString()

                    //sum of entered scores and instant scores
                    val resultInstantScore1 = exInstantScore1.toInt() - newInstantScore1Multiply
                    val resultInstantScore2 = exInstantScore2.toInt() - newInstantScore2Multiply
                    val resultInstantScore3 = exInstantScore3.toInt() - newInstantScore3Multiply

                    binding.scoreBoard3PlayerPlayer1InstantScoreText.text =
                        resultInstantScore1.toString()
                    binding.scoreBoard3PlayerPlayer2InstantScoreText.text =
                        resultInstantScore2.toString()
                    binding.scoreBoard3PlayerPlayer3InstantScoreText.text =
                        resultInstantScore3.toString()

                    scoreAdapter3Player.notifyDataSetChanged()

                }

                val score1 =
                    binding.scoreBoard3PlayerPlayer1InstantScoreText.text.toString().toInt()
                val score2 =
                    binding.scoreBoard3PlayerPlayer2InstantScoreText.text.toString().toInt()
                val score3 =
                    binding.scoreBoard3PlayerPlayer3InstantScoreText.text.toString().toInt()

                if (gameType == "Deduct from the number") {
                    if (score1 <= 0 || score2 <= 0 || score3 <= 0) {
                        winnerTeam()
                    }
                }

                dialog.dismiss()
            }

        }
        addDialog.setNegativeButton(R.string.cancel_text) { dialog, _ ->
            dialog.dismiss()
        }
        addDialog.setCancelable(false)
        addDialog.create()
        addDialog.show()
    }


    //scoreboard
    @SuppressLint("NotifyDataSetChanged", "CutPasteId", "SetTextI18n")
    private fun scoreboard() {

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.scoreboard_3_player, null)

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        val winnerTeam = view.findViewById<TextView>(R.id.kazananOyuncular_text)

        val player1ScoreText = view.findViewById<TextView>(R.id.oyuncu1Skor_textView)
        val player2ScoreText = view.findViewById<TextView>(R.id.oyuncu2Skor_textView)
        val player3ScoreText = view.findViewById<TextView>(R.id.oyuncu3Skor_textView)

        val player1TotalScore = binding.scoreBoard3PlayerPlayer1InstantScoreText.text.toString()
        val player2TotalScore = binding.scoreBoard3PlayerPlayer2InstantScoreText.text.toString()
        val player3TotalScore = binding.scoreBoard3PlayerPlayer3InstantScoreText.text.toString()

        player1ScoreText.text = player1TotalScore
        player2ScoreText.text = player2TotalScore
        player3ScoreText.text = player3TotalScore

        val player1scoreboardNameText =
            view.findViewById<TextView>(R.id.oyuncu1SkorTabloAd_textView)
        val player2scoreboardNameText =
            view.findViewById<TextView>(R.id.oyuncu2SkorTabloAd_textView)
        val player3scoreboardNameText =
            view.findViewById<TextView>(R.id.oyuncu3SkorTabloAd_textView)

        player1scoreboardNameText.text = player1Name
        player2scoreboardNameText.text = player2Name
        player3scoreboardNameText.text = player3Name

        //show the leading team
        val player1Score = player1TotalScore.toInt()
        val player2Score = player2TotalScore.toInt()
        val player3Score = player3TotalScore.toInt()

        when {
            ((player1Score < player2Score) && (player1Score < player3Score)) -> {
                winnerTeam.text = "$player1Name ${getString(R.string.ahead_text)}."
            }

            ((player2Score < player1Score) && (player2Score < player3Score)) -> {
                winnerTeam.text = "$player2Name ${getString(R.string.ahead_text)}."
            }

            ((player3Score < player1Score) && (player3Score < player2Score)) -> {
                winnerTeam.text = "$player3Name ${getString(R.string.ahead_text)}."
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


    //scoreboard for winner team
    @SuppressLint("NotifyDataSetChanged", "CutPasteId", "SetTextI18n")
    private fun winnerTeam() {

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.scoreboard_3_player, null)

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        val winnerTeam = view.findViewById<TextView>(R.id.kazananOyuncular_text)

        val player1ScoreText = view.findViewById<TextView>(R.id.oyuncu1Skor_textView)
        val player2ScoreText = view.findViewById<TextView>(R.id.oyuncu2Skor_textView)
        val player3ScoreText = view.findViewById<TextView>(R.id.oyuncu3Skor_textView)

        val player1TotalScore = binding.scoreBoard3PlayerPlayer1InstantScoreText.text.toString()
        val player2TotalScore = binding.scoreBoard3PlayerPlayer2InstantScoreText.text.toString()
        val player3TotalScore = binding.scoreBoard3PlayerPlayer3InstantScoreText.text.toString()

        player1ScoreText.text = player1TotalScore
        player2ScoreText.text = player2TotalScore
        player3ScoreText.text = player3TotalScore

        val player1ScoreboardNameText =
            view.findViewById<TextView>(R.id.oyuncu1SkorTabloAd_textView)
        val player2ScoreboardNameText =
            view.findViewById<TextView>(R.id.oyuncu2SkorTabloAd_textView)
        val player3ScoreboardNameText =
            view.findViewById<TextView>(R.id.oyuncu3SkorTabloAd_textView)

        player1ScoreboardNameText.text = player1Name
        player2ScoreboardNameText.text = player2Name
        player3ScoreboardNameText.text = player3Name

        //show the leading team
        val player1Score = player1TotalScore.toInt()
        val player2Score = player2TotalScore.toInt()
        val player3Score = player3TotalScore.toInt()

        when {
            ((player1Score < player2Score) && (player1Score < player3Score)) -> {
                winnerTeam.text = "$player1Name ${getString(R.string.won_text)}."
            }

            ((player2Score < player1Score) && (player2Score < player3Score)) -> {
                winnerTeam.text = "$player2Name ${getString(R.string.won_text)}."
            }

            ((player3Score < player1Score) && (player3Score < player2Score)) -> {
                winnerTeam.text = "$player3Name ${getString(R.string.won_text)}."
            }

            else -> {
                winnerTeam.text = getString(R.string.tie_text)
            }
        }

        addDialog.setView(view)
        addDialog.setPositiveButton(R.string.new_game_start_text) { dialog, _ ->

            //turn back TeamOperations for start a new game
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
        intentCalculator.putExtra("Scoreboard", 3)
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

            if (gameType == "Add Score") {

                val totalScore1 = binding.scoreBoard3PlayerPlayer1InstantScoreText.text.toString()
                val totalScore2 = binding.scoreBoard3PlayerPlayer2InstantScoreText.text.toString()
                val totalScore3 = binding.scoreBoard3PlayerPlayer3InstantScoreText.text.toString()

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

                            binding.scoreBoard3PlayerRoundNumberText.text =
                                "$gameNumber. ${getString(R.string.round_text)}"

                            val resultScore1 =
                                totalScore1.toInt() - scoreList3Player[position].player1_score.toInt()
                            val resultScore2 =
                                totalScore2.toInt() - scoreList3Player[position].player2_score.toInt()
                            val resultScore3 =
                                totalScore3.toInt() - scoreList3Player[position].player3_score.toInt()

                            binding.scoreBoard3PlayerPlayer1InstantScoreText.text =
                                resultScore1.toString()
                            binding.scoreBoard3PlayerPlayer2InstantScoreText.text =
                                resultScore2.toString()
                            binding.scoreBoard3PlayerPlayer3InstantScoreText.text =
                                resultScore3.toString()

                            scoreList3Player.removeAt(position)

                            scoreCount--

                            scoreAdapter3Player.notifyDataSetChanged()

                            val score1 =
                                binding.scoreBoard3PlayerPlayer1InstantScoreText.text.toString()
                                    .toInt()
                            val score2 =
                                binding.scoreBoard3PlayerPlayer2InstantScoreText.text.toString()
                                    .toInt()
                            val score3 =
                                binding.scoreBoard3PlayerPlayer3InstantScoreText.text.toString()
                                    .toInt()

                            if (gameType == "Deduct from the number") {
                                if (score1 <= 0 || score2 <= 0 || score3 <= 0) {
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

                val totalScore1 = binding.scoreBoard3PlayerPlayer1InstantScoreText.text.toString()
                val totalScore2 = binding.scoreBoard3PlayerPlayer2InstantScoreText.text.toString()
                val totalScore3 = binding.scoreBoard3PlayerPlayer3InstantScoreText.text.toString()

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

                            binding.scoreBoard3PlayerRoundNumberText.text =
                                "$gameNumber. ${getString(R.string.round_text)}"

                            val resultScore1 =
                                totalScore1.toInt() + scoreList3Player[position].player1_score.toInt()
                            val resultScore2 =
                                totalScore2.toInt() + scoreList3Player[position].player2_score.toInt()
                            val resultScore3 =
                                totalScore3.toInt() + scoreList3Player[position].player3_score.toInt()

                            binding.scoreBoard3PlayerPlayer1InstantScoreText.text =
                                resultScore1.toString()
                            binding.scoreBoard3PlayerPlayer2InstantScoreText.text =
                                resultScore2.toString()
                            binding.scoreBoard3PlayerPlayer3InstantScoreText.text =
                                resultScore3.toString()

                            scoreList3Player.removeAt(position)

                            scoreCount--

                            scoreAdapter3Player.notifyDataSetChanged()

                            val score1 =
                                binding.scoreBoard3PlayerPlayer1InstantScoreText.text.toString()
                                    .toInt()
                            val score2 =
                                binding.scoreBoard3PlayerPlayer2InstantScoreText.text.toString()
                                    .toInt()
                            val score3 =
                                binding.scoreBoard3PlayerPlayer3InstantScoreText.text.toString()
                                    .toInt()

                            if (gameType == "Deduct from the number") {
                                if (score1 <= 0 || score2 <= 0 || score3 <= 0) {
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
        scoreAdapter3Player.setOnItemClickListener(object :
            ScoreAdapter3Player.OnItemClickListener {
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
        val view = inflater.inflate(R.layout.detail_of_that_round_score, null)


        //set linear layout visibility
        val linearLayout3 = view.findViewById<LinearLayout>(R.id.scoreDetail_player3_linearLayout)
        val linearLayout4 = view.findViewById<LinearLayout>(R.id.scoreDetail_player4_linearLayout)

        linearLayout3.visibility = View.VISIBLE
        linearLayout4.visibility = View.GONE


        //set game number
        val gameNumberText =
            view.findViewById<TextView>(R.id.scoreDetail_selectedScoreGameNumber_textView)

        gameNumberText.text =
            "${scoreList3Player[position].gameNumber}. ${getString(R.string.round_text)}"


        //set player names
        val playerName1 = view.findViewById<TextView>(R.id.scoreDetail_player1Name_textView)
        val playerName2 = view.findViewById<TextView>(R.id.scoreDetail_player2Name_textView)
        val playerName3 = view.findViewById<TextView>(R.id.scoreDetail_player3Name_textView)

        playerName1.text = "$player1Name"
        playerName2.text = "$player2Name"
        playerName3.text = "$player3Name"


        //set first score
        val firstScore1Text = view.findViewById<TextView>(R.id.scoreDetail_player1Score_textView)
        val firstScore2Text = view.findViewById<TextView>(R.id.scoreDetail_player2Score_textView)
        val firstScore3Text = view.findViewById<TextView>(R.id.scoreDetail_player3Score_textView)

        firstScore1 =
            scoreList3Player[position].player1_score.toInt() / scoreList3Player[position].multiplyNumber
        firstScore2 =
            scoreList3Player[position].player2_score.toInt() / scoreList3Player[position].multiplyNumber
        firstScore3 =
            scoreList3Player[position].player3_score.toInt() / scoreList3Player[position].multiplyNumber

        firstScore1Text.text = firstScore1.toString()
        firstScore2Text.text = firstScore2.toString()
        firstScore3Text.text = firstScore3.toString()


        //set colors and colors value
        val color = view.findViewById<CardView>(R.id.scoreDetail_selectedRoundColor)

        val colorValue1 = view.findViewById<TextView>(R.id.scoreDetail_multiplyPlayer1_textView)
        val colorValue2 = view.findViewById<TextView>(R.id.scoreDetail_multiplyPlayer2_textView)
        val colorValue3 = view.findViewById<TextView>(R.id.scoreDetail_multiplyPlayer3_textView)

        colorValue1.text = scoreList3Player[position].multiplyNumber.toString()
        colorValue2.text = scoreList3Player[position].multiplyNumber.toString()
        colorValue3.text = scoreList3Player[position].multiplyNumber.toString()

        when (scoreList3Player[position].color) {
            "White" -> {
                colorValue1.setTextColor(getColor(R.color.siyah_tas_color))
                colorValue2.setTextColor(getColor(R.color.siyah_tas_color))
                colorValue3.setTextColor(getColor(R.color.siyah_tas_color))

                color.setCardBackgroundColor(getColor(R.color.skor_detay_beyaz_tas_color))
                color.visibility = View.GONE
            }

            "Red" -> {
                colorValue1.setTextColor(getColor(R.color.red))
                colorValue2.setTextColor(getColor(R.color.red))
                colorValue3.setTextColor(getColor(R.color.red))

                color.setCardBackgroundColor(getColor(R.color.red))
            }

            "Blue" -> {
                colorValue1.setTextColor(getColor(R.color.blue))
                colorValue2.setTextColor(getColor(R.color.blue))
                colorValue3.setTextColor(getColor(R.color.blue))

                color.setCardBackgroundColor(getColor(R.color.blue))
            }

            "Yellow" -> {
                colorValue1.setTextColor(getColor(R.color.yellow))
                colorValue2.setTextColor(getColor(R.color.yellow))
                colorValue3.setTextColor(getColor(R.color.yellow))

                color.setCardBackgroundColor(getColor(R.color.yellow))
            }

            "Black" -> {
                colorValue1.setTextColor(getColor(R.color.siyah_tas_color))
                colorValue2.setTextColor(getColor(R.color.siyah_tas_color))
                colorValue3.setTextColor(getColor(R.color.siyah_tas_color))

                color.setCardBackgroundColor(getColor(R.color.siyah_tas_color))
            }
        }


        //set multiply score
        val lastScore1 = view.findViewById<TextView>(R.id.scoreDetail_totalScorePlayer1_textView)
        val lastScore2 = view.findViewById<TextView>(R.id.scoreDetail_totalScorePlayer2_textView)
        val lastScore3 = view.findViewById<TextView>(R.id.scoreDetail_totalScorePlayer3_textView)

        val result1 = firstScore1 * scoreList3Player[position].multiplyNumber
        val result2 = firstScore2 * scoreList3Player[position].multiplyNumber
        val result3 = firstScore3 * scoreList3Player[position].multiplyNumber

        lastScore1.text = result1.toString()
        lastScore2.text = result2.toString()
        lastScore3.text = result3.toString()


        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        addDialog.setView(view)
        addDialog.setPositiveButton(R.string.edit_text) { dialog, _ ->

            val selectedGameNumber = scoreList3Player[position].gameNumber
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
        val view = inflater.inflate(R.layout.add_score_3_player, null)

        //set selected score
        val selectedScore1 = scoreList3Player[position].player1_score
        val selectedScore2 = scoreList3Player[position].player2_score
        val selectedScore3 = scoreList3Player[position].player3_score

        //set playerScore view
        val player1Score = view.findViewById<EditText>(R.id.addScore3Player_player1Score_editText)
        val player2Score = view.findViewById<EditText>(R.id.addScore3Player_player2Score_editText)
        val player3Score = view.findViewById<EditText>(R.id.addScore3Player_player3Score_editText)

        //set player names
        val player1Text = view.findViewById<TextView>(R.id.addScore3Player_player1Name_textView)
        val player2Text = view.findViewById<TextView>(R.id.addScore3Player_player2Name_textView)
        val player3Text = view.findViewById<TextView>(R.id.addScore3Player_player3Name_textView)

        //set colors layout visibility
        val colorLayout = view.findViewById<RadioGroup>(R.id.addScore3Player_colors_radioGroup)

        if (redValue == 1 && blueValue == 1 && yellowValue == 1 && blackValue == 1) {
            colorLayout.visibility = View.GONE
        }

        //set colors
        val noColorButton = view.findViewById<RadioButton>(R.id.addScore3Player_noColor_radioButton)
        val redButton = view.findViewById<RadioButton>(R.id.addScore3Player_red_radioButton)
        val blueButton = view.findViewById<RadioButton>(R.id.addScore3Player_blue_radioButton)
        val yellowButton = view.findViewById<RadioButton>(R.id.addScore3Player_yellow_radioButton)
        val blackButton = view.findViewById<RadioButton>(R.id.addScore3Player_black_radioButton)

        //set multiply
        val cross = view.findViewById<LinearLayout>(R.id.addScore3Player_cross_linearLayout)
        val multiply = view.findViewById<LinearLayout>(R.id.addScore3Player_multiply_linearLayout)

        val multiply1 = view.findViewById<TextView>(R.id.addScore3Player_multiplyPlayer1_text)
        val multiply2 = view.findViewById<TextView>(R.id.addScore3Player_multiplyPlayer2_text)
        val multiply3 = view.findViewById<TextView>(R.id.addScore3Player_multiplyPlayer3_text)

        //set last color
        when (scoreList3Player[position].color) {
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

            color = "White"
        }

        redButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = redValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()
            multiply3.text = multiplyNumber.toString()

            color = "Red"
        }

        blueButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = blueValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()
            multiply3.text = multiplyNumber.toString()

            color = "Blue"
        }

        yellowButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = yellowValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()
            multiply3.text = multiplyNumber.toString()

            color = "Yellow"
        }

        blackButton.setOnClickListener {
            cross.visibility = View.VISIBLE
            multiply.visibility = View.VISIBLE

            multiplyNumber = blackValue

            multiply1.text = multiplyNumber.toString()
            multiply2.text = multiplyNumber.toString()
            multiply3.text = multiplyNumber.toString()

            color = "Black"
        }

        player1Text.text = player1Name
        player2Text.text = player2Name
        player3Text.text = player3Name

        player1Score.setText(selectedScore1)
        player2Score.setText(selectedScore2)
        player3Score.setText(selectedScore3)

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        addDialog.setView(view)
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
            } else if (player3Score!!.text.isEmpty()) {
                player3Score.error = getString(R.string.compulsory_text)
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
                    val newInstantScore3 = player3Score.text.toString()

                    val newInstantScore1Multiply = newInstantScore1.toInt() * multiplyNumber
                    val newInstantScore2Multiply = newInstantScore2.toInt() * multiplyNumber
                    val newInstantScore3Multiply = newInstantScore3.toInt() * multiplyNumber

                    //set new score to arraylist
                    scoreList3Player[position].player1_score = newInstantScore1Multiply.toString()
                    scoreList3Player[position].player2_score = newInstantScore2Multiply.toString()
                    scoreList3Player[position].player3_score = newInstantScore3Multiply.toString()
                    scoreList3Player[position].gameNumber = selectedGameNumber
                    scoreList3Player[position].multiplyNumber = multiplyNumber
                    scoreList3Player[position].color = color

                    binding.scoreBoard3PlayerRoundNumberText.text =
                        "$gameNumber. ${getString(R.string.round_text)}"

                    scoreCount++

                    //instant score
                    val exInstantScore1 =
                        binding.scoreBoard3PlayerPlayer1InstantScoreText.text.toString()
                    val exInstantScore2 =
                        binding.scoreBoard3PlayerPlayer2InstantScoreText.text.toString()
                    val exInstantScore3 =
                        binding.scoreBoard3PlayerPlayer3InstantScoreText.text.toString()

                    //sum entered score and instant score
                    val resultOldInstantScore1 = exInstantScore1.toInt() - selectedScore1.toInt()
                    val resultOldInstantScore2 = exInstantScore2.toInt() - selectedScore2.toInt()
                    val resultOldInstantScore3 = exInstantScore3.toInt() - selectedScore3.toInt()

                    val resultNewInstantScore1 = resultOldInstantScore1 + newInstantScore1Multiply
                    val resultNewInstantScore2 = resultOldInstantScore2 + newInstantScore2Multiply
                    val resultNewInstantScore3 = resultOldInstantScore3 + newInstantScore3Multiply

                    binding.scoreBoard3PlayerPlayer1InstantScoreText.text =
                        resultNewInstantScore1.toString()
                    binding.scoreBoard3PlayerPlayer2InstantScoreText.text =
                        resultNewInstantScore2.toString()
                    binding.scoreBoard3PlayerPlayer3InstantScoreText.text =
                        resultNewInstantScore3.toString()

                    scoreAdapter3Player.notifyDataSetChanged()

                } else {

                    //entered scores to arraylist
                    val newInstantScore1 = player1Score.text.toString()
                    val newInstantScore2 = player2Score.text.toString()
                    val newInstantScore3 = player3Score.text.toString()

                    val newInstantScore1Multiply = newInstantScore1.toInt() * multiplyNumber
                    val newInstantScore2Multiply = newInstantScore2.toInt() * multiplyNumber
                    val newInstantScore3Multiply = newInstantScore3.toInt() * multiplyNumber

                    //set new score to arraylist
                    scoreList3Player[position].player1_score = newInstantScore1Multiply.toString()
                    scoreList3Player[position].player2_score = newInstantScore2Multiply.toString()
                    scoreList3Player[position].player3_score = newInstantScore3Multiply.toString()
                    scoreList3Player[position].gameNumber = selectedGameNumber
                    scoreList3Player[position].multiplyNumber = multiplyNumber
                    scoreList3Player[position].color = color

                    binding.scoreBoard3PlayerRoundNumberText.text =
                        "$gameNumber. ${getString(R.string.round_text)}"

                    scoreCount++

                    //instant score
                    val exInstantScore1 =
                        binding.scoreBoard3PlayerPlayer1InstantScoreText.text.toString()
                    val exInstantScore2 =
                        binding.scoreBoard3PlayerPlayer2InstantScoreText.text.toString()
                    val exInstantScore3 =
                        binding.scoreBoard3PlayerPlayer3InstantScoreText.text.toString()

                    //sum entered score and instant score
                    val resultOldInstantScore1 = exInstantScore1.toInt() + selectedScore1.toInt()
                    val resultOldInstantScore2 = exInstantScore2.toInt() + selectedScore2.toInt()
                    val resultOldInstantScore3 = exInstantScore3.toInt() + selectedScore3.toInt()

                    val resultNewInstantScore1 = resultOldInstantScore1 - newInstantScore1Multiply
                    val resultNewInstantScore2 = resultOldInstantScore2 - newInstantScore2Multiply
                    val resultNewInstantScore3 = resultOldInstantScore3 - newInstantScore3Multiply

                    binding.scoreBoard3PlayerPlayer1InstantScoreText.text =
                        resultNewInstantScore1.toString()
                    binding.scoreBoard3PlayerPlayer2InstantScoreText.text =
                        resultNewInstantScore2.toString()
                    binding.scoreBoard3PlayerPlayer3InstantScoreText.text =
                        resultNewInstantScore3.toString()

                    scoreAdapter3Player.notifyDataSetChanged()

                }

                val score1 =
                    binding.scoreBoard3PlayerPlayer1InstantScoreText.text.toString().toInt()
                val score2 =
                    binding.scoreBoard3PlayerPlayer2InstantScoreText.text.toString().toInt()
                val score3 =
                    binding.scoreBoard3PlayerPlayer3InstantScoreText.text.toString().toInt()

                if (gameType == "Deduct from the number") {
                    if (score1 <= 0 || score2 <= 0 || score3 <= 0) {
                        winnerTeam()
                    }
                }

                dialog.dismiss()

            }

        }
        addDialog.setNegativeButton(R.string.cancel_text) { dialog, _ ->
            dialog.dismiss()
        }
        addDialog.setCancelable(false)
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


    //save & and exit
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


    //on back pressed main menu
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        if (isSelected) {

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