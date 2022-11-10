package com.mahmutalperenunal.okeypuantablosu.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mahmutalperenunal.okeypuantablosu.R
import com.mahmutalperenunal.okeypuantablosu.model.SkorData4Kisi

class SkorAdapter4Kisi(private val skorList4Kisi: ArrayList<SkorData4Kisi>) : RecyclerView.Adapter<SkorAdapter4Kisi.SkorViewHolder>() {

    private var clickCount = 0


    /**
     * item click
     */
    private lateinit var skorListener: OnItemClickListener

    interface OnItemClickListener { fun onItemClick(position: Int) }

    fun setOnItemClickListener(listener: OnItemClickListener) { skorListener = listener }

    /**
     * longItemClick
     */
    private lateinit var skorListenerLong: OnItemLongClickListener

    interface OnItemLongClickListener { fun onItemLongClick(position: Int) }

    fun setOnItemLongClickListener(listenerLong: OnItemLongClickListener) { skorListenerLong = listenerLong }


    inner class SkorViewHolder(view: View, listener: OnItemClickListener, listenerLong: OnItemLongClickListener) : RecyclerView.ViewHolder(view) {
        var skor1 = view.findViewById<TextView>(R.id.skor1_text)!!
        var skor2 = view.findViewById<TextView>(R.id.skor2_text)!!
        var skor3 = view.findViewById<TextView>(R.id.skor3_text)!!
        var skor4 = view.findViewById<TextView>(R.id.skor4_text)!!
        var number = view.findViewById<TextView>(R.id.gameNumber_4Kisi_text)!!

        var colorBackground = view.findViewById<CardView>(R.id.color_background)!!

        private val selectIcon: ImageView = itemView.findViewById(R.id.skor4_selectIcon)

        private val preferences = itemView.context.getSharedPreferences("clickCount4Kisi", Context.MODE_PRIVATE)
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
                    skorList4Kisi[adapterPosition].isSelected = false
                    editor.putBoolean("selected", false)
                    editor.putInt("count", clickCount)
                    editor.apply()
                    selectIcon.visibility = View.GONE
                    colorBackground.visibility = View.VISIBLE
                    itemView.setBackgroundResource(R.drawable.shape_unselected_cardview)
                    listener.onItemClick(adapterPosition)

                } else {

                    editor.putBoolean("selected", false)
                    skorList4Kisi[adapterPosition].isSelected = false
                    listener.onItemClick(adapterPosition)

                }
            }

            itemView.setOnLongClickListener {

                if (clickCount >= 1) {
                    clickCount--
                    skorList4Kisi[adapterPosition].isSelected = true
                    editor.putBoolean("selected", false)
                    editor.putInt("count", clickCount)
                    editor.apply()
                    selectIcon.visibility = View.GONE
                    colorBackground.visibility = View.VISIBLE
                    itemView.setBackgroundResource(R.drawable.shape_unselected_cardview)
                    listenerLong.onItemLongClick(adapterPosition)
                    return@setOnLongClickListener true
                } else {
                    clickCount++
                    skorList4Kisi[adapterPosition].isSelected = true
                    editor.putBoolean("selected", true)
                    editor.putInt("count", clickCount)
                    editor.apply()
                    selectIcon.visibility = View.VISIBLE
                    colorBackground.visibility = View.GONE
                    itemView.setBackgroundResource(R.drawable.shape_selected_cardview)
                    listenerLong.onItemLongClick(adapterPosition)
                    return@setOnLongClickListener true
                }

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_skor_4_kisi, parent, false)
        return SkorViewHolder(view, skorListener, skorListenerLong)
    }

    override fun onBindViewHolder(holder: SkorViewHolder, position: Int) {
        val newList = skorList4Kisi[position]
        holder.skor1.text = newList.oyuncu1_skor
        holder.skor2.text = newList.oyuncu2_skor
        holder.skor3.text = newList.oyuncu3_skor
        holder.skor4.text = newList.oyuncu4_skor
        holder.number.text = newList.gameNumber.toString()

        when (newList.color) {
            "White" -> holder.colorBackground.setCardBackgroundColor(Color.WHITE)
            "Red" -> holder.colorBackground.setCardBackgroundColor(Color.RED)
            "Blue" -> holder.colorBackground.setCardBackgroundColor(Color.BLUE)
            "Yellow" -> holder.colorBackground.setCardBackgroundColor(Color.YELLOW)
            "Black" -> holder.colorBackground.setCardBackgroundColor(Color.BLACK)
        }
    }

    override fun getItemCount(): Int {
        return skorList4Kisi.size
    }

}