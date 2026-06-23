package com.roota.app.presentation.ui.util

import com.roota.app.domain.model.TaskPriority
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateFormatters {

    private val displayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val inputFormat = SimpleDateFormat("dd:MM:yyyy", Locale.getDefault())

    fun formatDeadlineDisplay(timestamp: Long?): String {
        if (timestamp == null) return "—"
        return displayFormat.format(Date(timestamp))
    }

    fun formatDeadlineInput(timestamp: Long?): String {
        if (timestamp == null) return ""
        return applyDeadlineMask(
            SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(Date(timestamp))
        )
    }

    fun applyDeadlineMask(raw: String): String {
        val digits = raw.filter { it.isDigit() }.take(8)
        return buildString {
            digits.forEachIndexed { index, char ->
                if (index == 2 || index == 4) append(':')
                append(char)
            }
        }
    }

    fun validateDeadlineInput(input: String): String? {
        val trimmed = input.trim()
        if (trimmed.isBlank()) return null

        val digits = trimmed.filter { it.isDigit() }
        if (digits.length < 8) return "Введите дату полностью (дд:мм:гггг)"

        val day = digits.substring(0, 2).toIntOrNull()
        val month = digits.substring(2, 4).toIntOrNull()
        val year = digits.substring(4, 8).toIntOrNull()

        if (day == null || month == null || year == null) {
            return "Неверный формат даты"
        }
        if (day !in 1..31) return "День должен быть от 1 до 31"
        if (month !in 1..12) return "Месяц должен быть от 1 до 12"
        if (year < 1970 || year > 2100) return "Некорректный год"

        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
            val maxDay = getActualMaximum(Calendar.DAY_OF_MONTH)
            if (day > maxDay) return "В этом месяце максимум $maxDay дней"
        }

        return null
    }

    fun parseDeadlineInput(input: String): Long? {
        val trimmed = input.trim()
        if (trimmed.isBlank()) return null
        if (validateDeadlineInput(trimmed) != null) return null
        return runCatching { inputFormat.parse(trimmed)?.time }.getOrNull()
    }
}

fun priorityLabel(priority: TaskPriority): String = when (priority) {
    TaskPriority.LOW -> "Низкий"
    TaskPriority.MEDIUM -> "Средний"
    TaskPriority.HIGH -> "Высокий"
}
