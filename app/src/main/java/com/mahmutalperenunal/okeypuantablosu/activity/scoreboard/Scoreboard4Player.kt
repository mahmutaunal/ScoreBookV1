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
import com.mahmutalperenunal.okeypuantablosu.adapter.ScoreAdapter4Player
import com.mahmutalperenunal.okeypuantablosu.databinding.ActivityScoreboard4PlayerBinding
import com.mahmutalperenunal.okeypuantablosu.model.ScoreData4Player

//operations such as entering scores, deleting players.
class Scoreboard4Player : AppCompatActivity() {

    private lateinit var binding: ActivityScoreboard4PlayerBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var scoreList4Player: ArrayList<ScoreData4Player>
    private lateinit var scoreAdapter4Player: ScoreAdapter4Player

    private var scoreCount: Int = -1

    private var gameName: String? = null

    private var player1Name: String? = null
    private var player2Name: String? = null
    private var player3Name: String? = null
    private var player4Name: String? = null

    private var player1Score: EditText? = null
    private var player2Score: EditText? = null
    private var player3Score: EditText? = null
    private var player4Score: EditText? = null

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
    private var firstScore4: Int = 1

    private lateinit var sharedPreferencesTheme: SharedPreferences


    @SuppressLint("SetTextI18n", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreboard4PlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set orientation portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //set admob banner
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.scoreBoard4PlayerAdView.loadAd(adRequest)

        //game name
        gameName = intent.getStringExtra("Game Name").toString()

        if (gameName == "") {
            binding.scoreBoard4PlayerTitleText.text = getString(R.string.new_game_text)
        } else {
            binding.scoreBoard4PlayerTitleText.text = gameName
        }

        //player names
        player1Name = intent.getStringExtra("Player-1 Name").toString()
        player2Name = intent.getStringExtra("Player-2 Name").toString()
        player3Name = intent.getStringExtra("Player-3 Name").toString()
        player4Name = intent.getStringExtra("Player-4 Name").toString()

        binding.scoreBoard4PlayerPlayer1NameText.text = player1Name
        binding.scoreBoard4PlayerPlayer2NameText.text = player2Name
        binding.scoreBoard4PlayerPlayer3NameText.text = player3Name
        binding.scoreBoard4PlayerPlayer4NameText.text = player4Name

        //get colors value
        redValue = intent.getIntExtra("Red Value", 0)
        blueValue = intent.getIntExtra("Blue Value", 0)
        yellowValue = intent.getIntExtra("Yellow Value", 0)
        blackValue = intent.getIntExtra("Black Value", 0)

        //get game type and first number
        gameType = intent.getStringExtra("Game Type").toString()
        firstNumber = intent.getStringExtra("Number of Starts").toString()

        if (firstNumber.isEmpty()) {
            binding.scoreBoard4PlayerPlayer1InstantScoreText.text = "0000"
            binding.scoreBoard4PlayerPlayer2InstantScoreText.text = "0000"
            binding.scoreBoard4PlayerPlayer3InstantScoreText.text = "0000"
            binding.scoreBoard4PlayerPlayer4InstantScoreText.text = "0000"
        } else {
            binding.scoreBoard4PlayerPlayer1InstantScoreText.text = firstNumber
            binding.scoreBoard4PlayerPlayer2InstantScoreText.text = firstNumber
            binding.scoreBoard4PlayerPlayer3InstantScoreText.text = firstNumber
            binding.scoreBoard4PlayerPlayer4InstantScoreText.text = firstNumber
        }


        //get click count
        sharedPreferences = getSharedPreferences("clickCount4Player", Context.MODE_PRIVATE)

        //get theme
        sharedPreferencesTheme = getSharedPreferences("appTheme", MODE_PRIVATE)


        //set list
        scoreList4Player = ArrayList()

        //set recyclerView
        recyclerView = findViewById(R.id.scoreBoard4Player_recyclerView)

        //set adapter
        scoreAdapter4Player = ScoreAdapter4Player(scoreList4Player)

        //set recyclerView adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = scoreAdapter4Player


        //onClick process
        onClickProcess()


        //set addScore dialog
        binding.scoreBoard4PlayerAddScoreButton.setOnClickListener { addScore() }

        //set scoreboard dialog
        binding.scoreBoard4PlayerScoreboardButton.setOnClickListener { scoreboard() }

        //exit main menu
        binding.scoreBoard4PlayerBackButton.setOnClickListener { exitMainMenu() }

        //save & exit
        binding.scoreBoard4PlayerFinishGameButton.setOnClickListener { saveExit() }

        //dice roller
        binding.scoreBoard4PlayerDiceIcon.setOnClickListener { diceRoller() }

        //calculator
        binding.scoreBoard4PlayerCalculatorIcon.setOnClickListener { openCalculator() }
    }


