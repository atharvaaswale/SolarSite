package com.unreal.solarsite.utils

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState

/** Asks for location permission, moves the camera to the user, returns whether permission is granted. */
@Composable
fun rememberUserLocation(cameraPositionState: CameraPositionState): Boolean {
    val context = LocalContext.current
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    fun moveToLastLocation() {
        try {
            fusedClient.lastLocation.addOnSuccessListener { loc ->
                loc?.let {
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
                    )
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) moveToLastLocation()
    }

    LaunchedEffect(Unit) {
        if (hasPermission) moveToLastLocation()
        else launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    return hasPermission
}