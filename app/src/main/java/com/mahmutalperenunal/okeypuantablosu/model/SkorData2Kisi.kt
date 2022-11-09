package com.mahmutalperenunal.okeypuantablosu.model

//data class for player scores
data class SkorData2Kisi (
    var oyuncu1_skor: String,
    var oyuncu2_skor: String,
    var gameNumber: Int,
    var multiplyNumber: Int,
    var color: String,
    var isSelected: Boolean)