package com.example.win11calculator.data

import java.math.BigDecimal

data class MemoryItem(
    val id: Long = System.nanoTime(),
    val value: BigDecimal
)
