package com.example.okeypuantablosu.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.okeypuantablosu.R
import com.example.okeypuantablosu.data.SkorData4Kisi

class SkorAdapter4Kisi(private val skorList4Kisi: ArrayList<SkorData4Kisi>) : RecyclerView.Adapter<SkorAdapter4Kisi.SkorViewHolder>() {

    inner class SkorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var skor1 = view.findViewById<TextView>(R.id.skor1_text)!!
        var skor2 = view.findViewById<TextView>(R.id.skor2_text)!!
        var skor3 = view.findViewById<TextView>(R.id.skor3_text)!!
        var skor4 = view.findViewById<TextView>(R.id.skor4_text)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_skor_4_kisi, parent, false)
        return SkorViewHolder(view)
    }

    override fun onBindViewHolder(holder: SkorViewHolder, position: Int) {
        val newList = skorList4Kisi[position]
        holder.skor1.text = newList.oyuncu1_skor
        holder.skor2.text = newList.oyuncu2_skor
        holder.skor3.text = newList.oyuncu3_skor
        holder.skor4.text = newList.oyuncu4_skor
    }

    override fun getItemCount(): Int {
        return skorList4Kisi.size
    }

}