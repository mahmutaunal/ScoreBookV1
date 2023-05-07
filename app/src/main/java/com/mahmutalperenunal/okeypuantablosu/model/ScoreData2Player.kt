package com.mahmutalperenunal.okeypuantablosu.model

//data class for player scores
data class ScoreData2Player(
    var player1_score: String,
    var player2_score: String,
    var gameNumber: Int,
    var multiplyNumber: Int,
    var color: String,
    var isSelected: Boolean
)