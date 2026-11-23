package com.unreal.solarsite.presentation.boundary

import com.google.android.gms.maps.model.LatLng

data class PolygonBoundaryUiState(
    val points: List<LatLng> = emptyList(),
    val areaSqm: Double = 0.0,
    val isSatellite: Boolean = true
) {
    val isValidPolygon: Boolean get() = points.size >= 3
}