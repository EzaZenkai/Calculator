package com.example.win11calculator.engine

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

enum class BinaryOp(val symbol: String, val displaySymbol: String, val precedence: Int) {
    ADD("+", "+", 1),
    SUBTRACT("-", "−", 1),
    MULTIPLY("*", "×", 2),
    DIVIDE("/", "÷", 2);

    fun apply(a: BigDecimal, b: BigDecimal, mc: MathContext = MathContext.DECIMAL64): BigDecimal {
        return when (this) {
            ADD -> a.add(b, mc)
            SUBTRACT -> a.subtract(b, mc)
            MULTIPLY -> a.multiply(b, mc)
            DIVIDE -> {
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    throw CalculatorException(CalculatorError.DIVIDE_BY_ZERO)
                }
                a.divide(b, mc)
            }
        }
    }

    companion object {
        fun fromSymbol(symbol: String): BinaryOp? = entries.find { it.symbol == symbol || it.displaySymbol == symbol }
    }
}

enum class UnaryOp {
    RECIPROCAL, // 1/x
    SQUARE,     // x²
    SQRT,       // ²√x
    PERCENT,    // %
    NEGATE      // +/-
}

enum class CalculatorError {
    DIVIDE_BY_ZERO,
    INVALID_INPUT,
    OVERFLOW
}

class CalculatorException(
    val error: CalculatorError,
    message: String = when (error) {
        CalculatorError.DIVIDE_BY_ZERO -> "Cannot divide by zero"
        CalculatorError.INVALID_INPUT -> "Invalid input"
        CalculatorError.OVERFLOW -> "Overflow"
    }
) : ArithmeticException(message)
