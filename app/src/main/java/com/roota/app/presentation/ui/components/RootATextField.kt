package com.roota.app.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.roota.app.presentation.ui.theme.AccentGreen
import com.roota.app.presentation.ui.theme.DarkSurface
import com.roota.app.presentation.ui.theme.TextFieldShape
import com.roota.app.presentation.ui.theme.TextPrimary
import com.roota.app.presentation.ui.theme.TextSecondary

@Composable
fun RootATextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    singleLine: Boolean = minLines == 1,
    isError: Boolean = false,
    supportingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        minLines = minLines,
        singleLine = singleLine,
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        shape = TextFieldShape,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentGreen,
            unfocusedBorderColor = TextSecondary.copy(alpha = 0.4f),
            focusedLabelColor = AccentGreen,
            unfocusedLabelColor = TextSecondary,
            cursorColor = AccentGreen,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = DarkSurface,
            unfocusedContainerColor = DarkSurface
        )
    )
}
