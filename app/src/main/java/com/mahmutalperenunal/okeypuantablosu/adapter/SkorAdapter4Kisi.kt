package com.mahmutalperenunal.okeypuantablosu.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mahmutalperenunal.okeypuantablosu.R
import com.mahmutalperenunal.okeypuantablosu.data.SkorData4Kisi

class SkorAdapter4Kisi(private val skorList4Kisi: ArrayList<SkorData4Kisi>) : RecyclerView.Adapter<SkorAdapter4Kisi.SkorViewHolder>() {

    private var clickCount = 0

    /**
     * longItemClick
     */
    private lateinit var skorListenerLong: OnItemLongClickListener

    interface OnItemLongClickListener { fun onItemLongClick(position: Int) }

    fun setOnItemLongClickListener(listenerLong: OnItemLongClickListener) { skorListenerLong = listenerLong }


    inner class SkorViewHolder(view: View, listenerLong: OnItemLongClickListener) : RecyclerView.ViewHolder(view) {
        var skor1 = view.findViewById<TextView>(R.id.skor1_text)!!
        var skor2 = view.findViewById<TextView>(R.id.skor2_text)!!
        var skor3 = view.findViewById<TextView>(R.id.skor3_text)!!
        var skor4 = view.findViewById<TextView>(R.id.skor4_text)!!
        var number = view.findViewById<TextView>(R.id.gameNumber_4Kisi_text)!!

        val selectIcon: ImageView = itemView.findViewById(R.id.skor4_selectIcon)

        private val preferences = itemView.context.getSharedPreferences("clickCount4Kisi", Context.MODE_PRIVATE)
        private val editor = preferences.edit()

        init {

            itemView.setOnLongClickListener {

                if (clickCount >= 1) {
                    clickCount--
                    editor.putBoolean("selected", false)
                    editor.putInt("count", clickCount)
                    editor.apply()
                    selectIcon.visibility = View.GONE
                    itemView.setBackgroundResource(R.drawable.shape_unselected_cardview)
                    listenerLong.onItemLongClick(adapterPosition)
                    return@setOnLongClickListener true
                } else {
                    clickCount++
                    editor.putBoolean("selected", true)
                    editor.putInt("count", clickCount)
                    editor.apply()
                    selectIcon.visibility = View.VISIBLE
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
        return SkorViewHolder(view, skorListenerLong)
    }

    override fun onBindViewHolder(holder: SkorViewHolder, position: Int) {
        val newList = skorList4Kisi[position]
        holder.skor1.text = newList.oyuncu1_skor
        holder.skor2.text = newList.oyuncu2_skor
        holder.skor3.text = newList.oyuncu3_skor
        holder.skor4.text = newList.oyuncu4_skor
        holder.number.text = newList.gameNumber.toString()
    }

    override fun getItemCount(): Int {
        return skorList4Kisi.size
    }

}