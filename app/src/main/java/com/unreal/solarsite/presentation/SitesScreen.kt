package com.unreal.solarsite.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SitesScreen() {
    Text(
        text = "Sites",
        style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold
        )
    )
}

/*
@Composable
@Preview(showSystemUi = true)
fun ShowSites() {
    SitesScreen()
}*/
