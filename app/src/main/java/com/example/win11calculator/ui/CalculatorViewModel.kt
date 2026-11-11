package com.example.win11calculator.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.win11calculator.data.CalculatorRepository
import com.example.win11calculator.data.HistoryItem
import com.example.win11calculator.data.InMemoryCalculatorRepository
import com.example.win11calculator.data.MemoryItem
import com.example.win11calculator.data.SharedPreferencesCalculatorRepository
import com.example.win11calculator.engine.BinaryOp
import com.example.win11calculator.engine.CalculatorEngine
import com.example.win11calculator.engine.EngineState
import com.example.win11calculator.engine.ExpressionEvaluator
import com.example.win11calculator.engine.UnaryOp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

class CalculatorViewModel(
    private val repository: CalculatorRepository = InMemoryCalculatorRepository()
) : ViewModel() {

    // Explicit parameterless constructor for default factory / Compose preview
    constructor() : this(InMemoryCalculatorRepository())

    private val engine = CalculatorEngine()

    private val _engineState = MutableStateFlow(engine.getState())
    val engineState: StateFlow<EngineState> = _engineState.asStateFlow()

    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())
    val history: StateFlow<List<HistoryItem>> = _history.asStateFlow()

    private val _memory = MutableStateFlow<List<MemoryItem>>(emptyList())
    val memory: StateFlow<List<MemoryItem>> = _memory.asStateFlow()

    private val _isHistoryOpen = MutableStateFlow(false)
    val isHistoryOpen: StateFlow<Boolean> = _isHistoryOpen.asStateFlow()

    private val _isMemoryOpen = MutableStateFlow(false)
    val isMemoryOpen: StateFlow<Boolean> = _isMemoryOpen.asStateFlow()

    init {
        // Load persisted history and memory
        _history.value = repository.loadHistory()
        _memory.value = repository.loadMemory()

        engine.onCalculationCompleted = { expression, result ->
            val newItem = HistoryItem(
                expression = expression,
                result = result
            )
            // Add latest to top of history, capped at 100
            val updated = (listOf(newItem) + _history.value).take(SharedPreferencesCalculatorRepository.MAX_HISTORY_ITEMS)
            _history.value = updated
            repository.saveHistory(updated)
        }
    }

    private fun syncState() {
        _engineState.value = engine.getState()
    }

    fun onDigit(digit: Int) {
        engine.inputDigit(digit)
        syncState()
    }

    fun onDecimal() {
        engine.inputDecimal()
        syncState()
    }

    fun onBackspace() {
        engine.inputBackspace()
        syncState()
    }

    fun onClearEntry() {
        engine.clearEntry()
        syncState()
    }

    fun onClear() {
        engine.clear()
        syncState()
    }

    fun onBinaryOp(op: BinaryOp) {
        engine.inputBinaryOp(op)
        syncState()
    }

    fun onUnaryOp(op: UnaryOp) {
        engine.inputUnaryOp(op)
        syncState()
    }

    fun onToggleSign() {
        engine.toggleSign()
        syncState()
    }

    fun onEquals() {
        engine.inputEquals()
        syncState()
    }

    // Memory operations
    fun onMemoryClear() {
        if (_engineState.value.isError) return
        _memory.value = emptyList()
        repository.clearMemory()
    }

    fun onMemoryRecall() {
        if (_engineState.value.isError) return
        val topItem = _memory.value.firstOrNull() ?: return
        engine.setDisplayValue(ExpressionEvaluator.formatBigDecimal(topItem.value))
        syncState()
    }

    fun onMemoryAdd() {
        if (_engineState.value.isError) return
        val current = engine.getCurrentBigDecimal()
        val currentList = _memory.value
        val updated = if (currentList.isEmpty()) {
            listOf(MemoryItem(value = current))
        } else {
            val updatedFirst = currentList.first().let { it.copy(value = it.value.add(current, ExpressionEvaluator.MATH_CONTEXT)) }
            listOf(updatedFirst) + currentList.drop(1)
        }
        _memory.value = updated
        repository.saveMemory(updated)
    }

    fun onMemorySubtract() {
        if (_engineState.value.isError) return
        val current = engine.getCurrentBigDecimal()
        val currentList = _memory.value
        val updated = if (currentList.isEmpty()) {
            listOf(MemoryItem(value = current.negate()))
        } else {
            val updatedFirst = currentList.first().let { it.copy(value = it.value.subtract(current, ExpressionEvaluator.MATH_CONTEXT)) }
            listOf(updatedFirst) + currentList.drop(1)
        }
        _memory.value = updated
        repository.saveMemory(updated)
    }

    fun onMemoryStore() {
        if (_engineState.value.isError) return
        val current = engine.getCurrentBigDecimal()
        val updated = (listOf(MemoryItem(value = current)) + _memory.value).take(SharedPreferencesCalculatorRepository.MAX_MEMORY_ITEMS)
        _memory.value = updated
        repository.saveMemory(updated)
    }

    // Sheet visibility & item selection
    fun setHistoryOpen(open: Boolean) {
        _isHistoryOpen.value = open
    }

    fun setMemoryOpen(open: Boolean) {
        _isMemoryOpen.value = open
    }

    fun onClearHistory() {
        _history.value = emptyList()
        repository.clearHistory()
    }

    fun onSelectHistoryItem(item: HistoryItem) {
        // Restore both the result and the expression in the secondary display
        engine.setDisplayValue(item.result, item.expression)
        syncState()
        setHistoryOpen(false)
    }

    fun onSelectMemoryItem(item: MemoryItem) {
        engine.setDisplayValue(ExpressionEvaluator.formatBigDecimal(item.value))
        syncState()
        setMemoryOpen(false)
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CalculatorViewModel(
                        SharedPreferencesCalculatorRepository(context.applicationContext)
                    ) as T
                }
            }
    }
}
