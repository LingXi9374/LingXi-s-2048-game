package com.example.lingxis2048

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("game_settings", Context.MODE_PRIVATE)

    companion object {
        const val KEY_SOUND_ENABLED = "sound_enabled"
        const val KEY_SOUND_VOLUME = "sound_volume"
    }

    fun isSoundEnabled(): Boolean {
        return prefs.getBoolean(KEY_SOUND_ENABLED, true)
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit {
            putBoolean(KEY_SOUND_ENABLED, enabled)
        }
    }

    fun getSoundVolume(): Float {
        return prefs.getFloat(KEY_SOUND_VOLUME, 1.0f)
    }

    fun setSoundVolume(volume: Float) {
        prefs.edit {
            putFloat(KEY_SOUND_VOLUME, volume)
        }
    }
}
