package com.roota.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 1,  // Singleton
    val theme: String = "dark",
    val language: String = "ru"
)