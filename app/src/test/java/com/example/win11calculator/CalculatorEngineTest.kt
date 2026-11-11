package com.example.win11calculator

import com.example.win11calculator.engine.BinaryOp
import com.example.win11calculator.engine.CalculatorEngine
import com.example.win11calculator.engine.ExpressionEvaluator
import com.example.win11calculator.engine.ExpressionToken
import com.example.win11calculator.engine.UnaryOp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class CalculatorEngineTest {

    private lateinit var engine: CalculatorEngine

    @Before
    fun setUp() {
        engine = CalculatorEngine()
    }

    @Test
    fun testPemdasPrecedence_MultiplyBeforeAdd() {
        // 2 + 3 * 4 = 14
        engine.inputDigit(2)
        engine.inputBinaryOp(BinaryOp.ADD)
        engine.inputDigit(3)
        engine.inputBinaryOp(BinaryOp.MULTIPLY)
        engine.inputDigit(4)
        engine.inputEquals()

        val state = engine.getState()
        assertEquals("14", state.primaryDisplay)
        assertEquals("2 + 3 × 4 =", state.secondaryDisplay)
    }

    @Test
    fun testPemdasPrecedence_DivideBeforeSubtract() {
        // 10 - 6 / 2 = 7
        engine.inputDigit(1)
        engine.inputDigit(0)
        engine.inputBinaryOp(BinaryOp.SUBTRACT)
        engine.inputDigit(6)
        engine.inputBinaryOp(BinaryOp.DIVIDE)
        engine.inputDigit(2)
        engine.inputEquals()

        val state = engine.getState()
        assertEquals("7", state.primaryDisplay)
        assertEquals("10 − 6 ÷ 2 =", state.secondaryDisplay)
    }

    @Test
    fun testBigDecimalAccuracy_NoFloatingPointError() {
        // 0.1 + 0.2 = 0.3
        engine.inputDigit(0)
        engine.inputDecimal()
        engine.inputDigit(1)
        engine.inputBinaryOp(BinaryOp.ADD)
        engine.inputDigit(0)
        engine.inputDecimal()
        engine.inputDigit(2)
        engine.inputEquals()

        val state = engine.getState()
        assertEquals("0.3", state.primaryDisplay)
    }

    @Test
    fun testUnarySquareRoot() {
        // 9 -> sqrt = 3
        engine.inputDigit(9)
        engine.inputUnaryOp(UnaryOp.SQRT)

        val state = engine.getState()
        assertEquals("3", state.primaryDisplay)
        assertEquals("√(9)", state.secondaryDisplay)
    }

    @Test
    fun testUnarySquare() {
        // 4 -> sqr = 16
        engine.inputDigit(4)
        engine.inputUnaryOp(UnaryOp.SQUARE)

        val state = engine.getState()
        assertEquals("16", state.primaryDisplay)
        assertEquals("sqr(4)", state.secondaryDisplay)
    }

    @Test
    fun testUnaryReciprocal() {
        // 8 -> 1/x = 0.125
        engine.inputDigit(8)
        engine.inputUnaryOp(UnaryOp.RECIPROCAL)

        val state = engine.getState()
        assertEquals("0.125", state.primaryDisplay)
        assertEquals("1/(8)", state.secondaryDisplay)
    }

    @Test
    fun testDivisionByZero_ShowsError() {
        // 5 / 0 =
        engine.inputDigit(5)
        engine.inputBinaryOp(BinaryOp.DIVIDE)
        engine.inputDigit(0)
        engine.inputEquals()

        val state = engine.getState()
        assertTrue(state.isError)
        assertEquals("Cannot divide by zero", state.primaryDisplay)
    }

    @Test
    fun testSquareRootNegative_ShowsError() {
        // -9 -> sqrt -> error
        engine.inputDigit(9)
        engine.toggleSign()
        engine.inputUnaryOp(UnaryOp.SQRT)

        val state = engine.getState()
        assertTrue(state.isError)
        assertEquals("Invalid input", state.primaryDisplay)
    }

    @Test
    fun testBackspace() {
        // 123 -> backspace -> 12
        engine.inputDigit(1)
        engine.inputDigit(2)
        engine.inputDigit(3)
        engine.inputBackspace()

        assertEquals("12", engine.getState().primaryDisplay)
    }

    @Test
    fun testClearEntryVsClear() {
        // 10 + 5 -> CE -> 10 + 0 -> 8 = 18
        engine.inputDigit(1)
        engine.inputDigit(0)
        engine.inputBinaryOp(BinaryOp.ADD)
        engine.inputDigit(5)
        engine.clearEntry()
        assertEquals("0", engine.getState().primaryDisplay)

        engine.inputDigit(8)
        engine.inputEquals()
        assertEquals("18", engine.getState().primaryDisplay)
    }

    @Test
    fun testHistoryCallbackTriggered() {
        var recordedExpr = ""
        var recordedRes = ""
        engine.onCalculationCompleted = { expr, res ->
            recordedExpr = expr
            recordedRes = res
        }

        engine.inputDigit(1)
        engine.inputDigit(2)
        engine.inputBinaryOp(BinaryOp.ADD)
        engine.inputDigit(8)
        engine.inputEquals()

        assertEquals("12 + 8 =", recordedExpr)
        assertEquals("20", recordedRes)
    }

    @Test
    fun testRepeatEqualsOperation() {
        // 5 + 2 = 7, = 9, = 11
        engine.inputDigit(5)
        engine.inputBinaryOp(BinaryOp.ADD)
        engine.inputDigit(2)
        engine.inputEquals()
        assertEquals("7", engine.getState().primaryDisplay)

        engine.inputEquals()
        assertEquals("9", engine.getState().primaryDisplay)
        assertEquals("7 + 2 =", engine.getState().secondaryDisplay)

        engine.inputEquals()
        assertEquals("11", engine.getState().primaryDisplay)
        assertEquals("9 + 2 =", engine.getState().secondaryDisplay)
    }

    @Test
    fun testEqualsSpamOnSingleNumber_DoesNotRecordHistory() {
        var callbackCount = 0
        engine.onCalculationCompleted = { _, _ -> callbackCount++ }

        engine.inputDigit(5)
        engine.inputEquals()
        engine.inputEquals()
        engine.inputEquals()

        assertEquals(0, callbackCount)
        assertEquals("5", engine.getState().primaryDisplay)
    }

    @Test
    fun testPercentageWithMultiplicationAndAddition() {
        // 200 + 10 % = 220
        engine.inputDigit(2)
        engine.inputDigit(0)
        engine.inputDigit(0)
        engine.inputBinaryOp(BinaryOp.ADD)
        engine.inputDigit(1)
        engine.inputDigit(0)
        engine.inputUnaryOp(UnaryOp.PERCENT)

        // 10% of 200 is 20
        assertEquals("20", engine.getState().primaryDisplay)

        engine.inputEquals()
        // 200 + 20 = 220
        assertEquals("220", engine.getState().primaryDisplay)
    }

    @Test
    fun testSignToggle_FormatsProperly() {
        engine.inputDigit(5)
        engine.toggleSign()
        assertEquals("-5", engine.getState().primaryDisplay)

        engine.toggleSign()
        assertEquals("5", engine.getState().primaryDisplay)
    }

    @Test
    fun testTokensClearedAfterError_SubsequentCalculationWorks() {
        // 10 / 0 = (error)
        engine.inputDigit(1)
        engine.inputDigit(0)
        engine.inputBinaryOp(BinaryOp.DIVIDE)
        engine.inputDigit(0)
        engine.inputEquals()
        assertTrue(engine.getState().isError)

        // Type 7 + 3 = 10 without manually pressing C
        engine.inputDigit(7)
        assertFalse(engine.getState().isError)
        engine.inputBinaryOp(BinaryOp.ADD)
        engine.inputDigit(3)
        engine.inputEquals()
        assertEquals("10", engine.getState().primaryDisplay)
    }

    @Test
    fun testSquareRootLargeNumber_DoesNotCrash() {
        // 10^320 is larger than Double.MAX_VALUE (~1.8e308)
        val largeNum = BigDecimal("1" + "0".repeat(320))
        val result = ExpressionEvaluator.sqrt(largeNum)
        // sqrt(10^320) = 10^160
        val expected = BigDecimal("1" + "0".repeat(160))
        assertEquals(expected, result)
    }
}
