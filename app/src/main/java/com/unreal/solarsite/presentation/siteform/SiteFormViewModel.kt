package com.unreal.solarsite.presentation.siteform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unreal.solarsite.data.repository.SitesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SiteFormViewModel(
    private val repository: SitesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SiteFormUiState())
    val uiState: StateFlow<SiteFormUiState> = _uiState.asStateFlow()

    fun initForm(siteId: String?) {
        if (siteId == null || _uiState.value.siteId == siteId) return

        _uiState.update { it.copy(isEditMode = true, siteId = siteId, isLoading = true, error = null) }

        viewModelScope.launch {
            // Replace with repository.getSiteById(siteId) once wired in SitesRepository
            repository.getSites()
                .onSuccess { list ->
                    val site = list.find { it.id == siteId }
                    if (site != null) {
                        val assessment = site.siteAssessments
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                name = site.name ?: "",
                                clientName = site.clientName ?: "",
                                status = site.status ?: "DRAFT",
                                latitude = site.latitude,
                                longitude = site.longitude,
                                boundaryGeoJson = site.boundaryGeojson?.toString(),
                                roofType = assessment?.roofType ?: "FLAT_CONCRETE",
                                shadingCondition = assessment?.shadingCondition ?: "NONE",
                                tiltDegrees = (assessment?.tiltDegrees ?: 10).toString(),
                                azimuthDegrees = (assessment?.azimuthDegrees ?: 180).toString(),
                                capacityKwp = assessment?.capacityKwp?.toString() ?: "",
                                calculatedAreaSqm = assessment?.calculatedAreaSqm,
                                notes = assessment?.notes ?: ""
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = "Site not found") }
                    }
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isLoading = false, error = err.message ?: "Failed to load site") }
                }
        }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, error = null) }
    fun onClientNameChange(value: String) = _uiState.update { it.copy(clientName = value, error = null) }
    fun onStatusChange(value: String) = _uiState.update { it.copy(status = value) }
    fun onRoofTypeChange(value: String) = _uiState.update { it.copy(roofType = value) }
    fun onShadingChange(value: String) = _uiState.update { it.copy(shadingCondition = value) }
    fun onTiltChange(value: String) = _uiState.update { it.copy(tiltDegrees = value.filter { c -> c.isDigit() }) }
    fun onAzimuthChange(value: String) = _uiState.update { it.copy(azimuthDegrees = value.filter { c -> c.isDigit() }) }
    fun onCapacityChange(value: String) = _uiState.update { it.copy(capacityKwp = value) }
    fun onNotesChange(value: String) = _uiState.update { it.copy(notes = value) }

    fun onPolygonResult(areaSqm: Double, geoJson: String, pointCount: Int, lat: Double?, lng: Double?) {
        _uiState.update {
            it.copy(
                calculatedAreaSqm = areaSqm,
                boundaryGeoJson = geoJson,
                polygonPointCount = pointCount,
                latitude = lat ?: it.latitude,
                longitude = lng ?: it.longitude
            )
        }
    }

    fun saveSite() {
        val current = _uiState.value
        if (current.name.isBlank() || current.clientName.isBlank()) {
            _uiState.update { it.copy(error = "Site Name and Client Name are required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            // Hook your repository.createSite(...) / repository.updateSite(...) call here
            // For now simulates completion until you connect the POST/PUT Retrofit methods
            _uiState.update { it.copy(isSaving = false, isSavedSuccess = true) }
        }
    }
}