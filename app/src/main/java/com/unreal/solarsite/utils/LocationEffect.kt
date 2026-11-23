package com.unreal.solarsite.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.launch

data class LocationController(
    val hasPermission: Boolean,
    val isGpsEnabled: Boolean,
    val recenterToCurrentLocation: () -> Unit
)

@Composable
fun rememberUserLocation(cameraPositionState: CameraPositionState): Boolean {
    return rememberLocationController(
        cameraPositionState = cameraPositionState,
        autoMoveOnStart = true,
        targetZoom = 15f
    ).hasPermission
}

/** Full permission + GPS-on check + fresh location controller for PolygonBoundaryScreen. */
@Composable
fun rememberLocationController(
    cameraPositionState: CameraPositionState,
    autoMoveOnStart: Boolean = true,
    targetZoom: Float = 19f
): LocationController {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var hasPermission by remember { mutableStateOf(context.hasLocationPermission()) }
    var isGpsEnabled by remember { mutableStateOf(context.isGpsProviderEnabled()) }

    @SuppressLint("MissingPermission")
    fun fetchAndMoveCamera() {
        if (!context.hasLocationPermission()) return
        isGpsEnabled = context.isGpsProviderEnabled()

        fusedClient.lastLocation.addOnSuccessListener { lastLoc ->
            if (lastLoc != null) {
                scope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(LatLng(lastLoc.latitude, lastLoc.longitude), targetZoom)
                    )
                }
            } else {
                fusedClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    CancellationTokenSource().token
                ).addOnSuccessListener { freshLoc ->
                    if (freshLoc != null) {
                        scope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(LatLng(freshLoc.latitude, freshLoc.longitude), targetZoom)
                            )
                        }
                    }
                }
            }
        }
    }

    val gpsResolutionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        isGpsEnabled = (result.resultCode == Activity.RESULT_OK) || context.isGpsProviderEnabled()
        if (isGpsEnabled) fetchAndMoveCamera()
    }

    fun checkGpsAndFetch() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).build()
        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(request)
            .setAlwaysShow(true)
            .build()

        LocationServices.getSettingsClient(context)
            .checkLocationSettings(settingsRequest)
            .addOnSuccessListener {
                isGpsEnabled = true
                fetchAndMoveCamera()
            }
            .addOnFailureListener { exception ->
                isGpsEnabled = false
                if (exception is ResolvableApiException) {
                    runCatching {
                        gpsResolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    }
                }
            }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        hasPermission = granted
        if (granted) checkGpsAndFetch()
    }

    fun triggerLocationFlow() {
        if (context.hasLocationPermission()) {
            hasPermission = true
            checkGpsAndFetch()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(autoMoveOnStart) {
        if (autoMoveOnStart) triggerLocationFlow()
    }

    return LocationController(
        hasPermission = hasPermission,
        isGpsEnabled = isGpsEnabled,
        recenterToCurrentLocation = ::triggerLocationFlow
    )
}

private fun Context.hasLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

private fun Context.isGpsProviderEnabled(): Boolean {
    val lm = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    return lm?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
            lm?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
}