package com.unreal.solarsite.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.unreal.solarsite.R
import com.unreal.solarsite.data.dao.SitesDao
import com.unreal.solarsite.presentation.small_components.SurveyListItemCard
import com.unreal.solarsite.ui.theme.DarkBlueText
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreen(
    onNewSurveyClick: () -> Unit,
    onSiteClick: (String) -> Unit,
    onSeeAllSitesClick: () -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DashboardContent(
        uiState = uiState,
        onNewSurveyClick = onNewSurveyClick,
        onSiteClick = onSiteClick,
        onSeeAllSitesClick = onSeeAllSitesClick,
        onRetryClick = viewModel::loadDashboard
    )
}

@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    onNewSurveyClick: () -> Unit,
    onSiteClick: (String) -> Unit,
    onSeeAllSitesClick: () -> Unit,
    onRetryClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Auto-Sliding Active Surveys Hero Carousel
        item {
            ActiveSurveysSlider(
                sites = uiState.recentSites,
                isLoading = uiState.isLoading,
                onSiteClick = onSiteClick
            )
        }

        // 2. Primary Action: New Survey Button
        item {
            Button(
                onClick = onNewSurveyClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF29B05E),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_circle),
                    contentDescription = "Add Survey",
                    modifier = Modifier
                        .size(26.dp)
                        .padding(end = 6.dp)
                )
                Text(
                    text = "New Survey",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.W700
                )
            }
        }

        // 3. Section Header: Recent Surveys + See All
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Surveys",
                    color = DarkBlueText,
                    fontWeight = FontWeight.W700,
                    fontSize = 18.sp
                )
                TextButton(
                    onClick = onSeeAllSitesClick,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "See all",
                        color = Color(0xFF8B3A00),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // 4. Recent Surveys States (Loading / Error / Empty / Real List)
        when {
            uiState.isLoading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF8B3A00))
                    }
                }
            }

            uiState.error != null -> {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = uiState.error,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                        OutlinedButton(onClick = onRetryClick) {
                            Text("Retry")
                        }
                    }
                }
            }

            uiState.recentSites.isEmpty() -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No surveys recorded yet. Tap 'New Survey' to begin.",
                            color = Color(0xFF64748B),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            else -> {
                items(
                    items = uiState.recentSites,
                    key = { it.id ?: it.hashCode() }
                ) { site ->
                    val statusLabel = formatStatusLabel(site.status)
                    val iconRes = mapRoofToIconRes(site.siteAssessments?.roofType)

                    SurveyListItemCard(
                        title = site.name ?: "Unnamed Site",
                        statusText = statusLabel,
                        icon = iconRes,
                        onClick = { site.id?.let(onSiteClick) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveSurveysSlider(
    sites: List<SitesDao.Data>,
    isLoading: Boolean,
    onSiteClick: (String) -> Unit
) {
    val pageCount = if (sites.isEmpty()) 1 else sites.size
    val pagerState = rememberPagerState(pageCount = { pageCount })

    // Auto-slide every 4 seconds when there are multiple active sites
    LaunchedEffect(pageCount) {
        if (pageCount > 1) {
            while (true) {
                delay(4000L)
                val nextPage = (pagerState.currentPage + 1) % pageCount
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Box(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF1E293B))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val currentSite = sites.getOrNull(page)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(enabled = currentSite?.id != null) {
                        currentSite?.id?.let(onSiteClick)
                    }
            ) {
                if (!currentSite?.primaryImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = currentSite.primaryImageUrl,
                        contentDescription = currentSite.name,
                        placeholder = painterResource(id = R.drawable.solar_site_1),
                        error = painterResource(id = R.drawable.solar_site_1),
                        onError = { state ->
                            android.util.Log.e("COIL_ERROR", "URL=${currentSite.primaryImageUrl}", state.result.throwable)
                        },
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.solar_site_1),
                        contentDescription = currentSite?.name ?: "Sample Site",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Dark bottom scrim for crisp text legibility
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.15f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.78f)
                                )
                            )
                        )
                )

                // Bottom-left site details
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, end = 64.dp, bottom = 14.dp)
                ) {
                    Text(
                        text = currentSite?.name ?: if (isLoading) "Loading active surveys..." else "No active surveys",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (currentSite != null) {
                        val capacity = currentSite.siteAssessments?.capacityKwp
                        val subtitle = buildString {
                            append(currentSite.clientName ?: "Client")
                            if (capacity != null && capacity > 0.0) {
                                append(" • $capacity kWp")
                            }
                        }
                        Text(
                            text = subtitle,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Top-left Active Count Badge
        Surface(
            color = Color.Black.copy(alpha = 0.55f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
        ) {
            Text(
                text = if (sites.isEmpty()) "0 surveys" else "${sites.size} active surveys",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
            )
        }

        // Bottom-right Pager Dots
        if (pageCount > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pageCount) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color.White else Color.White.copy(alpha = 0.45f))
                    )
                }
            }
        }
    }
}

private fun formatStatusLabel(rawStatus: String?): String = when (rawStatus?.uppercase()) {
    "DRAFT" -> "Draft"
    "READY_FOR_REVIEW" -> "Ready for review"
    "SYNCED" -> "Synced"
    else -> rawStatus ?: "Draft"
}

private fun mapRoofToIconRes(roofType: String?): Int = when (roofType?.uppercase()) {
    "METAL_SHEET", "STANDING SEAM METAL" -> R.drawable.ic_warehouse
    "FLAT_CONCRETE" -> R.drawable.ic_appartments
    "TILE", "CLAY TILE" -> R.drawable.ic_factory
    else -> R.drawable.ic_nav_sites
}

@Preview(showBackground = true)
@Composable
private fun DashboardPreview() {
    DashboardContent(
        uiState = DashboardUiState(isLoading = false),
        onNewSurveyClick = {},
        onSiteClick = {},
        onSeeAllSitesClick = {},
        onRetryClick = {}
    )
}