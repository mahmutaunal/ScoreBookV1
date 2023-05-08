package com.mahmutalperenunal.okeypuantablosu.teamoperations

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
import com.mahmutalperenunal.okeypuantablosu.mainmenu.MainMenu
import com.mahmutalperenunal.okeypuantablosu.databinding.ActivityTeamOperationsBinding
import com.mahmutalperenunal.okeypuantablosu.puantablosu.Scoreboard2Player
import com.mahmutalperenunal.okeypuantablosu.puantablosu.Scoreboard3Player
import com.mahmutalperenunal.okeypuantablosu.puantablosu.PuanTablosu4Kisi

class TeamOperations : AppCompatActivity() {

    private lateinit var binding: ActivityTeamOperationsBinding

    private var player1Name: EditText? = null
    private var player2Name: EditText? = null
    private var player3Name: EditText? = null
    private var player4Name: EditText? = null

    private var gameName: EditText? = null

    private var redValue: Int = 0
    private var blueValue: Int = 0
    private var yellowValue: Int = 0
    private var blackValue: Int = 0

    private var isColorsValueEntered: Boolean = false

    private var gameType: String = "Add Score"

    private var firstNumber: EditText? = null


    @SuppressLint("VisibleForTests", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamOperationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set screen orientation to portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //set player names
        player1Name = binding.oyuncu1EditText
        player2Name = binding.oyuncu2EditText
        player3Name = binding.oyuncu3EditText
        player4Name = binding.oyuncu4EditText

        //set game name
        gameName = binding.oyunIsmiEditText

        //set first number
        firstNumber = binding.baslangicSayisiEditText

        playerNumberVisibility()
        playerNumber()
        teamDeterminationByPlayerType()
        playerType()
        gameType()

        //navigate Scoreboard Activity with number of player
        binding.baslatButton.setOnClickListener { controlUsernames() }


        //on back pressed turn back to main menu
        binding.backButton.setOnClickListener {
            val intentMain = Intent(applicationContext, MainMenu::class.java)
            startActivity(intentMain)
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

    }


    //check usernames
    private fun controlUsernames() {

        if (playerNumber() == 2) {

            if (binding.oyuncu1EditText.text!!.isEmpty()) {
                binding.oyuncu1EditTextLayout.error = getString(R.string.compulsory_text)
                Toast.makeText(
                    applicationContext,
                    R.string.enter_player_name_text,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.oyuncu2EditText.text!!.isEmpty()) {
                binding.oyuncu2EditTextLayout.error = getString(R.string.compulsory_text)
                Toast.makeText(
                    applicationContext,
                    R.string.enter_player_name_text,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (gameType == "Deduct from the number") {
                    if (firstNumber!!.text.toString() == "") {
                        binding.baslangicSayisiEditTextLayout.error =
                            getString(R.string.compulsory_text)
                        Toast.makeText(
                            applicationContext,
                            R.string.enter_player_name_text,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        setColorValue()
                    }
                } else {
                    setColorValue()
                }
            }

        } else if (playerNumber() == 3) {

            if (binding.oyuncu1EditText.text!!.isEmpty()) {
                binding.oyuncu1EditTextLayout.error = getString(R.string.compulsory_text)
                Toast.makeText(
                    applicationContext,
                    R.string.enter_player_name_text,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.oyuncu2EditText.text!!.isEmpty()) {
                binding.oyuncu2EditTextLayout.error = getString(R.string.compulsory_text)
                Toast.makeText(
                    applicationContext,
                    R.string.enter_player_name_text,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.oyuncu3EditText.text!!.isEmpty()) {
                binding.oyuncu3EditTextLayout.error = getString(R.string.compulsory_text)
                Toast.makeText(
                    applicationContext,
                    R.string.enter_player_name_text,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (gameType == "Deduct from the number") {
                    if (firstNumber!!.text.toString() == "") {
                        binding.baslangicSayisiEditTextLayout.error =
                            getString(R.string.compulsory_text)
                        Toast.makeText(
                            applicationContext,
                            R.string.enter_player_name_text,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        setColorValue()
                    }
                } else {
                    setColorValue()
                }
            }

        } else if (playerNumber() == 4) {

            if (binding.oyuncu1EditText.text!!.isEmpty()) {
                binding.oyuncu1EditTextLayout.error = getString(R.string.compulsory_text)
                Toast.makeText(
                    applicationContext,
                    R.string.enter_player_name_text,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.oyuncu2EditText.text!!.isEmpty()) {
                binding.oyuncu2EditTextLayout.error = getString(R.string.compulsory_text)
                Toast.makeText(
                    applicationContext,
                    R.string.enter_player_name_text,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.oyuncu3EditText.text!!.isEmpty()) {
                binding.oyuncu3EditTextLayout.error = getString(R.string.compulsory_text)
                Toast.makeText(
                    applicationContext,
                    R.string.enter_player_name_text,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.oyuncu2EditText.text!!.isEmpty()) {
                binding.oyuncu3EditTextLayout.error = getString(R.string.compulsory_text)
                Toast.makeText(
                    applicationContext,
                    R.string.enter_player_name_text,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (gameType == "Deduct from the number") {
                    if (firstNumber!!.text.toString() == "") {
                        binding.baslangicSayisiEditTextLayout.error =
                            getString(R.string.compulsory_text)
                        Toast.makeText(
                            applicationContext,
                            R.string.enter_player_name_text,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        setColorValue()
                    }
                } else {
                    setColorValue()
                }
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
        addDialog.setPositiveButton(
            R.string.start_text
        ) { dialog, _ ->
            if (redEditText.text.isEmpty()) {

                redEditTextLayout.error = getString(R.string.compulsory_text)
                Toast.makeText(applicationContext, R.string.enter_color_values, Toast.LENGTH_SHORT)
                    .show()
                isColorsValueEntered = false

            } else if (blueEditText.text.isEmpty()) {

                blueEditTextLayout.error = getString(R.string.compulsory_text)
                Toast.makeText(applicationContext, R.string.enter_color_values, Toast.LENGTH_SHORT)
                    .show()
                isColorsValueEntered = false

            } else if (yellowEditText.text.isEmpty()) {

                yellowEditTextLayout.error = getString(R.string.compulsory_text)
                Toast.makeText(applicationContext, R.string.enter_color_values, Toast.LENGTH_SHORT)
                    .show()
                isColorsValueEntered = false

            } else if (blackEditText.text.isEmpty()) {

                blackEditTextLayout.error = getString(R.string.compulsory_text)
                Toast.makeText(applicationContext, R.string.enter_color_values, Toast.LENGTH_SHORT)
                    .show()
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
        addDialog.setNegativeButton(
            R.string.skip_text
        ) { dialog, _ ->
            redValue = 1
            blueValue = 1
            yellowValue = 1
            blackValue = 1

            isColorsValueEntered = true

            startGame()

            dialog.dismiss()
        }
        addDialog.setNeutralButton(
            R.string.cancel_text
        ) { dialog, _ ->
            isColorsValueEntered = true
            dialog.dismiss()
        }
        addDialog.setCancelable(false)
        addDialog.show()
    }


    //start game
    private fun startGame() {

        val intentScoreboard2 = Intent(applicationContext, Scoreboard2Player::class.java)
        val intentScoreboard3 = Intent(applicationContext, Scoreboard3Player::class.java)
        val intentScoreboard4 = Intent(applicationContext, PuanTablosu4Kisi::class.java)

        if (playerNumber() == 2) {

            //player name must be entered
            if (binding.oyuncu1EditText.text!!.isEmpty() || binding.oyuncu2EditText.text!!.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    R.string.enter_player_name_text,
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                if (isColorsValueEntered) {

                    //send game name to ScoreboardActivity
                    intentScoreboard2.putExtra("Game Name", gameName!!.text.toString())

                    //send player names to ScoreboardActivity
                    intentScoreboard2.putExtra("Player-1 Name", player1Name!!.text.toString())
                    intentScoreboard2.putExtra("Player-2 Name", player2Name!!.text.toString())

                    //send colors value to ScoreboardActivity
                    intentScoreboard2.putExtra("Red Value", redValue)
                    intentScoreboard2.putExtra("Blue Value", blueValue)
                    intentScoreboard2.putExtra("Yellow Value", yellowValue)
                    intentScoreboard2.putExtra("Black Value", blackValue)

                    //send game type and first number
                    intentScoreboard2.putExtra("Game Type", gameType)
                    intentScoreboard2.putExtra("Number of Starts", firstNumber!!.text.toString())

                    startActivity(intentScoreboard2)
                    finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                }

            }

        } else if (playerNumber() == 3) {

            //player name must be entered
            if (binding.oyuncu1EditText.text!!.isEmpty() || binding.oyuncu2EditText.text!!.isEmpty() || binding.oyuncu3EditText.text!!.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    R.string.enter_player_name_text,
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                if (isColorsValueEntered) {

                    //send game name to ScoreboardActivity
                    intentScoreboard3.putExtra("Game Name", gameName!!.text.toString())

                    //send player names to ScoreboardActivity
                    intentScoreboard3.putExtra("Player-1 Name", player1Name!!.text.toString())
                    intentScoreboard3.putExtra("Player-2 Name", player2Name!!.text.toString())
                    intentScoreboard3.putExtra("Player-3 Name", player3Name!!.text.toString())

                    //send colors value to ScoreboardActivity
                    intentScoreboard3.putExtra("Red Value", redValue)
                    intentScoreboard3.putExtra("Blue Value", blueValue)
                    intentScoreboard3.putExtra("Yellow Value", yellowValue)
                    intentScoreboard3.putExtra("Black Value", blackValue)

                    //send game type and first number
                    intentScoreboard3.putExtra("Game Type", gameType)
                    intentScoreboard3.putExtra("Number of Starts", firstNumber!!.text.toString())

                    startActivity(intentScoreboard3)
                    finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                }

            }

        } else if (playerNumber() == 4) {

            //player name must be entered
            if (binding.oyuncu1EditText.text!!.isEmpty() || binding.oyuncu2EditText.text!!.isEmpty() || binding.oyuncu3EditText.text!!.isEmpty() || binding.oyuncu4EditText.text!!.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    R.string.enter_player_name_text,
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                if (isColorsValueEntered) {

                    if (playerType() == "Multiple") {

                        //send game name to ScoreboardActivity
                        intentScoreboard2.putExtra("Game Name", gameName!!.text.toString())

                        //send player names to ScoreboardActivity
                        intentScoreboard2.putExtra("Player-1 Name", player1Name!!.text.toString())
                        intentScoreboard2.putExtra("Player-2 Name", player2Name!!.text.toString())

                        //send colors value to ScoreboardActivity
                        intentScoreboard2.putExtra("Red Value", redValue)
                        intentScoreboard2.putExtra("Blue Value", blueValue)
                        intentScoreboard2.putExtra("Yellow Value", yellowValue)
                        intentScoreboard2.putExtra("Black Value", blackValue)

                        //send game type and first number
                        intentScoreboard2.putExtra("Game Type", gameType)
                        intentScoreboard2.putExtra(
                            "Number of Starts",
                            firstNumber!!.text.toString()
                        )

                        startActivity(intentScoreboard2)
                        finish()
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                    } else {

                        //send game name to ScoreboardActivity
                        intentScoreboard4.putExtra("Game Name", gameName!!.text.toString())

                        //send player names to ScoreboardActivity
                        intentScoreboard4.putExtra("Player-1 Name", player1Name!!.text.toString())
                        intentScoreboard4.putExtra("Player-2 Name", player2Name!!.text.toString())
                        intentScoreboard4.putExtra("Player-3 Name", player3Name!!.text.toString())
                        intentScoreboard4.putExtra("Player-4 Name", player4Name!!.text.toString())

                        //send colors value to ScoreboardActivity
                        intentScoreboard4.putExtra("Red Value", redValue)
                        intentScoreboard4.putExtra("Blue Value", blueValue)
                        intentScoreboard4.putExtra("Yellow Value", yellowValue)
                        intentScoreboard4.putExtra("Black Value", blackValue)

                        //send game type and first number
                        intentScoreboard4.putExtra("Game Type", gameType)
                        intentScoreboard4.putExtra(
                            "Number of Starts",
                            firstNumber!!.text.toString()
                        )

                        startActivity(intentScoreboard4)
                        finish()
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                    }

                }
            }

        }

    }


    //control of player number
    private fun playerNumber(): Int {
        return when {
            binding.ikiOyunculuRadioButton.isChecked -> 2
            binding.ucOyunculuRadioButton.isChecked -> 3
            else -> 4
        }
    }


    //change edittext visibility
    private fun playerNumberVisibility() {

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
    private fun playerType(): String {

        return if (binding.tekliRadioButton.isChecked) {
            "Single"
        } else {
            "Multiple"
        }
    }


    //change hint font and visibility
    private fun teamDeterminationByPlayerType() {

        binding.tekliRadioButton.setOnClickListener {
            binding.oyuncu1EditTextLayout.hint = getString(R.string.player1_text)
            binding.oyuncu2EditTextLayout.hint = getString(R.string.player2_text)
            binding.oyuncu1EditTextLayout.visibility = View.VISIBLE
            binding.oyuncu2EditTextLayout.visibility = View.VISIBLE
            binding.oyuncu3EditTextLayout.visibility = View.VISIBLE
            binding.oyuncu4EditTextLayout.visibility = View.VISIBLE
        }

        binding.esliRadioButton.setOnClickListener {
            binding.oyuncu1EditTextLayout.hint = getString(R.string.team1_text)
            binding.oyuncu2EditTextLayout.hint = getString(R.string.team2_text)
            binding.oyuncu1EditTextLayout.visibility = View.VISIBLE
            binding.oyuncu2EditTextLayout.visibility = View.VISIBLE
            binding.oyuncu3EditTextLayout.visibility = View.GONE
            binding.oyuncu4EditTextLayout.visibility = View.GONE
        }
    }


    //set game type
    @SuppressLint("SetTextI18n")
    private fun gameType() {
        binding.sayidanDusRadioButton.setOnClickListener {
            gameType = "Deduct from the number"
            binding.baslangicSayisiEditTextLayout.visibility = View.VISIBLE
            firstNumber!!.setText("")
        }

        binding.sayiEKleRadioButton.setOnClickListener {
            gameType = "Add Score"
            binding.baslangicSayisiEditTextLayout.visibility = View.GONE
            firstNumber!!.setText("0000")
        }
    }


    //on back pressed turn back to main menu
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intentMain = Intent(applicationContext, MainMenu::class.java)
        startActivity(intentMain)
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}