package com.example.win11calculator.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.win11calculator.ui.theme.Win11Accent
import com.example.win11calculator.ui.theme.Win11Border
import com.example.win11calculator.ui.theme.Win11ButtonNumberBg
import com.example.win11calculator.ui.theme.Win11ButtonOpBg
import com.example.win11calculator.ui.theme.Win11TextOnAccent
import com.example.win11calculator.ui.theme.Win11TextPrimary

enum class ButtonStyleType {
    NUMBER,
    OPERATOR,
    ACCENT
}

@Composable
fun CalculatorButton(
    text: String,
    modifier: Modifier = Modifier,
    styleType: ButtonStyleType = ButtonStyleType.OPERATOR,
    fontSize: TextUnit = if (styleType == ButtonStyleType.NUMBER) 18.sp else 16.sp,
    fontWeight: FontWeight = if (styleType == ButtonStyleType.NUMBER || styleType == ButtonStyleType.ACCENT) FontWeight.SemiBold else FontWeight.Normal,
    contentDesc: String? = null,
    onClick: () -> Unit
) {
    val bgColor = when (styleType) {
        ButtonStyleType.NUMBER -> Win11ButtonNumberBg
        ButtonStyleType.OPERATOR -> Win11ButtonOpBg
        ButtonStyleType.ACCENT -> Win11Accent
    }

    val textColor = when (styleType) {
        ButtonStyleType.ACCENT -> Win11TextOnAccent
        else -> Win11TextPrimary
    }

    val border = if (styleType == ButtonStyleType.ACCENT) {
        null
    } else {
        BorderStroke(1.dp, Win11Border)
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .padding(2.dp)
            .fillMaxSize()
            .then(
                if (contentDesc != null) {
                    Modifier.semantics { this.contentDescription = contentDesc }
                } else Modifier
            ),
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor,
            contentColor = textColor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (styleType == ButtonStyleType.NUMBER) 0.5.dp else 0.dp,
            pressedElevation = 0.dp
        ),
        border = border,
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = fontWeight,
            color = textColor
        )
    }
}
