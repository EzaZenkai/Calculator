package com.example.win11calculator.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.win11calculator.R
import com.example.win11calculator.ui.theme.Win11TextPrimary
import com.example.win11calculator.ui.theme.Win11TextSecondary

@Composable
fun DisplayHeader(
    secondaryText: String,
    primaryText: String,
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        // App top header bar: "Standard" title + History toggle button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.mode_standard),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Win11TextPrimary
            )
            IconButton(onClick = onOpenHistory) {
                Icon(
                    imageVector = Icons.Outlined.History,
                    contentDescription = stringResource(id = R.string.history_title),
                    tint = Win11TextPrimary
                )
            }
        }

        // Secondary expression display (e.g. "12 + 8 =")
        Text(
            text = if (secondaryText.isEmpty()) " " else secondaryText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Win11TextSecondary,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 2.dp)
        )

        // Dynamic font size for primary display based on length
        val primaryFontSize = when {
            primaryText.length > 14 -> 28.sp
            primaryText.length > 10 -> 36.sp
            else -> 46.sp
        }

        // Primary large display (e.g. "20")
        Text(
            text = primaryText,
            fontSize = primaryFontSize,
            fontWeight = FontWeight.SemiBold,
            color = Win11TextPrimary,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
    }
}
