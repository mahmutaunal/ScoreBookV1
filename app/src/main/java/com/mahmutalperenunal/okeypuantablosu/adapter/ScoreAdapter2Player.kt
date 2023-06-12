package com.mahmutalperenunal.okeypuantablosu.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mahmutalperenunal.okeypuantablosu.R
import com.mahmutalperenunal.okeypuantablosu.model.ScoreData2Player

class ScoreAdapter2Player(private val scoreList2Player: ArrayList<ScoreData2Player>) :
    RecyclerView.Adapter<ScoreAdapter2Player.ScoreViewHolder>() {

    private var clickCount = 0

    private lateinit var scoreListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        scoreListener = listener
    }

    inner class ScoreViewHolder(view: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(view) {
        var score1 = view.findViewById<TextView>(R.id.thatRoundScores2Player_player1Score_text)!!
        var score2 = view.findViewById<TextView>(R.id.thatRoundScores2Player_player2Score_text)!!
        var number = view.findViewById<TextView>(R.id.thatRoundScores2Player_roundNumber_text)!!

        var colorBackground =
            view.findViewById<CardView>(R.id.thatRoundScores2Player_scoreColor_background)!!

        private val preferences =
            itemView.context.getSharedPreferences("clickCount2Player", Context.MODE_PRIVATE)
        private val editor = preferences.edit()

        init {

            editor.clear()
            editor.commit()
            editor.putBoolean("selected", false)
            editor.putInt("count", 0)
            editor.apply()

            itemView.setOnClickListener {
                if (clickCount >= 1) {

                    clickCount--
                    scoreList2Player[adapterPosition].isSelected = false
                    editor.putBoolean("selected", false)
                    editor.putInt("count", clickCount)
                    editor.apply()
                    colorBackground.visibility = View.VISIBLE
                    itemView.setBackgroundResource(R.drawable.shape_unselected_cardview)
                    listener.onItemClick(adapterPosition)

                } else {

                    editor.putBoolean("selected", false)
                    scoreList2Player[adapterPosition].isSelected = false
                    listener.onItemClick(adapterPosition)

                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.that_round_score_2_player, parent, false)
        return ScoreViewHolder(view, scoreListener)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val newList = scoreList2Player[position]
        holder.score1.text = newList.player1_score
        holder.score2.text = newList.player2_score
        holder.number.text = newList.gameNumber.toString()

        when (newList.color) {
            "White" -> {
                holder.colorBackground.visibility = View.GONE
            }

            "Red" -> {
                holder.colorBackground.visibility = View.VISIBLE
                holder.colorBackground.setCardBackgroundColor(Color.RED)
            }

            "Blue" -> {
                holder.colorBackground.visibility = View.VISIBLE
                holder.colorBackground.setCardBackgroundColor(Color.BLUE)
            }

            "Yellow" -> {
                holder.colorBackground.visibility = View.VISIBLE
                holder.colorBackground.setCardBackgroundColor(Color.YELLOW)
            }

            "Black" -> {
                holder.colorBackground.visibility = View.VISIBLE
                holder.colorBackground.setCardBackgroundColor(Color.BLACK)
            }

            "Fake" -> {
                holder.colorBackground.visibility = View.VISIBLE
                holder.colorBackground.setCardBackgroundColor(Color.GRAY)
            }
        }

    }

    override fun getItemCount(): Int {
        return scoreList2Player.size
    }

}