package com.example.reddit.domain.repository

interface SharedPreferencesRepository {
    fun getBoolean(key: String, defaultValue : Boolean): Boolean
    fun saveBoolean(key: String, value : Boolean)
}