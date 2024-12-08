package com.example.comepet.utils

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREF_NAME = "UserSession"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_LIKE_PREFIX = "like_"

    fun saveToken(context: Context, token: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun clearToken(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(KEY_TOKEN).apply()
    }

    fun saveLikeStatus(context: Context, itemId: String, isLiked: Boolean) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(KEY_LIKE_PREFIX + itemId, isLiked).apply()
    }

    fun getLikeStatus(context: Context, itemId: String): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_LIKE_PREFIX + itemId, false) // Default is false (not liked)
    }

    // Clear like status for a specific item
    fun clearLikeStatus(context: Context, itemId: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(KEY_LIKE_PREFIX + itemId).apply()
    }
}