    //add score
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n", "InflateParams")
    private fun addScore() {

        multiplyNumber = 1

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.add_score_4_player, null)

        color = "White"

        //set playerScore view
        player1Score = view.findViewById(R.id.addScore4Player_player1Score_editText)
        player2Score = view.findViewById(R.id.addScore4Player_player2Score_editText)
        player3Score = view.findViewById(R.id.addScore4Player_player3Score_editText)
        player4Score = view.findViewById(R.id.addScore4Player_player4Score_editText)

        //set player names
        val player1Text = view.findViewById<TextView>(R.id.addScore4Player_player1Name_textView)
        val player2Text = view.findViewById<TextView>(R.id.addScore4Player_player2Name_textView)
        val player3Text = view.findViewById<TextView>(R.id.addScore4Player_player3Name_textView)
        val player4Text = view.findViewById<TextView>(R.id.addScore4Player_player4Name_textView)

        //set colors layout visibility
        val colorLayout = view.findViewById<RadioGroup>(R.id.addScore4Player_colors_radioGroup)

        if (redValue == 1 && blueValue == 1 && yellowValue == 1 && blackValue == 1) {
            colorLayout.visibility = View.GONE
        }

        //set colors
        val noColorButton = view.findViewById<RadioButton>(R.id.addScore4Player_noColor_radioButton)
        val redButton = view.findViewById<RadioButton>(R.id.addScore4Player_red_radioButton)
        val blueButton = view.findViewById<RadioButton>(R.id.addScore4Player_blue_radioButton)
        val yellowButton = view.findViewById<RadioButton>(R.id.addScore4Player_yellow_radioButton)
        val blackButton = view.findViewById<RadioButton>(R.id.addScore4Player_black_radioButton)

        //set multiply
        val cross = view.findViewById<LinearLayout>(R.id.addScore4Player_cross_linearLayout)
        val multiply = view.findViewById<LinearLayout>(R.id.addScore4Player_multiply_linearLayout)

        val multiply1 = view.findViewById<TextView>(R.id.addScore4Player_multiplyPlayer1_text)
        val multiply2 = view.findViewById<TextView>(R.id.addScore4Player_multiplyPlayer2_text)
        val multiply3 = view.findViewById<TextView>(R.id.addScore4Player_multiplyPlayer3_text)
        val multiply4 = view.findViewById<TextView>(R.id.addScore4Player_multiplyPlayer4_text)

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

