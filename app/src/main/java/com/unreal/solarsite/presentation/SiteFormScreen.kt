package com.unreal.solarsite.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.unreal.solarsite.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteForm(
    siteId: String?,
    onBackClick: () -> Unit,
    onOpenBoundaryMap: () -> Unit
){
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (siteId == null) "Add Site" else "Edit Site") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(painter = painterResource(R.drawable.ic_arrow_back), contentDescription = "back")
                    }
                }
            )
        }
    ) {paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Form fields, Map trigger field, Submit button
        }
    }
}

@Preview
@Composable
fun ShowForm() {
    SiteForm(
        siteId = null,
        onBackClick = {},
        onOpenBoundaryMap = {}
    )
}