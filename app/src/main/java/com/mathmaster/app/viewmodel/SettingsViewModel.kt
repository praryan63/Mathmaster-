package com.mathmaster.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathmaster.app.data.preferences.SettingsRepository
import com.mathmaster.app.data.preferences.SettingsState
import com.mathmaster.app.data.preferences.ThemeSetting
import com.mathmaster.app.data.repository.PracticeRepository
import com.mathmaster.app.domain.model.Difficulty
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: PracticeRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsState> = settingsRepository.settingsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        SettingsState()
    )

    fun setTheme(theme: ThemeSetting) = viewModelScope.launch { settingsRepository.setTheme(theme) }
    fun setSound(enabled: Boolean) = viewModelScope.launch { settingsRepository.setSound(enabled) }
    fun setHaptic(enabled: Boolean) = viewModelScope.launch { settingsRepository.setHaptic(enabled) }
    fun setAutoCheck(enabled: Boolean) = viewModelScope.launch { settingsRepository.setAutoCheck(enabled) }
    fun setAutoNext(enabled: Boolean) = viewModelScope.launch { settingsRepository.setAutoNext(enabled) }
    fun setAutoNextDelay(delayMs: Long) = viewModelScope.launch { settingsRepository.setAutoNextDelay(delayMs) }
    fun setTimerEnabled(enabled: Boolean) = viewModelScope.launch { settingsRepository.setTimerEnabled(enabled) }
    fun setTimerSeconds(seconds: Int) = viewModelScope.launch { settingsRepository.setTimerSeconds(seconds) }
    fun setDailyGoal(goal: Int) = viewModelScope.launch { settingsRepository.setDailyGoal(goal) }
    fun setDifficulty(difficulty: Difficulty) = viewModelScope.launch { settingsRepository.setDifficulty(difficulty) }
    fun resetProgress() = viewModelScope.launch { repository.clearProgress() }
}
