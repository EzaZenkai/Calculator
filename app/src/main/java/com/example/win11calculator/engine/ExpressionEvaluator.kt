package com.example.win11calculator.engine

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.ArrayDeque

sealed class ExpressionToken {
    data class Operand(val value: BigDecimal, val displayRep: String) : ExpressionToken()
    data class Operator(val op: BinaryOp) : ExpressionToken()
}

object ExpressionEvaluator {

    val MATH_CONTEXT: MathContext = MathContext(16, RoundingMode.HALF_UP)

    /**
     * Evaluates a sequence of operands and binary operators using PEMDAS (Shunting-yard algorithm).
     * Returns the calculated BigDecimal result.
     */
    fun evaluate(tokens: List<ExpressionToken>): BigDecimal {
        if (tokens.isEmpty()) return BigDecimal.ZERO

        val values = ArrayDeque<BigDecimal>()
        val ops = ArrayDeque<BinaryOp>()

        fun applyTopOp() {
            if (values.size < 2 || ops.isEmpty()) return
            val b = values.removeLast()
            val a = values.removeLast()
            val op = ops.removeLast()
            val result = op.apply(a, b, MATH_CONTEXT)
            values.addLast(result)
        }

        for (token in tokens) {
            when (token) {
                is ExpressionToken.Operand -> {
                    values.addLast(token.value)
                }
                is ExpressionToken.Operator -> {
                    // While top of ops has greater or equal precedence, pop and apply
                    while (ops.isNotEmpty() && ops.last.precedence >= token.op.precedence) {
                        applyTopOp()
                    }
                    ops.addLast(token.op)
                }
            }
        }

        while (ops.isNotEmpty()) {
            applyTopOp()
        }

        return values.lastOrNull() ?: BigDecimal.ZERO
    }

    /**
     * Pure zero-dependency Newton-Raphson square root for BigDecimal.
     * Compatible with all Android SDK versions (API 24+) and robust against Double overflow/underflow.
     */
    fun sqrt(value: BigDecimal): BigDecimal {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw CalculatorException(CalculatorError.INVALID_INPUT, "Invalid input")
        }
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO
        }

        try {
            val doubleVal = value.toDouble()
            val initialGuess: BigDecimal = if (!doubleVal.isInfinite() && !doubleVal.isNaN() && doubleVal > 0.0) {
                BigDecimal(kotlin.math.sqrt(doubleVal), MATH_CONTEXT)
            } else {
                val exp = value.precision() - value.scale()
                val halfExp = exp / 2
                if (halfExp >= 0) {
                    BigDecimal.TEN.pow(halfExp, MATH_CONTEXT)
                } else {
                    BigDecimal.ONE.divide(BigDecimal.TEN.pow(-halfExp, MATH_CONTEXT), MATH_CONTEXT)
                }
            }

            var x = if (initialGuess.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ONE else initialGuess
            val two = BigDecimal(2)
            val tolerance = BigDecimal("1e-16")

            for (i in 0 until 80) {
                val quotient = value.divide(x, MATH_CONTEXT)
                val nextX = x.add(quotient, MATH_CONTEXT).divide(two, MATH_CONTEXT)
                if (nextX.subtract(x).abs() <= tolerance) {
                    return nextX.round(MATH_CONTEXT)
                }
                x = nextX
            }
            return x.round(MATH_CONTEXT)
        } catch (e: CalculatorException) {
            throw e
        } catch (e: Exception) {
            throw CalculatorException(CalculatorError.OVERFLOW, "Overflow")
        }
    }

    /**
     * Formats a BigDecimal into a user-friendly string:
     * - Trims trailing decimal zeros
     * - Prevents negative zero (-0 -> 0)
     * - Uses plain string if within reasonable scale, scientific if huge
     */
    fun formatBigDecimal(value: BigDecimal): String {
        if (value.compareTo(BigDecimal.ZERO) == 0) return "0"

        val rounded = value.round(MATH_CONTEXT)
        val stripped = rounded.stripTrailingZeros()

        // If the number is within 16 digits, show full plain string
        val plain = stripped.toPlainString()
        if (plain.length > 20 || (plain.startsWith("0.0000000") && stripped.scale() > 10)) {
            return stripped.toString()
        }
        return plain
    }
}
