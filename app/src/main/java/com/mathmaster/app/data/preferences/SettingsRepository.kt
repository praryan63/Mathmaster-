package com.mathmaster.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mathmaster.app.domain.model.Difficulty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "math_master_settings")

enum class ThemeSetting { SYSTEM, LIGHT, DARK }

data class SettingsState(
    val theme: ThemeSetting = ThemeSetting.SYSTEM,
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val autoCheck: Boolean = true,
    val autoNext: Boolean = true,
    val autoNextDelayMs: Long = 900L,
    val timerEnabled: Boolean = false,
    val timerSeconds: Int = 60,
    val dailyGoal: Int = 20,
    val difficulty: Difficulty = Difficulty.EASY,
    val lastTopicId: String = "addition"
)

class SettingsRepository(private val context: Context) {

    private val dataStore = context.applicationContext.settingsDataStore

    val settingsFlow: Flow<SettingsState> = dataStore.data.map { preferences ->
        SettingsState(
            theme = runCatching { ThemeSetting.valueOf(preferences[Keys.THEME] ?: ThemeSetting.SYSTEM.name) }
                .getOrDefault(ThemeSetting.SYSTEM),
            soundEnabled = preferences[Keys.SOUND] ?: true,
            hapticEnabled = preferences[Keys.HAPTIC] ?: true,
            autoCheck = preferences[Keys.AUTO_CHECK] ?: true,
            autoNext = preferences[Keys.AUTO_NEXT] ?: true,
            autoNextDelayMs = preferences[Keys.AUTO_NEXT_DELAY] ?: 900L,
            timerEnabled = preferences[Keys.TIMER_ENABLED] ?: false,
            timerSeconds = preferences[Keys.TIMER_SECONDS] ?: 60,
            dailyGoal = preferences[Keys.DAILY_GOAL] ?: 20,
            difficulty = runCatching { Difficulty.valueOf(preferences[Keys.DIFFICULTY] ?: Difficulty.EASY.name) }
                .getOrDefault(Difficulty.EASY),
            lastTopicId = preferences[Keys.LAST_TOPIC] ?: "addition"
        )
    }.distinctUntilChanged()

    private object Keys {
        val THEME = stringPreferencesKey("theme")
        val SOUND = booleanPreferencesKey("sound")
        val HAPTIC = booleanPreferencesKey("haptic")
        val AUTO_CHECK = booleanPreferencesKey("auto_check")
        val AUTO_NEXT = booleanPreferencesKey("auto_next")
        val AUTO_NEXT_DELAY = longPreferencesKey("auto_next_delay_ms")
        val TIMER_ENABLED = booleanPreferencesKey("timer_enabled")
        val TIMER_SECONDS = intPreferencesKey("timer_seconds")
        val DAILY_GOAL = intPreferencesKey("daily_goal")
        val DIFFICULTY = stringPreferencesKey("difficulty")
        val LAST_TOPIC = stringPreferencesKey("last_topic")
    }

    private suspend fun update(transform: (MutablePreferences) -> Unit) {
        dataStore.edit { preferences -> transform(preferences) }
    }

    suspend fun setTheme(theme: ThemeSetting) = update { it[Keys.THEME] = theme.name }
    suspend fun setSound(enabled: Boolean) = update { it[Keys.SOUND] = enabled }
    suspend fun setHaptic(enabled: Boolean) = update { it[Keys.HAPTIC] = enabled }
    suspend fun setAutoCheck(enabled: Boolean) = update { it[Keys.AUTO_CHECK] = enabled }
    suspend fun setAutoNext(enabled: Boolean) = update { it[Keys.AUTO_NEXT] = enabled }
    suspend fun setAutoNextDelay(delayMs: Long) = update { it[Keys.AUTO_NEXT_DELAY] = delayMs }
    suspend fun setTimerEnabled(enabled: Boolean) = update { it[Keys.TIMER_ENABLED] = enabled }
    suspend fun setTimerSeconds(seconds: Int) = update { it[Keys.TIMER_SECONDS] = seconds.coerceIn(15, 600) }
    suspend fun setDailyGoal(goal: Int) = update { it[Keys.DAILY_GOAL] = goal.coerceIn(1, 500) }
    suspend fun setDifficulty(difficulty: Difficulty) = update { it[Keys.DIFFICULTY] = difficulty.name }
    suspend fun setLastTopic(topicId: String) = update { it[Keys.LAST_TOPIC] = topicId }

}
