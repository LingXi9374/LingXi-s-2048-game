package org.lingxi9374.game2048.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.lingxi9374.game2048.HistoryEntry
import org.lingxi9374.game2048.HistoryManager

sealed class HistoryUiState {
    object Loading : HistoryUiState()
    data class Success(val entries: List<HistoryEntry>) : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
}

class HistoryViewModel(private val historyManager: HistoryManager) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            // Delay to allow navigation animation to complete smoothly
            delay(400)

            historyManager.history
                .catch { e ->
                    _uiState.value = HistoryUiState.Error(e.message ?: "An unknown error occurred")
                }
                .collect { historyEntries ->
                    _uiState.value = HistoryUiState.Success(historyEntries)
                }
        }
    }
}