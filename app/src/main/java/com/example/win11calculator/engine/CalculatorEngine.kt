package com.example.win11calculator.engine

import java.math.BigDecimal

data class EngineState(
    val primaryDisplay: String = "0",
    val secondaryDisplay: String = "",
    val isError: Boolean = false,
    val error: CalculatorError? = null,
    val errorMessage: String? = null
)

class CalculatorEngine {

    private val tokens = mutableListOf<ExpressionToken>()
    private var currentInput: String = "0"
    private var isNewInput: Boolean = true
    private var isError: Boolean = false
    private var error: CalculatorError? = null
    private var errorMessage: String? = null
    private var lastUnaryRep: String? = null
    private var lastEvaluated: Boolean = false
    private var secondaryText: String = ""

    // Repeat equals operation support (e.g. 5 + 2 = 7, = 9, = 11)
    private var lastRepeatOp: BinaryOp? = null
    private var lastRepeatOperand: BigDecimal? = null
    private var lastRepeatRep: String? = null

    // Callback when a calculation completes and should be recorded in history
    var onCalculationCompleted: ((expression: String, result: String) -> Unit)? = null

    fun getState(): EngineState {
        return EngineState(
            primaryDisplay = if (isError) (errorMessage ?: "Error") else currentInput,
            secondaryDisplay = secondaryText,
            isError = isError,
            error = error,
            errorMessage = errorMessage
        )
    }

    /**
     * Appends a digit (0-9) to the current number.
     */
    fun inputDigit(digit: Int) {
        if (digit !in 0..9) return
        if (isError) clear()

        if (isNewInput || lastEvaluated) {
            currentInput = digit.toString()
            if (lastEvaluated) {
                secondaryText = ""
            }
            isNewInput = false
            lastEvaluated = false
            lastUnaryRep = null
            lastRepeatOp = null
            lastRepeatOperand = null
            lastRepeatRep = null
        } else {
            if (currentInput == "0") {
                currentInput = digit.toString()
            } else if (currentInput.replace("-", "").replace(".", "").length < 16) {
                currentInput += digit.toString()
            }
        }
    }

    /**
     * Appends a decimal point (.) if not already present.
     */
    fun inputDecimal() {
        if (isError) clear()

        if (isNewInput || lastEvaluated) {
            currentInput = "0."
            if (lastEvaluated) {
                secondaryText = ""
            }
            isNewInput = false
            lastEvaluated = false
            lastUnaryRep = null
            lastRepeatOp = null
            lastRepeatOperand = null
            lastRepeatRep = null
        } else if (!currentInput.contains(".")) {
            currentInput += "."
        }
    }

    /**
     * Backspace (⌫) removes the last character entered.
     */
    fun inputBackspace() {
        if (isError) {
            clear()
            return
        }
        if (isNewInput || lastEvaluated) {
            return
        }

        if (currentInput.length > 1) {
            currentInput = currentInput.dropLast(1)
            if (currentInput == "-" || currentInput.isEmpty()) {
                currentInput = "0"
                isNewInput = true
            }
        } else {
            currentInput = "0"
            isNewInput = true
        }
    }

    /**
     * Clear Entry (CE) clears the current input without resetting ongoing expression.
     */
    fun clearEntry() {
        if (isError) {
            clear()
            return
        }
        currentInput = "0"
        isNewInput = true
        lastUnaryRep = null
        secondaryText = buildSecondaryText()
    }

    /**
     * Clear (C) resets everything.
     */
    fun clear() {
        tokens.clear()
        currentInput = "0"
        secondaryText = ""
        isNewInput = true
        isError = false
        error = null
        errorMessage = null
        lastUnaryRep = null
        lastEvaluated = false
        lastRepeatOp = null
        lastRepeatOperand = null
        lastRepeatRep = null
    }

