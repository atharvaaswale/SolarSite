package com.unreal.solarsite.presentation.boundary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationDisabled
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.unreal.solarsite.utils.rememberLocationController
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

private val ActiveRust = Color(0xFF8B3A00)
private val TextPrimary = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val SurfaceMuted = Color(0xFFF1F5F9)

@Composable
fun PolygonBoundaryScreen(
    initialGeoJson: String? = null,
    onBackClick: () -> Unit,
    onConfirmBoundary: (BoundaryResult) -> Unit,
    viewModel: PolygonBoundaryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(initialGeoJson) {
        viewModel.loadInitialGeoJson(initialGeoJson)
    }
    // Neutral India center fallback only for the split-second before GPS resolves
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(20.5937, 78.9629), 5f)
    }
    // Only auto-move to current GPS if we aren't editing an existing polygon
    val locationController = rememberLocationController(
        cameraPositionState = cameraPositionState,
        autoMoveOnStart = initialGeoJson.isNullOrBlank(),
        targetZoom = 19f
    )
    // Center camera on existing polygon when editing a saved boundary
    LaunchedEffect(uiState.points.firstOrNull()) {
        if (!initialGeoJson.isNullOrBlank()) {
            uiState.points.firstOrNull()?.let { firstPoint ->
                cameraPositionState.position = CameraPosition.fromLatLngZoom(firstPoint, 19f)
            }
        }
    }

    PolygonBoundaryContent(
        uiState = uiState,
        cameraPositionState = cameraPositionState,
        hasLocationPermission = locationController.hasPermission,
        isGpsEnabled = locationController.isGpsEnabled,
        onRecenterClick = locationController.recenterToCurrentLocation,
        onMapClick = viewModel::addPoint,
        onMarkerDragEnd = viewModel::updatePoint,
        onUndoClick = viewModel::undoLastPoint,
        onClearClick = viewModel::clearPoints,
        onToggleMapType = viewModel::toggleMapType,
        onBackClick = onBackClick,
        onConfirmClick = {
            viewModel.buildResultPayload()?.let(onConfirmBoundary)
        }
    )
}

@Composable
fun PolygonBoundaryContent(
    uiState: PolygonBoundaryUiState,
    cameraPositionState: CameraPositionState,
    hasLocationPermission: Boolean,
    isGpsEnabled: Boolean,
    onRecenterClick: () -> Unit,
    onMapClick: (LatLng) -> Unit,
    onMarkerDragEnd: (Int, LatLng) -> Unit,
    onUndoClick: () -> Unit,
    onClearClick: () -> Unit,
    onToggleMapType: () -> Unit,
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = if (uiState.isSatellite) MapType.HYBRID else MapType.NORMAL,
                isMyLocationEnabled = hasLocationPermission
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = true,
                myLocationButtonEnabled = false
            ),
            onMapClick = onMapClick
        ) {
            // Draw closed polygon when >= 3 points, or polyline when 2 points
            if (uiState.points.size >= 3) {
                Polygon(
                    points = uiState.points,
                    fillColor = ActiveRust.copy(alpha = 0.28f),
                    strokeColor = ActiveRust,
                    strokeWidth = 6f
                )
            } else if (uiState.points.size == 2) {
                Polyline(
                    points = uiState.points,
                    color = ActiveRust,
                    width = 6f
                )
            }

            // Draggable vertex pins
            uiState.points.forEachIndexed { index, point ->
                val markerState = rememberUpdatedMarkerState(position = point)

                LaunchedEffect(markerState.dragState) {
                    if (markerState.dragState == DragState.END) {
                        onMarkerDragEnd(index, markerState.position)
                    }
                }

                Marker(
                    state = markerState,
                    title = "Corner ${index + 1}",
                    snippet = "Drag to fine-tune",
                    draggable = true
                )
            }
        }

        // Top Floating Controls (Back + Title + Satellite/Map Toggle)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onBackClick,
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 4.dp
            ) {
                Text(
                    text = if (uiState.points.isEmpty()) "Tap roof corners on map" else "${uiState.points.size} corners pinned",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }

            Surface(
                onClick = onToggleMapType,
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "Toggle Satellite",
                        tint = if (uiState.isSatellite) ActiveRust else TextPrimary
                    )
                }
            }
        }

        // Floating "My Location / Enable GPS" button
        FloatingActionButton(
            onClick = onRecenterClick,
            containerColor = Color.White,
            contentColor = if (isGpsEnabled && hasLocationPermission) ActiveRust else MaterialTheme.colorScheme.error,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 16.dp, bottom = 168.dp)
                .size(48.dp)
        ) {
            Icon(
                imageVector = if (isGpsEnabled && hasLocationPermission) Icons.Default.MyLocation else Icons.Default.LocationDisabled,
                contentDescription = "Current Location"
            )
        }

        // Bottom Area & Action Card
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "USABLE ROOF AREA",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextSecondary
                        )
                        Text(
                            text = String.format(Locale.US, "%.2f m²", uiState.areaSqm),
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (uiState.isValidPolygon) ActiveRust else TextPrimary
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalIconButton(
                            onClick = onUndoClick,
                            enabled = uiState.points.isNotEmpty(),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = SurfaceMuted,
                                contentColor = TextPrimary
                            )
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo last pin")
                        }

                        FilledTonalIconButton(
                            onClick = onClearClick,
                            enabled = uiState.points.isNotEmpty(),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = SurfaceMuted,
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Clear all pins")
                        }
                    }
                }

                Button(
                    onClick = onConfirmClick,
                    enabled = uiState.isValidPolygon,
                    colors = ButtonDefaults.buttonColors(containerColor = ActiveRust),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uiState.isValidPolygon) "Confirm Boundary" else "Pin at least 3 corners",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }
    }
}