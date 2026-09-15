package com.hisham.fdroidstore.data

import android.content.Context
import com.hisham.fdroidstore.model.SearchApp
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FavoriteStore(context: Context) {
    private val preferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    fun load(): List<SearchApp> {
        val saved = preferences.getString("favorite_apps", null) ?: return emptyList()
        return runCatching {
            json.decodeFromString<List<SearchApp>>(saved)
        }.getOrDefault(emptyList())
    }

    fun save(apps: List<SearchApp>) {
        preferences.edit()
            .putString("favorite_apps", json.encodeToString(apps))
            .apply()
    }
}
