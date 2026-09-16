package com.hisham.fdroidstore.data

import android.content.Context
import com.hisham.fdroidstore.model.DownloadRecord
import org.json.JSONArray
import org.json.JSONObject

class DownloadHistoryStore(context: Context) {
    private val preferences = context.getSharedPreferences("download_history", Context.MODE_PRIVATE)

    fun load(): List<DownloadRecord> {
        val saved = preferences.getString("downloads", null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(saved)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.getJSONObject(index)
                    add(
                        DownloadRecord(
                            fileName = item.optString("fileName"),
                            downloadedAt = item.optLong("downloadedAt")
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun add(fileName: String) {
        val records = (listOf(DownloadRecord(fileName)) + load()).take(100)
        val array = JSONArray()

        records.forEach { record ->
            array.put(
                JSONObject().apply {
                    put("fileName", record.fileName)
                    put("downloadedAt", record.downloadedAt)
                }
            )
        }

        preferences.edit()
            .putString("downloads", array.toString())
            .apply()
    }
}
