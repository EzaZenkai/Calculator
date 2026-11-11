package com.example.win11calculator.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal

interface CalculatorRepository {
    fun loadHistory(): List<HistoryItem>
    fun saveHistory(items: List<HistoryItem>)
    fun clearHistory()

    fun loadMemory(): List<MemoryItem>
    fun saveMemory(items: List<MemoryItem>)
    fun clearMemory()
}

class SharedPreferencesCalculatorRepository(
    private val prefs: SharedPreferences
) : CalculatorRepository {

    constructor(context: Context) : this(
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    )

    override fun loadHistory(): List<HistoryItem> {
        val jsonString = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        return try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<HistoryItem>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    HistoryItem(
                        id = obj.optLong("id", System.nanoTime()),
                        expression = obj.getString("expression"),
                        result = obj.getString("result")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun saveHistory(items: List<HistoryItem>) {
        try {
            val jsonArray = JSONArray()
            val capped = items.take(MAX_HISTORY_ITEMS)
            for (item in capped) {
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("expression", item.expression)
                    put("result", item.result)
                }
                jsonArray.put(obj)
            }
            prefs.edit().putString(KEY_HISTORY, jsonArray.toString()).apply()
        } catch (e: Exception) {
            // Ignore write errors
        }
    }

    override fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }

    override fun loadMemory(): List<MemoryItem> {
        val jsonString = prefs.getString(KEY_MEMORY, null) ?: return emptyList()
        return try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<MemoryItem>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val rawVal = obj.getString("value")
                list.add(
                    MemoryItem(
                        id = obj.optLong("id", System.nanoTime()),
                        value = BigDecimal(rawVal)
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun saveMemory(items: List<MemoryItem>) {
        try {
            val jsonArray = JSONArray()
            val capped = items.take(MAX_MEMORY_ITEMS)
            for (item in capped) {
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("value", item.value.toPlainString())
                }
                jsonArray.put(obj)
            }
            prefs.edit().putString(KEY_MEMORY, jsonArray.toString()).apply()
        } catch (e: Exception) {
            // Ignore write errors
        }
    }

    override fun clearMemory() {
        prefs.edit().remove(KEY_MEMORY).apply()
    }

    companion object {
        const val PREFS_NAME = "win11_calculator_prefs"
        const val KEY_HISTORY = "pref_history"
        const val KEY_MEMORY = "pref_memory"
        const val MAX_HISTORY_ITEMS = 100
        const val MAX_MEMORY_ITEMS = 50
    }
}

class InMemoryCalculatorRepository : CalculatorRepository {
    private var history = mutableListOf<HistoryItem>()
    private var memory = mutableListOf<MemoryItem>()

    override fun loadHistory(): List<HistoryItem> = history.toList()
    override fun saveHistory(items: List<HistoryItem>) {
        history = items.take(SharedPreferencesCalculatorRepository.MAX_HISTORY_ITEMS).toMutableList()
    }
    override fun clearHistory() {
        history.clear()
    }

    override fun loadMemory(): List<MemoryItem> = memory.toList()
    override fun saveMemory(items: List<MemoryItem>) {
        memory = items.take(SharedPreferencesCalculatorRepository.MAX_MEMORY_ITEMS).toMutableList()
    }
    override fun clearMemory() {
        memory.clear()
    }
}