        player1Text.text = player1Name
        player2Text.text = player2Name
        player3Text.text = player3Name
        player4Text.text = player4Name

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
            } else if (player3Score!!.text.isEmpty()) {
                player3Score!!.error = getString(R.string.compulsory_text)
                Toast.makeText(
                    applicationContext,
                    R.string.enter_all_scores_text,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (player4Score!!.text.isEmpty()) {
                player4Score!!.error = getString(R.string.compulsory_text)
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
                    val newInstantScore4 = player4Score!!.text.toString()

                    val newInstantScore1Multiply = newInstantScore1.toInt() * multiplyNumber
                    val newInstantScore2Multiply = newInstantScore2.toInt() * multiplyNumber
                    val newInstantScore3Multiply = newInstantScore3.toInt() * multiplyNumber
                    val newInstantScore4Multiply = newInstantScore4.toInt() * multiplyNumber

                    scoreList4Player.add(
                        ScoreData4Player(
                            newInstantScore1Multiply.toString(),
                            newInstantScore2Multiply.toString(),
                            newInstantScore3Multiply.toString(),
                            newInstantScore4Multiply.toString(),
                            gameNumber,
                            multiplyNumber,
                            color,
                            false
                        )
                    )

                    gameNumber++

                    binding.scoreBoard4PlayerRoundNumberText.text =
                        "$gameNumber. ${getString(R.string.round_text)}"

                    scoreCount++

                    //instant scores
                    val exInstantScore1 =
                        binding.scoreBoard4PlayerPlayer1InstantScoreText.text.toString()
                    val exInstantScore2 =
                        binding.scoreBoard4PlayerPlayer2InstantScoreText.text.toString()
                    val exInstantScore3 =
                        binding.scoreBoard4PlayerPlayer3InstantScoreText.text.toString()
                    val exInstantScore4 =
                        binding.scoreBoard4PlayerPlayer4InstantScoreText.text.toString()

                    //sum of entered scores and instant scores
                    val resultInstantScore1 = newInstantScore1Multiply + exInstantScore1.toInt()
                    val resultInstantScore2 = newInstantScore2Multiply + exInstantScore2.toInt()
                    val resultInstantScore3 = newInstantScore3Multiply + exInstantScore3.toInt()
                    val resultInstantScore4 = newInstantScore4Multiply + exInstantScore4.toInt()

                    binding.scoreBoard4PlayerPlayer1InstantScoreText.text =
                        resultInstantScore1.toString()
                    binding.scoreBoard4PlayerPlayer2InstantScoreText.text =
                        resultInstantScore2.toString()
                    binding.scoreBoard4PlayerPlayer3InstantScoreText.text =
                        resultInstantScore3.toString()
                    binding.scoreBoard4PlayerPlayer4InstantScoreText.text =
                        resultInstantScore4.toString()

                    scoreAdapter4Player.notifyDataSetChanged()

                } else {

                    //entered scores to arraylist
                    val newInstantScore1 = player1Score!!.text.toString()
                    val newInstantScore2 = player2Score!!.text.toString()
                    val newInstantScore3 = player3Score!!.text.toString()
                    val newInstantScore4 = player4Score!!.text.toString()

                    val newInstantScore1Multiply = newInstantScore1.toInt() * multiplyNumber
                    val newInstantScore2Multiply = newInstantScore2.toInt() * multiplyNumber
                    val newInstantScore3Multiply = newInstantScore3.toInt() * multiplyNumber
                    val newInstantScore4Multiply = newInstantScore4.toInt() * multiplyNumber

                    scoreList4Player.add(
                        ScoreData4Player(
                            newInstantScore1Multiply.toString(),
                            newInstantScore2Multiply.toString(),
                            newInstantScore3Multiply.toString(),
                            newInstantScore4Multiply.toString(),
                            gameNumber,
                            multiplyNumber,
                            color,
                            false
                        )
                    )

                    gameNumber++

                    binding.scoreBoard4PlayerRoundNumberText.text =
                        "$gameNumber. ${getString(R.string.round_text)}"

                    scoreCount++

                    //instant scores
                    val exInstantScore1 =
                        binding.scoreBoard4PlayerPlayer1InstantScoreText.text.toString()
                    val exInstantScore2 =
                        binding.scoreBoard4PlayerPlayer2InstantScoreText.text.toString()
                    val exInstantScore3 =
                        binding.scoreBoard4PlayerPlayer3InstantScoreText.text.toString()
                    val exInstantScore4 =
                        binding.scoreBoard4PlayerPlayer4InstantScoreText.text.toString()

                    //sum of entered scores and instant scores
                    val resultInstantScore1 = exInstantScore1.toInt() - newInstantScore1Multiply
                    val resultInstantScore2 = exInstantScore2.toInt() - newInstantScore2Multiply
                    val resultInstantScore3 = exInstantScore3.toInt() - newInstantScore3Multiply
                    val resultInstantScore4 = exInstantScore4.toInt() - newInstantScore4Multiply

                    binding.scoreBoard4PlayerPlayer1InstantScoreText.text =
                        resultInstantScore1.toString()
                    binding.scoreBoard4PlayerPlayer2InstantScoreText.text =
                        resultInstantScore2.toString()
                    binding.scoreBoard4PlayerPlayer3InstantScoreText.text =
                        resultInstantScore3.toString()
                    binding.scoreBoard4PlayerPlayer4InstantScoreText.text =
                        resultInstantScore4.toString()

                    scoreAdapter4Player.notifyDataSetChanged()

                }

                val score1 =
                    binding.scoreBoard4PlayerPlayer1InstantScoreText.text.toString().toInt()
                val score2 =
                    binding.scoreBoard4PlayerPlayer2InstantScoreText.text.toString().toInt()
                val score3 =
                    binding.scoreBoard4PlayerPlayer3InstantScoreText.text.toString().toInt()
                val score4 =
                    binding.scoreBoard4PlayerPlayer4InstantScoreText.text.toString().toInt()

                if (gameType == "Deduct from the number") {
                    if (score1 <= 0 || score2 <= 0 || score3 <= 0 || score4 <= 0) {
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
        val view = inflater.inflate(R.layout.scoreboard_4_player, null)

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        val winnerTeam = view.findViewById<TextView>(R.id.scoreboard4Player_winnerPlayer_text)

        val player1ScoreText = view.findViewById<TextView>(R.id.scoreboard4Player_player1Score_textView)
        val player2ScoreText = view.findViewById<TextView>(R.id.scoreboard4Player_player2Score_textView)
        val player3ScoreText = view.findViewById<TextView>(R.id.scoreboard4Player_player3Score_textView)
        val player4ScoreText = view.findViewById<TextView>(R.id.scoreboard4Player_player4Score_textView)

        val player1TotalScore = binding.scoreBoard4PlayerPlayer1InstantScoreText.text.toString()
        val player2TotalScore = binding.scoreBoard4PlayerPlayer2InstantScoreText.text.toString()
        val player3TotalScore = binding.scoreBoard4PlayerPlayer3InstantScoreText.text.toString()
        val player4TotalScore = binding.scoreBoard4PlayerPlayer4InstantScoreText.text.toString()

        player1ScoreText.text = player1TotalScore
        player2ScoreText.text = player2TotalScore
        player3ScoreText.text = player3TotalScore
        player4ScoreText.text = player4TotalScore

        val player1ScoreboardNameText =
            view.findViewById<TextView>(R.id.scoreboard4Player_player1Name_textView)
        val player2ScoreboardNameText =
            view.findViewById<TextView>(R.id.scoreboard4Player_player2Name_textView)
        val player3ScoreboardNameText =
            view.findViewById<TextView>(R.id.scoreboard4Player_player3Name_textView)
        val player4ScoreboardNameText =
            view.findViewById<TextView>(R.id.scoreboard4Player_player4Name_textView)

        player1ScoreboardNameText.text = player1Name
        player2ScoreboardNameText.text = player2Name
        player3ScoreboardNameText.text = player3Name
        player4ScoreboardNameText.text = player4Name

        //show the leading team
        val player1Score = player1TotalScore.toInt()
        val player2Score = player2TotalScore.toInt()
        val player3Score = player3TotalScore.toInt()
        val player4Score = player4TotalScore.toInt()

        when {
            ((player1Score < player2Score) && (player1Score < player3Score) && (player1Score < player4Score)) -> {
                winnerTeam.text = "$player1Name ${getString(R.string.ahead_text)}."
            }

            ((player2Score < player1Score) && (player2Score < player3Score) && (player2Score < player4Score)) -> {
                winnerTeam.text = "$player2Name ${getString(R.string.ahead_text)}."
            }

            ((player3Score < player1Score) && (player3Score < player2Score) && (player3Score < player4Score)) -> {
                winnerTeam.text = "$player3Name ${getString(R.string.ahead_text)}."
            }

            ((player4Score < player1Score) && (player4Score < player2Score) && (player4Score < player3Score)) -> {
                winnerTeam.text = "$player4Name ${getString(R.string.ahead_text)}."
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
        val view = inflater.inflate(R.layout.scoreboard_4_player, null)

        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        val winnerTeam = view.findViewById<TextView>(R.id.scoreboard4Player_winnerPlayer_text)

        val player1ScoreText = view.findViewById<TextView>(R.id.scoreboard4Player_player1Score_textView)
        val player2ScoreText = view.findViewById<TextView>(R.id.scoreboard4Player_player2Score_textView)
        val player3ScoreText = view.findViewById<TextView>(R.id.scoreboard4Player_player3Score_textView)
        val player4ScoreText = view.findViewById<TextView>(R.id.scoreboard4Player_player4Score_textView)

        val player1TotalScore = binding.scoreBoard4PlayerPlayer1InstantScoreText.text.toString()
        val player2TotalScore = binding.scoreBoard4PlayerPlayer2InstantScoreText.text.toString()
        val player3TotalScore = binding.scoreBoard4PlayerPlayer3InstantScoreText.text.toString()
        val player4TotalScore = binding.scoreBoard4PlayerPlayer4InstantScoreText.text.toString()

        player1ScoreText.text = player1TotalScore
        player2ScoreText.text = player2TotalScore
        player3ScoreText.text = player3TotalScore
        player4ScoreText.text = player4TotalScore

        val player1ScoreboardNameText =
            view.findViewById<TextView>(R.id.scoreboard4Player_player1Name_textView)
        val player2ScoreboardNameText =
            view.findViewById<TextView>(R.id.scoreboard4Player_player2Name_textView)
        val player3ScoreboardNameText =
            view.findViewById<TextView>(R.id.scoreboard4Player_player3Name_textView)
        val player4ScoreboardNameText =
            view.findViewById<TextView>(R.id.scoreboard4Player_player4Name_textView)

        player1ScoreboardNameText.text = player1Name
        player2ScoreboardNameText.text = player2Name
        player3ScoreboardNameText.text = player3Name
        player4ScoreboardNameText.text = player4Name

        //show the leading team
        val player1Score = player1TotalScore.toInt()
        val player2Score = player2TotalScore.toInt()
        val player3Score = player3TotalScore.toInt()
        val player4Score = player4TotalScore.toInt()

        when {
            ((player1Score < player2Score) && (player1Score < player3Score) && (player1Score < player4Score)) -> {
                winnerTeam.text = "$player1Name ${getString(R.string.ahead_text)}."
            }

            ((player2Score < player1Score) && (player2Score < player3Score) && (player2Score < player4Score)) -> {
                winnerTeam.text = "$player2Name ${getString(R.string.ahead_text)}."
            }

            ((player3Score < player1Score) && (player3Score < player2Score) && (player3Score < player4Score)) -> {
                winnerTeam.text = "$player3Name ${getString(R.string.ahead_text)}."
            }

            ((player4Score < player1Score) && (player4Score < player2Score) && (player4Score < player3Score)) -> {
                winnerTeam.text = "$player4Name ${getString(R.string.ahead_text)}."
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
        intentCalculator.putExtra("Scoreboard", 4)
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

                val totalScore1 = binding.scoreBoard4PlayerPlayer1InstantScoreText.text.toString()
                val totalScore2 = binding.scoreBoard4PlayerPlayer2InstantScoreText.text.toString()
                val totalScore3 = binding.scoreBoard4PlayerPlayer3InstantScoreText.text.toString()
                val totalScore4 = binding.scoreBoard4PlayerPlayer4InstantScoreText.text.toString()


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

                            binding.scoreBoard4PlayerRoundNumberText.text =
                                "$gameNumber. ${getString(R.string.round_text)}"

                            val resultScore1 =
                                totalScore1.toInt() - scoreList4Player[position].player1_score.toInt()
                            val resultScore2 =
                                totalScore2.toInt() - scoreList4Player[position].player2_score.toInt()
                            val resultScore3 =
                                totalScore3.toInt() - scoreList4Player[position].player3_score.toInt()
                            val resultScore4 =
                                totalScore4.toInt() - scoreList4Player[position].player4_score.toInt()

                            binding.scoreBoard4PlayerPlayer1InstantScoreText.text =
                                resultScore1.toString()
                            binding.scoreBoard4PlayerPlayer2InstantScoreText.text =
                                resultScore2.toString()
                            binding.scoreBoard4PlayerPlayer3InstantScoreText.text =
                                resultScore3.toString()
                            binding.scoreBoard4PlayerPlayer4InstantScoreText.text =
                                resultScore4.toString()

                            scoreList4Player.removeAt(position)

                            scoreCount--

                            scoreAdapter4Player.notifyDataSetChanged()

                            val score1 =
                                binding.scoreBoard4PlayerPlayer1InstantScoreText.text.toString()
                                    .toInt()
                            val score2 =
                                binding.scoreBoard4PlayerPlayer2InstantScoreText.text.toString()
                                    .toInt()
                            val score3 =
                                binding.scoreBoard4PlayerPlayer3InstantScoreText.text.toString()
                                    .toInt()
                            val score4 =
                                binding.scoreBoard4PlayerPlayer4InstantScoreText.text.toString()
                                    .toInt()

                            if (gameType == "Deduct from the number") {
                                if (score1 <= 0 || score2 <= 0 || score3 <= 0 || score4 <= 0) {
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

                val totalScore1 = binding.scoreBoard4PlayerPlayer1InstantScoreText.text.toString()
                val totalScore2 = binding.scoreBoard4PlayerPlayer2InstantScoreText.text.toString()
                val totalScore3 = binding.scoreBoard4PlayerPlayer3InstantScoreText.text.toString()
                val totalScore4 = binding.scoreBoard4PlayerPlayer4InstantScoreText.text.toString()


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

                            binding.scoreBoard4PlayerRoundNumberText.text =
                                "$gameNumber. ${getString(R.string.round_text)}"

                            val resultScore1 =
                                totalScore1.toInt() + scoreList4Player[position].player1_score.toInt()
                            val resultScore2 =
                                totalScore2.toInt() + scoreList4Player[position].player2_score.toInt()
                            val resultScore3 =
                                totalScore3.toInt() + scoreList4Player[position].player3_score.toInt()
                            val resultScore4 =
                                totalScore4.toInt() + scoreList4Player[position].player4_score.toInt()

                            binding.scoreBoard4PlayerPlayer1InstantScoreText.text =
                                resultScore1.toString()
                            binding.scoreBoard4PlayerPlayer2InstantScoreText.text =
                                resultScore2.toString()
                            binding.scoreBoard4PlayerPlayer3InstantScoreText.text =
                                resultScore3.toString()
                            binding.scoreBoard4PlayerPlayer4InstantScoreText.text =
                                resultScore4.toString()

                            scoreList4Player.removeAt(position)

                            scoreCount--

                            scoreAdapter4Player.notifyDataSetChanged()

                            val score1 =
                                binding.scoreBoard4PlayerPlayer1InstantScoreText.text.toString()
                                    .toInt()
                            val score2 =
                                binding.scoreBoard4PlayerPlayer2InstantScoreText.text.toString()
                                    .toInt()
                            val score3 =
                                binding.scoreBoard4PlayerPlayer3InstantScoreText.text.toString()
                                    .toInt()
                            val score4 =
                                binding.scoreBoard4PlayerPlayer4InstantScoreText.text.toString()
                                    .toInt()

                            if (gameType == "Deduct from the number") {
                                if (score1 <= 0 || score2 <= 0 || score3 <= 0 || score4 <= 0) {
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
        scoreAdapter4Player.setOnItemClickListener(object :
            ScoreAdapter4Player.OnItemClickListener {
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
        linearLayout4.visibility = View.VISIBLE


        //set game number
        val gameNumberText =
            view.findViewById<TextView>(R.id.scoreDetail_selectedScoreGameNumber_textView)

        gameNumberText.text =
            "${scoreList4Player[position].gameNumber}. ${getString(R.string.round_text)}"


        //set player names
        val playerName1 = view.findViewById<TextView>(R.id.scoreDetail_player1Name_textView)
        val playerName2 = view.findViewById<TextView>(R.id.scoreDetail_player2Name_textView)
        val playerName3 = view.findViewById<TextView>(R.id.scoreDetail_player3Name_textView)
        val playerName4 = view.findViewById<TextView>(R.id.scoreDetail_player4Name_textView)

        playerName1.text = "$player1Name"
        playerName2.text = "$player2Name"
        playerName3.text = "$player3Name"
        playerName4.text = "$player4Name"


        //set first score
        val firstScore1Text = view.findViewById<TextView>(R.id.scoreDetail_player1Score_textView)
        val firstScore2Text = view.findViewById<TextView>(R.id.scoreDetail_player2Score_textView)
        val firstScore3Text = view.findViewById<TextView>(R.id.scoreDetail_player3Score_textView)
        val firstScore4Text = view.findViewById<TextView>(R.id.scoreDetail_player4Score_textView)

        firstScore1 =
            scoreList4Player[position].player1_score.toInt() / scoreList4Player[position].multiplyNumber
        firstScore2 =
            scoreList4Player[position].player2_score.toInt() / scoreList4Player[position].multiplyNumber
        firstScore3 =
            scoreList4Player[position].player3_score.toInt() / scoreList4Player[position].multiplyNumber
        firstScore4 =
            scoreList4Player[position].player4_score.toInt() / scoreList4Player[position].multiplyNumber

        firstScore1Text.text = firstScore1.toString()
        firstScore2Text.text = firstScore2.toString()
        firstScore3Text.text = firstScore3.toString()
        firstScore4Text.text = firstScore4.toString()


        //set colors and colors value
        val color = view.findViewById<CardView>(R.id.scoreDetail_selectedRoundColor)

        val colorValue1 = view.findViewById<TextView>(R.id.scoreDetail_multiplyPlayer1_textView)
        val colorValue2 = view.findViewById<TextView>(R.id.scoreDetail_multiplyPlayer2_textView)
        val colorValue3 = view.findViewById<TextView>(R.id.scoreDetail_multiplyPlayer3_textView)
        val colorValue4 = view.findViewById<TextView>(R.id.scoreDetail_multiplyPlayer4_textView)

        colorValue1.text = scoreList4Player[position].multiplyNumber.toString()
        colorValue2.text = scoreList4Player[position].multiplyNumber.toString()
        colorValue3.text = scoreList4Player[position].multiplyNumber.toString()
        colorValue4.text = scoreList4Player[position].multiplyNumber.toString()

        when (scoreList4Player[position].color) {
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
        val lastScore1 = view.findViewById<TextView>(R.id.scoreDetail_totalScorePlayer1_textView)
        val lastScore2 = view.findViewById<TextView>(R.id.scoreDetail_totalScorePlayer2_textView)
        val lastScore3 = view.findViewById<TextView>(R.id.scoreDetail_totalScorePlayer3_textView)
        val lastScore4 = view.findViewById<TextView>(R.id.scoreDetail_totalScorePlayer4_textView)

        val result1 = firstScore1 * scoreList4Player[position].multiplyNumber
        val result2 = firstScore2 * scoreList4Player[position].multiplyNumber
        val result3 = firstScore3 * scoreList4Player[position].multiplyNumber
        val result4 = firstScore4 * scoreList4Player[position].multiplyNumber

        lastScore1.text = result1.toString()
        lastScore2.text = result2.toString()
        lastScore3.text = result3.toString()
        lastScore4.text = result4.toString()


        val addDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)

        addDialog.setView(view)
        addDialog.setPositiveButton(R.string.edit_text) { dialog, _ ->

            val selectedGameNumber = scoreList4Player[position].gameNumber
            editScore(position, selectedGameNumber)

            dialog.dismiss()
        }
        addDialog.setNegativeButton(R.string.delete_text) { dialog, _ ->

            delete(position)

            dialog.dismiss()
        }
        addDialog.setNeutralButton(R.string.ok_text) { dialog, _ ->
            dialog.dismiss()
        }
        addDialog.create()
        addDialog.show()
    }


    //edit score
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n", "InflateParams")
    private fun editScore(position: Int, selectedGameNumber: Int) {

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.add_score_4_player, null)

        //set selected score
        val selectedScore1 = scoreList4Player[position].player1_score
        val selectedScore2 = scoreList4Player[position].player2_score
        val selectedScore3 = scoreList4Player[position].player3_score
        val selectedScore4 = scoreList4Player[position].player4_score

        //set playerScore view
        val player1Score = view.findViewById<EditText>(R.id.addScore4Player_player1Score_editText)
        val player2Score = view.findViewById<EditText>(R.id.addScore4Player_player2Score_editText)
        val player3Score = view.findViewById<EditText>(R.id.addScore4Player_player3Score_editText)
        val player4Score = view.findViewById<EditText>(R.id.addScore4Player_player4Score_editText)

        //set player names
        val player1Text = view.findViewById<TextView>(R.id.addScore4Player_player1Name_textView)
        val player2Text = view.findViewById<TextView>(R.id.addScore4Player_player2Name_textView)
        val player3Text = view.findViewById<TextView>(R.id.addScore4Player_player3Name_textView)
        val player4Text = view.findViewById<TextView>(R.id.addScore4Player_player4Name_textView)

        //set colors layout visibility
        val colorLayout = view.findViewById<RadioGroup>(R.id.addScore4Player_colors_radioGroup)

        if (redValue == 1 && blueValue == 1 && yellowValue == 1 && blackValue == 1) {
            colorLayout.visibility = View.GONE
        }

        //set colors
        val noColorButton = view.findViewById<RadioButton>(R.id.addScore4Player_noColor_radioButton)
        val redButton = view.findViewById<RadioButton>(R.id.addScore4Player_red_radioButton)
        val blueButton = view.findViewById<RadioButton>(R.id.addScore4Player_blue_radioButton)
        val yellowButton = view.findViewById<RadioButton>(R.id.addScore4Player_yellow_radioButton)
        val blackButton = view.findViewById<RadioButton>(R.id.addScore4Player_black_radioButton)

        //set multiply
        val cross = view.findViewById<LinearLayout>(R.id.addScore4Player_cross_linearLayout)
        val multiply = view.findViewById<LinearLayout>(R.id.addScore4Player_multiply_linearLayout)

        val multiply1 = view.findViewById<TextView>(R.id.addScore4Player_multiplyPlayer1_text)
        val multiply2 = view.findViewById<TextView>(R.id.addScore4Player_multiplyPlayer2_text)
        val multiply3 = view.findViewById<TextView>(R.id.addScore4Player_multiplyPlayer3_text)
        val multiply4 = view.findViewById<TextView>(R.id.addScore4Player_multiplyPlayer4_text)

        //set last color
        when (scoreList4Player[position].color) {
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

        player1Text.text = player1Name
        player2Text.text = player2Name
        player3Text.text = player3Name
        player4Text.text = player4Name

        player1Score.setText(selectedScore1)
        player2Score.setText(selectedScore2)
        player3Score.setText(selectedScore3)
        player4Score.setText(selectedScore4)

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
            } else if (player4Score!!.text.isEmpty()) {
                player4Score.error = getString(R.string.compulsory_text)
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
                    val newInstantScore4 = player4Score.text.toString()

                    val newInstantScore1Multiply = newInstantScore1.toInt() * multiplyNumber
                    val newInstantScore2Multiply = newInstantScore2.toInt() * multiplyNumber
                    val newInstantScore3Multiply = newInstantScore3.toInt() * multiplyNumber
                    val newInstantScore4Multiply = newInstantScore4.toInt() * multiplyNumber

                    //set new score to arraylist
                    scoreList4Player[position].player1_score = newInstantScore1Multiply.toString()
                    scoreList4Player[position].player2_score = newInstantScore2Multiply.toString()
                    scoreList4Player[position].player3_score = newInstantScore3Multiply.toString()
                    scoreList4Player[position].player4_score = newInstantScore4Multiply.toString()
                    scoreList4Player[position].gameNumber = selectedGameNumber
                    scoreList4Player[position].multiplyNumber = multiplyNumber
                    scoreList4Player[position].color = color

                    binding.scoreBoard4PlayerRoundNumberText.text =
                        "$gameNumber. ${getString(R.string.round_text)}"

                    scoreCount++

                    //instant score
                    val exInstantScore1 =
                        binding.scoreBoard4PlayerPlayer1InstantScoreText.text.toString()
                    val exInstantScore2 =
                        binding.scoreBoard4PlayerPlayer2InstantScoreText.text.toString()
                    val exInstantScore3 =
                        binding.scoreBoard4PlayerPlayer3InstantScoreText.text.toString()
                    val exInstantScore4 =
                        binding.scoreBoard4PlayerPlayer4InstantScoreText.text.toString()

                    //sum entered score and instant score
                    val resultOldInstantScore1 = exInstantScore1.toInt() - selectedScore1.toInt()
                    val resultOldInstantScore2 = exInstantScore2.toInt() - selectedScore2.toInt()
                    val resultOldInstantScore3 = exInstantScore3.toInt() - selectedScore3.toInt()
                    val resultOldInstantScore4 = exInstantScore4.toInt() - selectedScore4.toInt()

                    val resultNewInstantScore1 = resultOldInstantScore1 + newInstantScore1Multiply
                    val resultNewInstantScore2 = resultOldInstantScore2 + newInstantScore2Multiply
                    val resultNewInstantScore3 = resultOldInstantScore3 + newInstantScore3Multiply
                    val resultNewInstantScore4 = resultOldInstantScore4 + newInstantScore4Multiply

                    binding.scoreBoard4PlayerPlayer1InstantScoreText.text =
                        resultNewInstantScore1.toString()
                    binding.scoreBoard4PlayerPlayer2InstantScoreText.text =
                        resultNewInstantScore2.toString()
                    binding.scoreBoard4PlayerPlayer3InstantScoreText.text =
                        resultNewInstantScore3.toString()
                    binding.scoreBoard4PlayerPlayer4InstantScoreText.text =
                        resultNewInstantScore4.toString()

                    scoreAdapter4Player.notifyDataSetChanged()

                } else {

                    //entered scores to arraylist
                    val newInstantScore1 = player1Score.text.toString()
                    val newInstantScore2 = player2Score.text.toString()
                    val newInstantScore3 = player3Score.text.toString()
                    val newInstantScore4 = player4Score.text.toString()

                    val newInstantScore1Multiply = newInstantScore1.toInt() * multiplyNumber
                    val newInstantScore2Multiply = newInstantScore2.toInt() * multiplyNumber
                    val newInstantScore3Multiply = newInstantScore3.toInt() * multiplyNumber
                    val newInstantScore4Multiply = newInstantScore4.toInt() * multiplyNumber

                    //set new score to arraylist
                    scoreList4Player[position].player1_score = newInstantScore1Multiply.toString()
                    scoreList4Player[position].player2_score = newInstantScore2Multiply.toString()
                    scoreList4Player[position].player3_score = newInstantScore3Multiply.toString()
                    scoreList4Player[position].player4_score = newInstantScore4Multiply.toString()
                    scoreList4Player[position].gameNumber = selectedGameNumber
                    scoreList4Player[position].multiplyNumber = multiplyNumber
                    scoreList4Player[position].color = color

                    binding.scoreBoard4PlayerRoundNumberText.text =
                        "$gameNumber. ${getString(R.string.round_text)}"

                    scoreCount++

                    //instant score
                    val exInstantScore1 =
                        binding.scoreBoard4PlayerPlayer1InstantScoreText.text.toString()
                    val exInstantScore2 =
                        binding.scoreBoard4PlayerPlayer2InstantScoreText.text.toString()
                    val exInstantScore3 =
                        binding.scoreBoard4PlayerPlayer3InstantScoreText.text.toString()
                    val exInstantScore4 =
                        binding.scoreBoard4PlayerPlayer4InstantScoreText.text.toString()

                    //sum entered score and instant score
                    val resultOldInstantScore1 = exInstantScore1.toInt() + selectedScore1.toInt()
                    val resultOldInstantScore2 = exInstantScore2.toInt() + selectedScore2.toInt()
                    val resultOldInstantScore3 = exInstantScore3.toInt() + selectedScore3.toInt()
                    val resultOldInstantScore4 = exInstantScore4.toInt() + selectedScore4.toInt()

                    val resultNewInstantScore1 = resultOldInstantScore1 - newInstantScore1Multiply
                    val resultNewInstantScore2 = resultOldInstantScore2 - newInstantScore2Multiply
                    val resultNewInstantScore3 = resultOldInstantScore3 - newInstantScore3Multiply
                    val resultNewInstantScore4 = resultOldInstantScore4 - newInstantScore4Multiply

                    binding.scoreBoard4PlayerPlayer1InstantScoreText.text =
                        resultNewInstantScore1.toString()
                    binding.scoreBoard4PlayerPlayer2InstantScoreText.text =
                        resultNewInstantScore2.toString()
                    binding.scoreBoard4PlayerPlayer3InstantScoreText.text =
                        resultNewInstantScore3.toString()
                    binding.scoreBoard4PlayerPlayer4InstantScoreText.text =
                        resultNewInstantScore4.toString()

                    scoreAdapter4Player.notifyDataSetChanged()

                }

                val score1 =
                    binding.scoreBoard4PlayerPlayer1InstantScoreText.text.toString().toInt()
                val score2 =
                    binding.scoreBoard4PlayerPlayer2InstantScoreText.text.toString().toInt()
                val score3 =
                    binding.scoreBoard4PlayerPlayer3InstantScoreText.text.toString().toInt()
                val score4 =
                    binding.scoreBoard4PlayerPlayer4InstantScoreText.text.toString().toInt()

                if (gameType == "Deduct from the number") {
                    if (score1 <= 0 || score2 <= 0 || score3 <= 0 || score4 <= 0) {
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


    //on back pressed main menu
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