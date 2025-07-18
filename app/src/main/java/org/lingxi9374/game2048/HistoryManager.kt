package org.lingxi9374.game2048

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "history")

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
        }

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
