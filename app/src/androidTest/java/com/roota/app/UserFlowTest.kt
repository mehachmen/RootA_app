package com.roota.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun createProjectWithColorTag_andCreateTaskWithPriority() {
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Начать", ignoreCase = false).performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Новый проект").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Например: Дипломная работа").performTextInput("Тестовый проект")
        composeRule.onNodeWithText("Сохранить").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Добавить первую задачу").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Название задачи").performTextInput("Задача с приоритетом")
        composeRule.onNodeWithText("дд:мм:гггг").performTextInput("01062026")
        composeRule.onNodeWithText("Высокий").performClick()
        composeRule.onNodeWithText("Сохранить задачу").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Задача с приоритетом").performClick()
        composeRule.onNodeWithText("Приоритет").assertIsDisplayed()
        composeRule.onNodeWithText("Высокий").assertIsDisplayed()
        composeRule.onNodeWithText("Дедлайн").assertIsDisplayed()
    }
}
