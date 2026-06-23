package com.roota.app.presentation.ui.util

import com.roota.app.domain.model.TaskPriority
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Test

class DateFormattersTest {

    @Test
    fun applyDeadlineMask_formatsDigitsWithColons() {
        assertEquals("01:06:2026", DateFormatters.applyDeadlineMask("01062026"))
    }

    @Test
    fun applyDeadlineMask_stripsNonDigits() {
        assertEquals("01:06:2026", DateFormatters.applyDeadlineMask("01-06-2026"))
    }

    @Test
    fun validateDeadlineInput_blankInput_returnsNull() {
        assertNull(DateFormatters.validateDeadlineInput(""))
        assertNull(DateFormatters.validateDeadlineInput("   "))
    }

    @Test
    fun validateDeadlineInput_incompleteDate_returnsError() {
        assertEquals(
            "Введите дату полностью (дд:мм:гггг)",
            DateFormatters.validateDeadlineInput("01:06")
        )
    }

    @Test
    fun validateDeadlineInput_invalidDayInFebruary_returnsError() {
        assertEquals(
            "В этом месяце максимум 29 дней",
            DateFormatters.validateDeadlineInput("30:02:2024")
        )
    }

    @Test
    fun validateDeadlineInput_validDate_returnsNull() {
        assertNull(DateFormatters.validateDeadlineInput("01:06:2026"))
    }

    @Test
    fun parseDeadlineInput_validDate_returnsTimestamp() {
        assertNotNull(DateFormatters.parseDeadlineInput("01:06:2026"))
    }

    @Test
    fun parseDeadlineInput_invalidDate_returnsNull() {
        assertNull(DateFormatters.parseDeadlineInput("31:02:2026"))
    }

    @Test
    fun formatDeadlineDisplay_null_returnsDash() {
        assertEquals("—", DateFormatters.formatDeadlineDisplay(null))
    }

    @Test
    fun priorityLabel_mapsAllPriorities() {
        assertEquals("Низкий", priorityLabel(TaskPriority.LOW))
        assertEquals("Средний", priorityLabel(TaskPriority.MEDIUM))
        assertEquals("Высокий", priorityLabel(TaskPriority.HIGH))
    }
}
