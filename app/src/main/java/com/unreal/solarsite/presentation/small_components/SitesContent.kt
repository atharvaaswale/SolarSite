package com.unreal.solarsite.presentation.small_components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.unreal.solarsite.R
import com.unreal.solarsite.data.dao.SitesDao
import com.unreal.solarsite.presentation.sites.SitesUiState
import com.unreal.solarsite.ui.theme.mainGreen

@Composable
fun SitesContent(
    state: SitesUiState,
    cameraPositionState: CameraPositionState,
    hasLocationPermission: Boolean,
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteConfirm: (String) -> Unit,
    onRetry: () -> Unit
) {
    var siteToDelete by remember { mutableStateOf<SitesDao.Data?>(null) }

    val mapProperties = remember(hasLocationPermission) {
        MapProperties(isMyLocationEnabled = hasLocationPermission)
    }
    val mapUiSettings = remember {
        MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false)
    }

    // Move camera to the first site with a polygon (or valid lat/lng) once sites load
    LaunchedEffect(state.sites) {
        val targetSite = state.sites.firstOrNull { site ->
            !site.boundaryGeojson?.coordinates?.firstOrNull().isNullOrEmpty() ||
                    (site.latitude != null && site.longitude != null)
        } ?: return@LaunchedEffect

        val firstRingPoint = targetSite.boundaryGeojson?.coordinates
            ?.firstOrNull()
            ?.firstOrNull()
            ?.takeIf { it.size >= 2 }

        val targetLatLng = if (firstRingPoint != null) {
            // GeoJSON coordinates are [longitude, latitude]
            LatLng(firstRingPoint[1], firstRingPoint[0])
        } else {
            LatLng(targetSite.latitude!!, targetSite.longitude!!)
        }

        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(targetLatLng, 16f),
            durationMs = 600
        )
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(0.3f)
                    .background(Color(0xFFE2E8F0))
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = mapProperties,
                    uiSettings = mapUiSettings
                ) {
                    // Draw all saved site polygons on the map
                    state.sites.forEach { site ->
                        val ring = site.boundaryGeojson?.coordinates?.firstOrNull().orEmpty()
                        val points = remember(site.id, ring) {
                            ring.mapNotNull { coord ->
                                if (coord.size >= 2) LatLng(coord[1], coord[0]) else null
                            }
                        }
                        if (points.size >= 3) {
                            Polygon(
                                points = points,
                                fillColor = mainGreen.copy(alpha = 0.25f),
                                strokeColor = mainGreen,
                                strokeWidth = 4f
                            )
                        }
                    }
                }
            }

            Text(
                text = "${state.sites.size} SITES",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color(0xFF64748B),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .fillMaxWidth()
            )

            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
            ) {
                when {
                    state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                    state.error != null -> Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.error)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onRetry) { Text("Retry") }
                    }

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(10.dp, 8.dp, 10.dp, 80.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(items = state.sites, key = { it.id }) { site ->
                            SiteCard(
                                site = site,
                                onEditClick = onEditClick,
                                onDeleteClick = { siteToDelete = site }
                            )
                        }
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = onAddClick,
            icon = { Icon(painterResource(R.drawable.ic_add), null) },
            text = { Text("Add Site") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 5.dp, end = 5.dp),
            containerColor = mainGreen,
            contentColor = Color.White
        )
    }

    // Delete Confirmation Popup
    siteToDelete?.let { site ->
        AlertDialog(
            onDismissRequest = { siteToDelete = null },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    text = "Delete Site?",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"${site.name}\"? This action cannot be undone.",
                    color = Color(0xFF475569)
                )
            },
            dismissButton = {
                Button(
                    onClick = { siteToDelete = null },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF1F5F9),
                        contentColor = Color(0xFF475569)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel", fontWeight = FontWeight.SemiBold)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteConfirm(site.id)
                        siteToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDC2626),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Delete", fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}