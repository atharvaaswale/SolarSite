package com.unreal.solarsite.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.unreal.solarsite.presentation.small_components.SitesContent
import com.unreal.solarsite.utils.rememberUserLocation
import org.koin.androidx.compose.koinViewModel

@Composable
fun SitesScreen(
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit,
    viewModel: SitesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(19.0760, 72.8777), 10f) // Mumbai
    }
    val hasLocationPermission = rememberUserLocation(cameraPositionState)

    SitesContent(
        state = state,
        cameraPositionState = cameraPositionState,
        hasLocationPermission = hasLocationPermission,
        onAddClick = onAddClick,
        onEditClick = onEditClick,
        onRetry = viewModel::loadSites
    )
}