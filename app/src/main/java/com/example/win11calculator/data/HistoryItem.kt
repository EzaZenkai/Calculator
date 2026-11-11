package com.example.win11calculator.data

data class HistoryItem(
    val id: Long = System.nanoTime(),
    val expression: String,
    val result: String
)
