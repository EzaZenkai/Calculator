package com.example.win11calculator.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.win11calculator.R
import com.example.win11calculator.ui.theme.Win11TextDisabled
import com.example.win11calculator.ui.theme.Win11TextPrimary

@Composable
fun MemoryBar(
    isMemoryEmpty: Boolean,
    onMemoryClear: () -> Unit,
    onMemoryRecall: () -> Unit,
    onMemoryAdd: () -> Unit,
    onMemorySubtract: () -> Unit,
    onMemoryStore: () -> Unit,
    onOpenMemorySheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(34.dp)
            .padding(horizontal = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MemoryButton(
            text = "MC",
            enabled = !isMemoryEmpty,
            contentDesc = stringResource(id = R.string.btn_mc),
            onClick = onMemoryClear,
            modifier = Modifier.weight(1f)
        )
        MemoryButton(
            text = "MR",
            enabled = !isMemoryEmpty,
            contentDesc = stringResource(id = R.string.btn_mr),
            onClick = onMemoryRecall,
            modifier = Modifier.weight(1f)
        )
        MemoryButton(
            text = "M+",
            enabled = true,
            contentDesc = stringResource(id = R.string.btn_m_plus),
            onClick = onMemoryAdd,
            modifier = Modifier.weight(1f)
        )
        MemoryButton(
            text = "M−",
            enabled = true,
            contentDesc = stringResource(id = R.string.btn_m_minus),
            onClick = onMemorySubtract,
            modifier = Modifier.weight(1f)
        )
        MemoryButton(
            text = "MS",
            enabled = true,
            contentDesc = stringResource(id = R.string.btn_ms),
            onClick = onMemoryStore,
            modifier = Modifier.weight(1f)
        )
        MemoryButton(
            text = "M˅",
            enabled = true,
            contentDesc = stringResource(id = R.string.btn_mv),
            onClick = onOpenMemorySheet,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MemoryButton(
    text: String,
    enabled: Boolean,
    contentDesc: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(32.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = Win11TextPrimary,
            disabledContentColor = Win11TextDisabled,
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
