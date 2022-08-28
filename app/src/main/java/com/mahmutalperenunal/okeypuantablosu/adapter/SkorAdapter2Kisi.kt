package com.mahmutalperenunal.okeypuantablosu.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mahmutalperenunal.okeypuantablosu.R
import com.mahmutalperenunal.okeypuantablosu.data.SkorData2Kisi

class SkorAdapter2Kisi (private val skorList2Kisi: ArrayList<SkorData2Kisi>) : RecyclerView.Adapter<SkorAdapter2Kisi.SkorViewHolder>() {

    inner class SkorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var skor1 = view.findViewById<TextView>(R.id.skor1_text)!!
        var skor2 = view.findViewById<TextView>(R.id.skor2_text)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_skor_2_kisi, parent, false)
        return SkorViewHolder(view)
    }

    override fun onBindViewHolder(holder: SkorViewHolder, position: Int) {
        val newList = skorList2Kisi[position]
        holder.skor1.text = newList.oyuncu1_skor
        holder.skor2.text = newList.oyuncu2_skor
    }

    override fun getItemCount(): Int {
        return skorList2Kisi.size
    }

}