package com.mahmutalperenunal.okeypuantablosu.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mahmutalperenunal.okeypuantablosu.R
import com.mahmutalperenunal.okeypuantablosu.data.SkorData3Kisi

class SkorAdapter3Kisi (private val skorList3Kisi: ArrayList<SkorData3Kisi>) : RecyclerView.Adapter<SkorAdapter3Kisi.SkorViewHolder>() {

    inner class SkorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var skor1 = view.findViewById<TextView>(R.id.skor1_text)!!
        var skor2 = view.findViewById<TextView>(R.id.skor2_text)!!
        var skor3 = view.findViewById<TextView>(R.id.skor3_text)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_skor_3_kisi, parent, false)
        return SkorViewHolder(view)
    }

    override fun onBindViewHolder(holder: SkorViewHolder, position: Int) {
        val newList = skorList3Kisi[position]
        holder.skor1.text = newList.oyuncu1_skor
        holder.skor2.text = newList.oyuncu2_skor
        holder.skor3.text = newList.oyuncu3_skor
    }

    override fun getItemCount(): Int {
        return skorList3Kisi.size
    }

}