package com.unreal.solarsite.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unreal.solarsite.R
import com.unreal.solarsite.presentation.small_components.SiteCard
import com.unreal.solarsite.presentation.small_components.SiteItemUiModel

@Composable
fun SitesScreen() {

    val mockSites = listOf(
        SiteItemUiModel(
            "1",
            "Axis Industrial Roof",
            "Panvel, Maharashtra",
            550,
            "Ready for Review",
            R.drawable.solar_site_1
        ),
        SiteItemUiModel("2", "Sahara Logistics HUB", "Panvel, Maharashtra", 420, "Draft", R.drawable.solar_site_1),
        SiteItemUiModel("3", "Apex Distribution Facility", "Taloja MIDC", 800, "Synced", R.drawable.solar_site_1),
        SiteItemUiModel("4", "Sunway Cold Storage", "Navi Mumbai", 310, "Ready for Review", R.drawable.solar_site_1)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f)
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Map Preview Area",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
            }
            // 2. Intermediate Site Count Bar
            Text(
                text = "${mockSites.size} SITES",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color(0xFF64748B),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .fillMaxWidth()
            )

            // 3. Sites List Section (70% Height Ratio)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f),
                contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = mockSites,
                    key = { it.id }
                ) { site ->
                    SiteCard(
                        site = site,
                        onEditClick = { siteId -> /* Handle edit */ },
                        onDeleteClick = { siteId -> /* Handle delete */ }
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun ShowSites() {
    SitesScreen()
}
