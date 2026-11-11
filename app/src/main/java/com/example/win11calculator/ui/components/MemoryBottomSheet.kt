package com.example.win11calculator.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.win11calculator.R
import com.example.win11calculator.data.MemoryItem
import com.example.win11calculator.engine.ExpressionEvaluator
import com.example.win11calculator.ui.theme.Win11Surface
import com.example.win11calculator.ui.theme.Win11TextPrimary
import com.example.win11calculator.ui.theme.Win11TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryBottomSheet(
    memoryList: List<MemoryItem>,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onClearMemory: () -> Unit,
    onItemClick: (MemoryItem) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Win11Surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .heightIn(max = 480.dp)
        ) {
            // Header: Title + Trash icon to clear all memory
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.memory_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Win11TextPrimary
                )
                if (memoryList.isNotEmpty()) {
                    IconButton(onClick = onClearMemory) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(id = R.string.clear_memory),
                            tint = Win11TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (memoryList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.memory_empty),
                        fontSize = 14.sp,
                        color = Win11TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(memoryList, key = { it.id }) { item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onItemClick(item) }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = ExpressionEvaluator.formatBigDecimal(item.value),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Win11TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
