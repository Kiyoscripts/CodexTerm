package com.yourpackage.codexterm.data.model

import com.yourpackage.codexterm.domain.model.PromptHistoryEntry
import org.json.JSONArray
import org.json.JSONObject

object HistoryJson {
    fun encode(entries: List<PromptHistoryEntry>): String {
        return JSONArray().apply {
            entries.forEach { entry ->
                put(
                    JSONObject()
                        .put("id", entry.id)
                        .put("prompt", entry.prompt)
                        .put("language", entry.language)
                        .put("response", entry.response)
                        .put("timestamp", entry.timestamp),
                )
            }
        }.toString()
    }

    fun decode(raw: String?): List<PromptHistoryEntry> {
        if (raw.isNullOrBlank()) return emptyList()
        val jsonArray = JSONArray(raw)
        return buildList {
            for (index in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(index)
                add(
                    PromptHistoryEntry(
                        id = item.optLong("id"),
                        prompt = item.optString("prompt"),
                        language = item.optString("language"),
                        response = item.optString("response"),
                        timestamp = item.optLong("timestamp"),
                    ),
                )
            }
        }
    }
}
