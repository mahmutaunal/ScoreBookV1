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
import com.mahmutalperenunal.okeypuantablosu.model.SkorData3Kisi

class SkorAdapter3Kisi (private val skorList3Kisi: ArrayList<SkorData3Kisi>) : RecyclerView.Adapter<SkorAdapter3Kisi.SkorViewHolder>() {

    private var clickCount = 0


    /**
     * item click
     */
    private lateinit var skorListener: OnItemClickListener

    interface OnItemClickListener { fun onItemClick(position: Int) }

    fun setOnItemClickListener(listener: OnItemClickListener) { skorListener = listener }


    inner class SkorViewHolder(view: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(view) {
        var skor1 = view.findViewById<TextView>(R.id.skor1_text)!!
        var skor2 = view.findViewById<TextView>(R.id.skor2_text)!!
        var skor3 = view.findViewById<TextView>(R.id.skor3_text)!!
        var number = view.findViewById<TextView>(R.id.gameNumber_3Kisi_text)!!

        var colorBackground = view.findViewById<CardView>(R.id.color_background)!!

        private val preferences = itemView.context.getSharedPreferences("clickCount3Kisi", Context.MODE_PRIVATE)
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
                    skorList3Kisi[adapterPosition].isSelected = false
                    editor.putBoolean("selected", false)
                    editor.putInt("count", clickCount)
                    editor.apply()
                    colorBackground.visibility = View.VISIBLE
                    itemView.setBackgroundResource(R.drawable.shape_unselected_cardview)
                    listener.onItemClick(adapterPosition)

                } else {

                    editor.putBoolean("selected", false)
                    skorList3Kisi[adapterPosition].isSelected = false
                    listener.onItemClick(adapterPosition)

                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_skor_3_kisi, parent, false)
        return SkorViewHolder(view, skorListener)
    }

    override fun onBindViewHolder(holder: SkorViewHolder, position: Int) {
        val newList = skorList3Kisi[position]
        holder.skor1.text = newList.oyuncu1_skor
        holder.skor2.text = newList.oyuncu2_skor
        holder.skor3.text = newList.oyuncu3_skor
        holder.number.text = newList.gameNumber.toString()

        when (newList.color) {
            "White" -> holder.colorBackground.visibility = View.GONE
            "Red" -> holder.colorBackground.setCardBackgroundColor(Color.RED)
            "Blue" -> holder.colorBackground.setCardBackgroundColor(Color.BLUE)
            "Yellow" -> holder.colorBackground.setCardBackgroundColor(Color.YELLOW)
            "Black" -> holder.colorBackground.setCardBackgroundColor(Color.BLACK)
        }
    }

    override fun getItemCount(): Int {
        return skorList3Kisi.size
    }

}