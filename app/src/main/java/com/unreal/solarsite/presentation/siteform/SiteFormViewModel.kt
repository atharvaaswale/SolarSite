package com.unreal.solarsite.presentation.siteform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.unreal.solarsite.data.dao.AssessmentRequest
import com.unreal.solarsite.data.dao.UpsertSiteRequest
import com.unreal.solarsite.data.repository.SitesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SiteFormViewModel(
    private val repository: SitesRepository
) : ViewModel() {

    private val gson = Gson()
    private val _uiState = MutableStateFlow(SiteFormUiState())
    val uiState: StateFlow<SiteFormUiState> = _uiState.asStateFlow()

    fun initForm(siteId: String?) {
        if (siteId == null || _uiState.value.siteId == siteId) return

        _uiState.update { it.copy(isEditMode = true, siteId = siteId, isLoading = true, error = null) }

        viewModelScope.launch {
/*
            repository.getSites()
                .onSuccess { list ->
                    val site = list.find { it.id == siteId }
                    if (site != null) {
                        val assessment = site.siteAssessments
                        val ring = site.boundaryGeojson?.coordinates?.firstOrNull().orEmpty()
                        val pointCount = if (ring.size > 1 && ring.first() == ring.last()) {
                            ring.size - 1
                        } else {
                            ring.size
                        }
                        val validGeoJson = site.boundaryGeojson?.let { gson.toJson(it) }

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                name = site.name,
                                clientName = site.clientName,
                                status = site.status.uppercase(),
                                latitude = site.latitude,
                                longitude = site.longitude,
                                boundaryGeoJson = validGeoJson,
                                polygonPointCount = pointCount,
                                roofType = normalizeRoofType(assessment?.roofType),
                                shadingCondition = assessment?.shadingCondition?.uppercase() ?: "NONE",
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
*/
            repository.getSiteById(siteId)
                .onSuccess { site ->
                    val assessment = site.siteAssessments
                    val ring = site.boundaryGeojson?.coordinates?.firstOrNull().orEmpty()
                    val pointCount = if (ring.size > 1 && ring.first() == ring.last()) {
                        ring.size - 1
                    } else {
                        ring.size
                    }
                    val validGeoJson = site.boundaryGeojson?.let { gson.toJson(it) }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            name = site.name,
                            clientName = site.clientName,
                            status = site.status.uppercase(),
                            latitude = site.latitude,
                            longitude = site.longitude,
                            boundaryGeoJson = validGeoJson,
                            polygonPointCount = pointCount,
                            roofType = normalizeRoofType(assessment?.roofType),
                            shadingCondition = assessment?.shadingCondition?.uppercase() ?: "NONE",
                            tiltDegrees = (assessment?.tiltDegrees ?: 10).toString(),
                            azimuthDegrees = (assessment?.azimuthDegrees ?: 180).toString(),
                            capacityKwp = assessment?.capacityKwp?.toString() ?: "",
                            calculatedAreaSqm = assessment?.calculatedAreaSqm,
                            notes = assessment?.notes ?: ""
                        )
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

        val parsedGeoJson = current.boundaryGeoJson?.let { raw ->
            runCatching { JsonParser.parseString(raw) }.getOrNull()
        }

        val request = UpsertSiteRequest(
            name = current.name.trim(),
            clientName = current.clientName.trim(),
            status = current.status,
            latitude = current.latitude,
            longitude = current.longitude,
            boundaryGeoJson = parsedGeoJson,
            assessment = AssessmentRequest(
                roofType = current.roofType,
                tiltDegrees = current.tiltDegrees.toIntOrNull() ?: 10,
                azimuthDegrees = current.azimuthDegrees.toIntOrNull() ?: 180,
                shadingCondition = current.shadingCondition,
                capacityKwp = current.capacityKwp.toDoubleOrNull(),
                calculatedAreaSqm = current.calculatedAreaSqm,
                notes = current.notes.trim().ifBlank { null }
            )
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            val result = if (current.isEditMode && current.siteId != null) {
                repository.updateSite(current.siteId, request)
            } else {
                repository.createSite(request)
            }

            result
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, isSavedSuccess = true) }
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isSaving = false, error = err.message ?: "Save failed") }
                }
        }
    }

    private fun normalizeRoofType(raw: String?): String = when (raw?.trim()?.uppercase()) {
        "FLAT_CONCRETE", "RCC FLAT" -> "FLAT_CONCRETE"
        "METAL_SHEET", "METAL DECK", "STANDING SEAM METAL" -> "METAL_SHEET"
        "TILE", "CLAY TILE" -> "TILE"
        "OTHER" -> "OTHER"
        else -> "FLAT_CONCRETE"
    }
}