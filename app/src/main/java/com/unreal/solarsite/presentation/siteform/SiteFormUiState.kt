package com.unreal.solarsite.presentation.siteform

data class SiteFormUiState(
    val isEditMode: Boolean = false,
    val siteId: String? = null,
    val name: String = "",
    val clientName: String = "",
    val status: String = "DRAFT", // DRAFT, READY_FOR_REVIEW, SYNCED
    val roofType: String = "FLAT_CONCRETE", // FLAT_CONCRETE, METAL_SHEET, TILE, OTHER
    val shadingCondition: String = "NONE", // NONE, LOW, MEDIUM, HIGH
    val tiltDegrees: String = "10",
    val azimuthDegrees: String = "180",
    val capacityKwp: String = "",
    val calculatedAreaSqm: Double? = null,
    val polygonPointCount: Int = 0,
    val boundaryGeoJson: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val notes: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSavedSuccess: Boolean = false
)