package com.unreal.solarsite.presentation.boundary

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PolygonBoundaryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PolygonBoundaryUiState())
    val uiState: StateFlow<PolygonBoundaryUiState> = _uiState.asStateFlow()

    fun addPoint(latLng: LatLng) {
        _uiState.update { current ->
            val updated = current.points + latLng
            current.copy(
                points = updated,
                areaSqm = calculateArea(updated)
            )
        }
    }

    fun updatePoint(index: Int, newLatLng: LatLng) {
        _uiState.update { current ->
            if (index !in current.points.indices) return@update current
            val updated = current.points.toMutableList().apply { set(index, newLatLng) }
            current.copy(
                points = updated,
                areaSqm = calculateArea(updated)
            )
        }
    }

    fun undoLastPoint() {
        _uiState.update { current ->
            if (current.points.isEmpty()) return@update current
            val updated = current.points.dropLast(1)
            current.copy(
                points = updated,
                areaSqm = calculateArea(updated)
            )
        }
    }

    fun clearPoints() {
        _uiState.update { it.copy(points = emptyList(), areaSqm = 0.0) }
    }

    fun toggleMapType() {
        _uiState.update { it.copy(isSatellite = !it.isSatellite) }
    }

    private fun calculateArea(points: List<LatLng>): Double {
        return if (points.size >= 3) {
            SphericalUtil.computeArea(points)
        } else {
            0.0
        }
    }

    fun buildResultPayload(): BoundaryResult? {
        val pts = _uiState.value.points
        if (pts.size < 3) return null

        // GeoJSON standard requires [longitude, latitude] and closed ring (first == last)
        val ring = JsonArray()
        pts.forEach { pt ->
            val coord = JsonArray().apply {
                add(pt.longitude)
                add(pt.latitude)
            }
            ring.add(coord)
        }
        val closingCoord = JsonArray().apply {
            add(pts.first().longitude)
            add(pts.first().latitude)
        }
        ring.add(closingCoord)

        val coordinates = JsonArray().apply { add(ring) }
        val geoJson = JsonObject().apply {
            addProperty("type", "Polygon")
            add("coordinates", coordinates)
        }

        val centroidLat = pts.map { it.latitude }.average()
        val centroidLng = pts.map { it.longitude }.average()

        return BoundaryResult(
            areaSqm = _uiState.value.areaSqm,
            geoJsonString = geoJson.toString(),
            pointCount = pts.size,
            centroidLat = centroidLat,
            centroidLng = centroidLng
        )
    }

    fun loadInitialGeoJson(geoJson: String?) {
        if (geoJson.isNullOrBlank() || _uiState.value.points.isNotEmpty()) return
        runCatching {
            val root = com.google.gson.JsonParser.parseString(geoJson).asJsonObject
            val ring = root.getAsJsonArray("coordinates")?.get(0)?.asJsonArray ?: return
            val parsedPoints = mutableListOf<LatLng>()
            for (i in 0 until ring.size()) {
                val coord = ring[i].asJsonArray
                val lng = coord[0].asDouble
                val lat = coord[1].asDouble
                parsedPoints.add(LatLng(lat, lng))
            }
            // Drop the duplicate GeoJSON closing vertex so pins don't overlap
            if (parsedPoints.size > 1 && parsedPoints.first() == parsedPoints.last()) {
                parsedPoints.removeAt(parsedPoints.lastIndex)
            }
            if (parsedPoints.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        points = parsedPoints,
                        areaSqm = calculateArea(parsedPoints)
                    )
                }
            }
        }
    }
}

data class BoundaryResult(
    val areaSqm: Double,
    val geoJsonString: String,
    val pointCount: Int,
    val centroidLat: Double,
    val centroidLng: Double
)