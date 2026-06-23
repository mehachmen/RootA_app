package com.roota.app.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.roota.app.presentation.ui.theme.AccentGreen
import com.roota.app.presentation.ui.theme.DarkSurface
import com.roota.app.presentation.ui.theme.TextFieldShape
import com.roota.app.presentation.ui.theme.TextPrimary
import com.roota.app.presentation.ui.theme.TextSecondary
import com.roota.app.presentation.ui.util.DateFormatters

private class DeadlineMaskTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val masked = DateFormatters.applyDeadlineMask(text.text)
        return TransformedText(
            AnnotatedString(masked),
            OffsetMapping.Identity
        )
    }
}

@Composable
fun DeadlineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = { raw ->
            onValueChange(DateFormatters.applyDeadlineMask(raw.filter { it.isDigit() }))
        },
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = TextFieldShape,
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        visualTransformation = DeadlineMaskTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentGreen,
            unfocusedBorderColor = TextSecondary.copy(alpha = 0.4f),
            focusedLabelColor = AccentGreen,
            unfocusedLabelColor = TextSecondary,
            cursorColor = AccentGreen,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = DarkSurface,
            unfocusedContainerColor = DarkSurface,
            errorBorderColor = com.roota.app.presentation.ui.theme.TagRed,
            errorLabelColor = com.roota.app.presentation.ui.theme.TagRed
        )
    )
}
