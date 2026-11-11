package com.example.win11calculator

import com.example.win11calculator.data.HistoryItem
import com.example.win11calculator.data.InMemoryCalculatorRepository
import com.example.win11calculator.data.MemoryItem
import com.example.win11calculator.engine.BinaryOp
import com.example.win11calculator.ui.CalculatorViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class CalculatorViewModelTest {

    private lateinit var repository: InMemoryCalculatorRepository
    private lateinit var viewModel: CalculatorViewModel

    @Before
    fun setUp() {
        repository = InMemoryCalculatorRepository()
        viewModel = CalculatorViewModel(repository)
    }

    @Test
    fun testHistoryPersistedOnCalculation() {
        viewModel.onDigit(3)
        viewModel.onBinaryOp(BinaryOp.ADD)
        viewModel.onDigit(4)
        viewModel.onEquals()

        assertEquals(1, viewModel.history.value.size)
        assertEquals("7", viewModel.history.value[0].result)

        // Verify repository saved it
        val loaded = repository.loadHistory()
        assertEquals(1, loaded.size)
        assertEquals("7", loaded[0].result)
    }

    @Test
    fun testHistoryRestoresExpressionAndResult() {
        val testItem = HistoryItem(expression = "15 + 5 =", result = "20")
        viewModel.onSelectHistoryItem(testItem)

        val state = viewModel.engineState.value
        assertEquals("20", state.primaryDisplay)
        assertEquals("15 + 5 =", state.secondaryDisplay)
    }

    @Test
    fun testMemoryOperationsPersisted() {
        viewModel.onDigit(9)
        viewModel.onMemoryStore()

        assertEquals(1, viewModel.memory.value.size)
        assertEquals(BigDecimal("9"), viewModel.memory.value[0].value)

        val savedMemory = repository.loadMemory()
        assertEquals(1, savedMemory.size)
        assertEquals(BigDecimal("9"), savedMemory[0].value)

        viewModel.onMemoryClear()
        assertTrue(viewModel.memory.value.isEmpty())
        assertTrue(repository.loadMemory().isEmpty())
    }

    @Test
    fun testMemoryBlockedDuringError() {
        // Force division by zero
        viewModel.onDigit(5)
        viewModel.onBinaryOp(BinaryOp.DIVIDE)
        viewModel.onDigit(0)
        viewModel.onEquals()

        assertTrue(viewModel.engineState.value.isError)

        // Attempt memory store during error
        viewModel.onMemoryStore()
        assertTrue(viewModel.memory.value.isEmpty())
    }
}
