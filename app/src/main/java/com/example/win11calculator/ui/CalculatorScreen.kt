package com.example.win11calculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.win11calculator.R
import com.example.win11calculator.engine.BinaryOp
import com.example.win11calculator.engine.CalculatorError
import com.example.win11calculator.engine.UnaryOp
import com.example.win11calculator.ui.components.ButtonStyleType
import com.example.win11calculator.ui.components.CalculatorButton
import com.example.win11calculator.ui.components.DisplayHeader
import com.example.win11calculator.ui.components.HistoryBottomSheet
import com.example.win11calculator.ui.components.MemoryBar
import com.example.win11calculator.ui.components.MemoryBottomSheet
import com.example.win11calculator.ui.theme.Win11WindowBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val engineState by viewModel.engineState.collectAsState()
    val history by viewModel.history.collectAsState()
    val memory by viewModel.memory.collectAsState()
    val isHistoryOpen by viewModel.isHistoryOpen.collectAsState()
    val isMemoryOpen by viewModel.isMemoryOpen.collectAsState()

    val historySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val memorySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Localized error message mapping if an error is active
    val displayPrimary = if (engineState.isError) {
        when (engineState.error) {
            CalculatorError.DIVIDE_BY_ZERO -> stringResource(id = R.string.error_div_zero)
            CalculatorError.INVALID_INPUT -> stringResource(id = R.string.error_invalid_input)
            CalculatorError.OVERFLOW -> stringResource(id = R.string.error_overflow)
            null -> engineState.errorMessage ?: "Error"
        }
    } else {
        engineState.primaryDisplay
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Win11WindowBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Win11WindowBg)
        ) {
            // Header & Display
            DisplayHeader(
                secondaryText = engineState.secondaryDisplay,
                primaryText = displayPrimary,
                onOpenHistory = { viewModel.setHistoryOpen(true) }
            )

            // Memory Bar: MC, MR, M+, M-, MS, Mv
            MemoryBar(
                isMemoryEmpty = memory.isEmpty(),
                onMemoryClear = viewModel::onMemoryClear,
                onMemoryRecall = viewModel::onMemoryRecall,
                onMemoryAdd = viewModel::onMemoryAdd,
                onMemorySubtract = viewModel::onMemorySubtract,
                onMemoryStore = viewModel::onMemoryStore,
                onOpenMemorySheet = { viewModel.setMemoryOpen(true) }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Main Calculator Keypad Grid (6 rows x 4 columns)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                // Row 1: %, CE, C, ⌫
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    CalculatorButton(
                        text = "%",
                        styleType = ButtonStyleType.OPERATOR,
                        contentDesc = stringResource(id = R.string.btn_percent),
                        onClick = { viewModel.onUnaryOp(UnaryOp.PERCENT) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorButton(
                        text = "CE",
                        styleType = ButtonStyleType.OPERATOR,
                        contentDesc = stringResource(id = R.string.btn_ce),
                        onClick = viewModel::onClearEntry,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorButton(
                        text = "C",
                        styleType = ButtonStyleType.OPERATOR,
                        contentDesc = stringResource(id = R.string.btn_c),
                        onClick = viewModel::onClear,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorButton(
                        text = "⌫",
                        styleType = ButtonStyleType.OPERATOR,
                        fontSize = 18.sp,
                        contentDesc = stringResource(id = R.string.btn_backspace),
                        onClick = viewModel::onBackspace,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }

                // Row 2: 1/x, x², ²√x, ÷
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    CalculatorButton(
                        text = "1/x",
                        styleType = ButtonStyleType.OPERATOR,
                        contentDesc = stringResource(id = R.string.btn_reciprocal),
                        onClick = { viewModel.onUnaryOp(UnaryOp.RECIPROCAL) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorButton(
                        text = "x²",
                        styleType = ButtonStyleType.OPERATOR,
                        contentDesc = stringResource(id = R.string.btn_square),
                        onClick = { viewModel.onUnaryOp(UnaryOp.SQUARE) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorButton(
                        text = "²√x",
                        styleType = ButtonStyleType.OPERATOR,
                        contentDesc = stringResource(id = R.string.btn_sqrt),
                        onClick = { viewModel.onUnaryOp(UnaryOp.SQRT) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorButton(
                        text = "÷",
                        styleType = ButtonStyleType.OPERATOR,
                        fontSize = 20.sp,
                        contentDesc = stringResource(id = R.string.btn_divide),
                        onClick = { viewModel.onBinaryOp(BinaryOp.DIVIDE) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }

                // Row 3: 7, 8, 9, ×
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    CalculatorButton(
                        text = "7",
                        styleType = ButtonStyleType.NUMBER,
                        onClick = { viewModel.onDigit(7) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorButton(
                        text = "8",
                        styleType = ButtonStyleType.NUMBER,
                        onClick = { viewModel.onDigit(8) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorButton(
                        text = "9",
                        styleType = ButtonStyleType.NUMBER,
                        onClick = { viewModel.onDigit(9) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorButton(
                        text = "×",
                        styleType = ButtonStyleType.OPERATOR,
                        fontSize = 20.sp,
                        contentDesc = stringResource(id = R.string.btn_multiply),
                        onClick = { viewModel.onBinaryOp(BinaryOp.MULTIPLY) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }

                // Row 4: 4, 5, 6, −
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    CalculatorButton(
                        text = "4",
                        styleType = ButtonStyleType.NUMBER,
                        onClick = { viewModel.onDigit(4) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorButton(
                        text = "5",
                        styleType = ButtonStyleType.NUMBER,
                        onClick = { viewModel.onDigit(5) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorButton(
                        text = "6",
                        styleType = ButtonStyleType.NUMBER,
                        onClick = { viewModel.onDigit(6) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorButton(
                        text = "−",
                        styleType = ButtonStyleType.OPERATOR,
                        fontSize = 20.sp,
                        contentDesc = stringResource(id = R.string.btn_subtract),
                        onClick = { viewModel.onBinaryOp(BinaryOp.SUBTRACT) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }

                // Row 5: 1, 2, 3, +
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    CalculatorButton(
                        text = "1",
                        styleType = ButtonStyleType.NUMBER,
                        onClick = { viewModel.onDigit(1) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorButton(
                        text = "2",
                        styleType = ButtonStyleType.NUMBER,
                        onClick = { viewModel.onDigit(2) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorButton(
                        text = "3",
                        styleType = ButtonStyleType.NUMBER,
                        onClick = { viewModel.onDigit(3) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorButton(
                        text = "+",
                        styleType = ButtonStyleType.OPERATOR,
                        fontSize = 20.sp,
                        contentDesc = stringResource(id = R.string.btn_add),
                        onClick = { viewModel.onBinaryOp(BinaryOp.ADD) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }

                // Row 6: +/−, 0, ., =
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    CalculatorButton(
                        text = "+/−",
                        styleType = ButtonStyleType.NUMBER,
                        contentDesc = stringResource(id = R.string.btn_negate),
                        onClick = viewModel::onToggleSign,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorButton(
                        text = "0",
                        styleType = ButtonStyleType.NUMBER,
                        onClick = { viewModel.onDigit(0) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorButton(
                        text = ".",
                        styleType = ButtonStyleType.NUMBER,
                        fontSize = 20.sp,
                        contentDesc = stringResource(id = R.string.btn_dot),
                        onClick = viewModel::onDecimal,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    CalculatorButton(
                        text = "=",
                        styleType = ButtonStyleType.ACCENT,
                        fontSize = 22.sp,
                        contentDesc = stringResource(id = R.string.btn_equals),
                        onClick = viewModel::onEquals,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }
            }
        }

        // History Modal Bottom Sheet
        if (isHistoryOpen) {
            HistoryBottomSheet(
                history = history,
                sheetState = historySheetState,
                onDismiss = { viewModel.setHistoryOpen(false) },
                onClearHistory = viewModel::onClearHistory,
                onItemClick = viewModel::onSelectHistoryItem
            )
        }

        // Memory Modal Bottom Sheet
        if (isMemoryOpen) {
            MemoryBottomSheet(
                memoryList = memory,
                sheetState = memorySheetState,
                onDismiss = { viewModel.setMemoryOpen(false) },
                onClearMemory = viewModel::onMemoryClear,
                onItemClick = viewModel::onSelectMemoryItem
            )
        }
    }
}