    /**
     * Toggles +/- sign of current operand.
     */
    fun toggleSign() {
        if (isError) return
        if (currentInput == "0") return

        if (!isNewInput && !lastEvaluated) {
            currentInput = if (currentInput.startsWith("-")) {
                currentInput.removePrefix("-")
            } else {
                "-$currentInput"
            }
        } else {
            try {
                val oldRep = lastUnaryRep ?: currentInput
                val value = BigDecimal(currentInput).negate()
                currentInput = ExpressionEvaluator.formatBigDecimal(value)
                lastUnaryRep = "negate($oldRep)"
                updateUnarySecondaryDisplay()
            } catch (e: Exception) {
                // Ignore parse errors
            }
        }
    }

    /**
     * Handles binary operations (+, −, ×, ÷).
     */
    fun inputBinaryOp(op: BinaryOp) {
        if (isError) return

        if (lastEvaluated) {
            tokens.clear()
            val operandVal = try {
                BigDecimal(currentInput)
            } catch (e: Exception) {
                BigDecimal.ZERO
            }
            tokens.add(ExpressionToken.Operand(operandVal, currentInput))
            tokens.add(ExpressionToken.Operator(op))
            secondaryText = "${tokens.first().let { (it as ExpressionToken.Operand).displayRep }} ${op.displaySymbol}"
            isNewInput = true
            lastEvaluated = false
            lastUnaryRep = null
            lastRepeatOp = null
            lastRepeatOperand = null
            lastRepeatRep = null
            return
        }

        // If user already pressed an operator and immediately presses another, replace it
        if (isNewInput && tokens.isNotEmpty() && tokens.last() is ExpressionToken.Operator) {
            tokens[tokens.size - 1] = ExpressionToken.Operator(op)
            secondaryText = buildSecondaryText()
            return
        }

        val operandRep = lastUnaryRep ?: currentInput
        val operandVal = try {
            BigDecimal(currentInput)
        } catch (e: Exception) {
            BigDecimal.ZERO
        }

        tokens.add(ExpressionToken.Operand(operandVal, operandRep))
        tokens.add(ExpressionToken.Operator(op))

        secondaryText = buildSecondaryText()
        isNewInput = true
        lastUnaryRep = null
    }

    /**
     * Handles unary operations: Square, Sqrt, Reciprocal, Percent.
     */
    fun inputUnaryOp(op: UnaryOp) {
        if (isError) return

        val currentVal = try {
            BigDecimal(currentInput)
        } catch (e: Exception) {
            BigDecimal.ZERO
        }

        try {
            when (op) {
                UnaryOp.RECIPROCAL -> {
                    if (currentVal.compareTo(BigDecimal.ZERO) == 0) {
                        secondaryText = if (tokens.isEmpty()) "1/(0)" else "${buildSecondaryText()} 1/(0)"
                        setError(CalculatorError.DIVIDE_BY_ZERO, "Cannot divide by zero")
                        return
                    }
                    val rep = if (lastUnaryRep != null) "1/($lastUnaryRep)" else "1/($currentInput)"
                    val result = BigDecimal.ONE.divide(currentVal, ExpressionEvaluator.MATH_CONTEXT)
                    currentInput = ExpressionEvaluator.formatBigDecimal(result)
                    lastUnaryRep = rep
                    isNewInput = true
                    updateUnarySecondaryDisplay()
                }
                UnaryOp.SQUARE -> {
                    val rep = if (lastUnaryRep != null) "sqr($lastUnaryRep)" else "sqr($currentInput)"
                    val result = currentVal.multiply(currentVal, ExpressionEvaluator.MATH_CONTEXT)
                    currentInput = ExpressionEvaluator.formatBigDecimal(result)
                    lastUnaryRep = rep
                    isNewInput = true
                    updateUnarySecondaryDisplay()
                }
                UnaryOp.SQRT -> {
                    if (currentVal.compareTo(BigDecimal.ZERO) < 0) {
                        secondaryText = if (tokens.isEmpty()) "√($currentInput)" else "${buildSecondaryText()} √($currentInput)"
                        setError(CalculatorError.INVALID_INPUT, "Invalid input")
                        return
                    }
                    val rep = if (lastUnaryRep != null) "√($lastUnaryRep)" else "√($currentInput)"
                    val result = ExpressionEvaluator.sqrt(currentVal)
                    currentInput = ExpressionEvaluator.formatBigDecimal(result)
                    lastUnaryRep = rep
                    isNewInput = true
                    updateUnarySecondaryDisplay()
                }
                UnaryOp.PERCENT -> {
                    val base = calculateBaseForPercentage()
                    val lastOp = findLastOperator()

                    val percentVal = if (base != null && lastOp != null) {
                        when (lastOp) {
                            BinaryOp.ADD, BinaryOp.SUBTRACT -> {
                                base.multiply(currentVal, ExpressionEvaluator.MATH_CONTEXT)
                                    .divide(BigDecimal(100), ExpressionEvaluator.MATH_CONTEXT)
                            }
                            BinaryOp.MULTIPLY, BinaryOp.DIVIDE -> {
                                currentVal.divide(BigDecimal(100), ExpressionEvaluator.MATH_CONTEXT)
                            }
                        }
                    } else {
                        currentVal.divide(BigDecimal(100), ExpressionEvaluator.MATH_CONTEXT)
                    }

                    currentInput = ExpressionEvaluator.formatBigDecimal(percentVal)
                    lastUnaryRep = currentInput
                    isNewInput = true
                    updateUnarySecondaryDisplay()
                }
                UnaryOp.NEGATE -> toggleSign()
            }
        } catch (e: CalculatorException) {
            setError(e.error, e.message)
        } catch (e: ArithmeticException) {
            setError(CalculatorError.OVERFLOW, "Overflow")
        } catch (e: Exception) {
            setError(CalculatorError.INVALID_INPUT, "Invalid input")
        }
    }

