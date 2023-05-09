package com.mahmutalperenunal.okeypuantablosu.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mahmutalperenunal.okeypuantablosu.R
import com.mahmutalperenunal.okeypuantablosu.databinding.ActivityCalculatorBinding
import com.mahmutalperenunal.okeypuantablosu.activity.scoreboard.Scoreboard2Player
import com.mahmutalperenunal.okeypuantablosu.activity.scoreboard.Scoreboard3Player
import com.mahmutalperenunal.okeypuantablosu.activity.scoreboard.Scoreboard4Player
import org.mariuszgromada.math.mxparser.Expression
import java.text.DecimalFormat

class Calculator : AppCompatActivity() {

    private lateinit var binding: ActivityCalculatorBinding

    private var scoreBoard: Int = 0

    private var isEqualClicked: Boolean = false


    @SuppressLint("SetTextI18n", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set screen orientation to portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //get scoreboard player
        scoreBoard = intent.getIntExtra("Scoreboard", 0)

        binding.buttonC.setOnClickListener {
            binding.solutionTv.text = ""
            binding.resultTv.text = ""
        }

        binding.buttonAc.setOnClickListener {
            binding.solutionTv.text = ""
            binding.resultTv.text = ""
        }

        binding.buttonOpenBracket.setOnClickListener {
            if (isEqualClicked) {
                binding.solutionTv.text = ""
                binding.resultTv.text = ""
                isEqualClicked = false
            }
            binding.solutionTv.text = addToInputText("(")
        }
        binding.buttonCloseBracket.setOnClickListener {
            if (isEqualClicked) {
                binding.solutionTv.text = ""
                binding.resultTv.text = ""
                isEqualClicked = false
            }
            binding.solutionTv.text = addToInputText(")")
        }
        binding.button0.setOnClickListener {
            if (isEqualClicked) {
                binding.solutionTv.text = ""
                binding.resultTv.text = ""
                isEqualClicked = false
            }
            binding.solutionTv.text = addToInputText("0")
        }
        binding.button1.setOnClickListener {
            if (isEqualClicked) {
                binding.solutionTv.text = ""
                binding.resultTv.text = ""
                isEqualClicked = false
            }
            binding.solutionTv.text = addToInputText("1")
        }
        binding.button2.setOnClickListener {
            if (isEqualClicked) {
                binding.solutionTv.text = ""
                binding.resultTv.text = ""
                isEqualClicked = false
            }
            binding.solutionTv.text = addToInputText("2")
        }
        binding.button3.setOnClickListener {
            if (isEqualClicked) {
                binding.solutionTv.text = ""
                binding.resultTv.text = ""
                isEqualClicked = false
            }
            binding.solutionTv.text = addToInputText("3")
        }
        binding.button4.setOnClickListener {
            if (isEqualClicked) {
                binding.solutionTv.text = ""
                binding.resultTv.text = ""
                isEqualClicked = false
            }
            binding.solutionTv.text = addToInputText("4")
        }
        binding.button5.setOnClickListener {
            if (isEqualClicked) {
                binding.solutionTv.text = ""
                binding.resultTv.text = ""
                isEqualClicked = false
            }
            binding.solutionTv.text = addToInputText("5")
        }
        binding.button6.setOnClickListener {
            if (isEqualClicked) {
                binding.solutionTv.text = ""
                binding.resultTv.text = ""
                isEqualClicked = false
            }
            binding.solutionTv.text = addToInputText("6")
        }
        binding.button7.setOnClickListener {
            if (isEqualClicked) {
                binding.solutionTv.text = ""
                binding.resultTv.text = ""
                isEqualClicked = false
            }
            binding.solutionTv.text = addToInputText("7")
        }
        binding.button8.setOnClickListener {
            if (isEqualClicked) {
                binding.solutionTv.text = ""
                binding.resultTv.text = ""
                isEqualClicked = false
            }
            binding.solutionTv.text = addToInputText("8")
        }
        binding.button9.setOnClickListener {
            if (isEqualClicked) {
                binding.solutionTv.text = ""
                binding.resultTv.text = ""
                isEqualClicked = false
            }
            binding.solutionTv.text = addToInputText("9")
        }
        binding.buttonDot.setOnClickListener {
            if (isEqualClicked) {
                binding.solutionTv.text = ""
                binding.resultTv.text = ""
                isEqualClicked = false
            }
            binding.solutionTv.text = addToInputText(".")
        }
        binding.buttonDivide.setOnClickListener {
            if (isEqualClicked)
                binding.solutionTv.text = binding.resultTv.text
            binding.solutionTv.text = addToInputText("÷")
        }
        binding.buttonMultiply.setOnClickListener {
            if (isEqualClicked)
                binding.solutionTv.text = binding.resultTv.text
            binding.solutionTv.text = addToInputText("×")
        }
        binding.buttonMinus.setOnClickListener {
            if (isEqualClicked)
                binding.solutionTv.text = binding.resultTv.text
            binding.solutionTv.text = addToInputText("-")
        }
        binding.buttonPlus.setOnClickListener {
            if (isEqualClicked)
                binding.solutionTv.text = binding.resultTv.text
            binding.solutionTv.text = addToInputText("+")
        }

        binding.buttonEquals.setOnClickListener {
            isEqualClicked = true
            showResult()
        }


        binding.calculatorBackButton.setOnClickListener { onBackPressed() }

    }


    private fun addToInputText(buttonValue: String): String {
        return "${binding.solutionTv.text}$buttonValue"
    }

    private fun getInputExpression(): String {
        var expression = binding.solutionTv.text.replace(Regex("÷"), "/")
        expression = expression.replace(Regex("×"), "*")
        return expression
    }

    @SuppressLint("SetTextI18n")
    private fun showResult() {
        try {
            val expression = getInputExpression()
            val result = Expression(expression).calculate()
            if (result.isNaN()) {
                // Show Error Message
                binding.resultTv.text = getString(R.string.error_text)
                binding.resultTv.setTextColor(ContextCompat.getColor(this, R.color.red))
                binding.solutionTv.text = ""
            } else {
                // Show Result
                binding.resultTv.text = DecimalFormat("0.######").format(result).toString()
                binding.resultTv.setTextColor(ContextCompat.getColor(this, R.color.blue))
            }
        } catch (e: Exception) {
            // Show Error Message
            binding.resultTv.text = getString(R.string.error_text)
            binding.resultTv.setTextColor(ContextCompat.getColor(this, R.color.red))
            binding.solutionTv.text = ""
        }
    }


    //on back pressed turn back scoreboardActivity
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        when (scoreBoard) {
            2 -> {
                val intentScoreboard = Intent(applicationContext, Scoreboard2Player::class.java)
                intentScoreboard.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intentScoreboard)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }

            3 -> {
                val intentScoreboard = Intent(applicationContext, Scoreboard3Player::class.java)
                intentScoreboard.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intentScoreboard)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }

            4 -> {
                val intentScoreboard = Intent(applicationContext, Scoreboard4Player::class.java)
                intentScoreboard.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intentScoreboard)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }

    }

}