package com.unreal.solarsite.presentation.small_components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.unreal.solarsite.R
import com.unreal.solarsite.presentation.SitesUiState
import com.unreal.solarsite.presentation.small_components.SiteCard
import com.unreal.solarsite.ui.theme.mainGreen

@Composable
fun SitesContent(
    state: SitesUiState,
    cameraPositionState: CameraPositionState,
    hasLocationPermission: Boolean,
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onRetry: () -> Unit
) {
    val mapProperties = remember(hasLocationPermission) {
        MapProperties(isMyLocationEnabled = hasLocationPermission)
    }
    val mapUiSettings = remember {
        MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false)
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
                )
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

            Box(Modifier.fillMaxWidth().weight(0.7f)) {
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
                                onDeleteClick = { /* TODO: viewModel.deleteSite(it) */ }
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
}