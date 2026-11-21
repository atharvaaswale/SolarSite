package com.unreal.solarsite.presentation

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unreal.solarsite.data.dao.SitesDao
import com.unreal.solarsite.data.repository.SitesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SitesUiState(
    val sites: List<SitesDao.Data> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class SitesViewModel(private val repo: SitesRepository): ViewModel() {
    private val _state = MutableStateFlow(SitesUiState())
    val state = _state.asStateFlow()

    init { loadSites() }

    fun loadSites() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repo.getSites()
                .onSuccess { list -> _state.update { it.copy(sites = list, isLoading = false) } }
                .onFailure { e -> _state.update { it.copy(error = e.message, isLoading = false) } }
        }
    }
}