    /**
     * Executes equals (=) and evaluates the expression with PEMDAS.
     */
    fun inputEquals() {
        if (isError) return

        if (tokens.isEmpty()) {
            // Repeat last operation if available (e.g. 5 + 2 = 7, pressing = again -> 7 + 2 = 9)
            if (lastRepeatOp != null && lastRepeatOperand != null && lastEvaluated) {
                val currentVal = try {
                    BigDecimal(currentInput)
                } catch (e: Exception) {
                    BigDecimal.ZERO
                }
                val op = lastRepeatOp!!
                val right = lastRepeatOperand!!
                val rightRep = lastRepeatRep ?: right.toPlainString()
                val expr = "${ExpressionEvaluator.formatBigDecimal(currentVal)} ${op.displaySymbol} $rightRep ="

                try {
                    val result = op.apply(currentVal, right, ExpressionEvaluator.MATH_CONTEXT)
                    val formattedResult = ExpressionEvaluator.formatBigDecimal(result)
                    currentInput = formattedResult
                    secondaryText = expr
                    isNewInput = true
                    lastEvaluated = true
                    lastUnaryRep = null
                    onCalculationCompleted?.invoke(expr, formattedResult)
                } catch (e: CalculatorException) {
                    setError(e.error, e.message)
                    secondaryText = expr
                } catch (e: ArithmeticException) {
                    val err = if (e.message?.contains("divide", ignoreCase = true) == true) {
                        CalculatorError.DIVIDE_BY_ZERO
                    } else {
                        CalculatorError.OVERFLOW
                    }
                    setError(err, e.message)
                    secondaryText = expr
                } catch (e: Exception) {
                    setError(CalculatorError.INVALID_INPUT, e.message)
                    secondaryText = expr
                }
                return
            }

            // Normal single number equals (e.g. "5 ="): do not spam duplicates to history
            val rep = lastUnaryRep ?: currentInput
            val expr = "$rep ="
            secondaryText = expr
            isNewInput = true
            lastEvaluated = true
            return
        }

        val operandRep = lastUnaryRep ?: currentInput
        val operandVal = try {
            BigDecimal(currentInput)
        } catch (e: Exception) {
            BigDecimal.ZERO
        }

        // Save last operator and right operand for repeat '=' behavior
        lastRepeatOp = findLastOperator()
        lastRepeatOperand = operandVal
        lastRepeatRep = operandRep

        tokens.add(ExpressionToken.Operand(operandVal, operandRep))
        val expr = "${tokens.joinToString(" ") { token ->
            when (token) {
                is ExpressionToken.Operand -> token.displayRep
                is ExpressionToken.Operator -> token.op.displaySymbol
            }
        }} ="

        try {
            val result = ExpressionEvaluator.evaluate(tokens)
            val formattedResult = ExpressionEvaluator.formatBigDecimal(result)
            currentInput = formattedResult
            secondaryText = expr
            isNewInput = true
            lastEvaluated = true
            lastUnaryRep = null
            tokens.clear()
            onCalculationCompleted?.invoke(expr, formattedResult)
        } catch (e: CalculatorException) {
            tokens.clear()
            setError(e.error, e.message)
            secondaryText = expr
        } catch (e: ArithmeticException) {
            tokens.clear()
            val err = if (e.message?.contains("divide", ignoreCase = true) == true) {
                CalculatorError.DIVIDE_BY_ZERO
            } else {
                CalculatorError.OVERFLOW
            }
            setError(err, e.message)
            secondaryText = expr
        } catch (e: Exception) {
            tokens.clear()
            setError(CalculatorError.INVALID_INPUT, e.message)
            secondaryText = expr
        }
    }

    /**
     * Sets display to a specific value (e.g. from history or memory recall).
     */
    fun setDisplayValue(value: String, expression: String = "") {
        tokens.clear()
        currentInput = value
        secondaryText = expression
        isNewInput = true
        isError = false
        error = null
        errorMessage = null
        lastUnaryRep = null
        lastEvaluated = false
        lastRepeatOp = null
        lastRepeatOperand = null
        lastRepeatRep = null
    }

    fun getCurrentBigDecimal(): BigDecimal {
        if (isError) return BigDecimal.ZERO
        return try {
            BigDecimal(currentInput)
        } catch (e: Exception) {
            BigDecimal.ZERO
        }
    }

    private fun setError(errorType: CalculatorError, msg: String? = null) {
        isError = true
        error = errorType
        errorMessage = msg ?: when (errorType) {
            CalculatorError.DIVIDE_BY_ZERO -> "Cannot divide by zero"
            CalculatorError.INVALID_INPUT -> "Invalid input"
            CalculatorError.OVERFLOW -> "Overflow"
        }
        tokens.clear()
        lastUnaryRep = null
        lastRepeatOp = null
        lastRepeatOperand = null
        lastRepeatRep = null
    }

    private fun buildSecondaryText(): String {
        return tokens.joinToString(" ") { token ->
            when (token) {
                is ExpressionToken.Operand -> token.displayRep
                is ExpressionToken.Operator -> token.op.displaySymbol
            }
        }
    }

    private fun updateUnarySecondaryDisplay() {
        if (tokens.isEmpty()) {
            secondaryText = lastUnaryRep ?: ""
        } else {
            secondaryText = "${buildSecondaryText()} ${lastUnaryRep ?: ""}"
        }
    }

    /**
     * Calculates the evaluated value of all tokens before the last operator,
     * providing the true base for percentages (e.g., in "10 * 5 + 10%", base is 50).
     */
    private fun calculateBaseForPercentage(): BigDecimal? {
        if (tokens.isEmpty()) return null
        return try {
            // Drop trailing operator if present to evaluate the preceding expression
            val tokensToEval = if (tokens.last() is ExpressionToken.Operator) {
                tokens.dropLast(1)
            } else {
                tokens.toList()
            }
            if (tokensToEval.isEmpty()) null else ExpressionEvaluator.evaluate(tokensToEval)
        } catch (e: Exception) {
            null
        }
    }

    private fun findLastOperator(): BinaryOp? {
        for (i in tokens.indices.reversed()) {
            val token = tokens[i]
            if (token is ExpressionToken.Operator) {
                return token.op
            }
        }
        return null
    }
}
