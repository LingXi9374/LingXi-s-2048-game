package org.lingxi9374.game2048

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import org.lingxi9374.game2048.R

class SoundManager(context: Context) {

    private val soundPool: SoundPool
    private val soundMap = mutableMapOf<String, Int>()
    private val settingsManager = SettingsManager(context)

    companion object {
        const val MOVE_SOUND = "move"
        const val MERGE_SOUND = "merge"
    }

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        loadSounds(context)
    }

    private fun loadSounds(context: Context) {
        soundMap[MOVE_SOUND] = soundPool.load(context, R.raw.move, 1)
        soundMap[MERGE_SOUND] = soundPool.load(context, R.raw.merge, 1)
    }

    fun playSound(sound: String) {
        if (!settingsManager.isSoundEnabled()) return

        val soundId = soundMap[sound]
        val volume = settingsManager.getSoundVolume()
        soundId?.let {
            soundPool.play(it, volume, volume, 0, 0, 1f)
        }
    }

    fun release() {
        soundPool.release()
    }
}
