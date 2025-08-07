@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package org.lingxi9374.game2048

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json



@Serializable
data class HistoryEntry(
    val score: Int,
    val timeElapsed: Long,
    val maxTile: Int,
    val timestamp: Long = System.currentTimeMillis()
)

class HistoryManager(private val context: Context) {

    private val HISTORY_KEY = stringPreferencesKey("game_history")

    val history: Flow<List<HistoryEntry>> = context.dataStore.data
        .map { preferences ->
            val jsonString = preferences[HISTORY_KEY]
            if (jsonString != null) {
                Json.decodeFromString<List<HistoryEntry>>(jsonString)
            } else {
                emptyList()
            }
        }.flowOn(Dispatchers.Default)

    suspend fun addHistoryEntry(entry: HistoryEntry) {
        context.dataStore.edit { preferences ->
            val currentHistoryJson = preferences[HISTORY_KEY]
            val currentHistory = if (currentHistoryJson != null) {
                Json.decodeFromString<MutableList<HistoryEntry>>(currentHistoryJson)
            } else {
                mutableListOf()
            }
            currentHistory.add(0, entry) // Add new entry to the top
            preferences[HISTORY_KEY] = Json.encodeToString(currentHistory)
        }
    }
}
