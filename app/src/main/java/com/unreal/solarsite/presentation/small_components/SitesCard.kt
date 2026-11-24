package com.unreal.solarsite.presentation.small_components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.unreal.solarsite.R
import com.unreal.solarsite.data.dao.SitesDao
import com.unreal.solarsite.ui.theme.BlueText
import com.unreal.solarsite.ui.theme.GreyText
import com.unreal.solarsite.ui.theme.LightBlueContainer
import com.unreal.solarsite.ui.theme.OrangeText
import com.unreal.solarsite.utils.toCustomSentenceCase

data class SiteItemUiModel(
    val id: String,
    val name: String,
    val location: String,
    val capacityKwp: Int,
    val status: String,
    val imageRes: Int? = null
)

@Composable
fun SiteCard(
    site: SitesDao.Data,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val subtleIconTint = Color(0xFF9CA3AF) // Softer muted grey so icons don't glare

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        onClick = { onEditClick(site.id) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Edge-to-edge Left Image (fills top, bottom, and left completely)
            Box(
                modifier = Modifier
                    .width(96.dp)
                    .fillMaxHeight()
                    .background(color = LightBlueContainer),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = site.primaryImageUrl,
                    contentDescription = site.name,
                    placeholder = painterResource(R.drawable.solar_site_1),
                    error = painterResource(R.drawable.solar_site_1),
                    fallback = painterResource(R.drawable.solar_site_1),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 2. Main Site Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = site.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = site.siteAssessments?.let {
                        "${it.roofType} | ${it.capacityKwp} kWp"
                    } ?: "No assessment data",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = site.status.toCustomSentenceCase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = when (site.status) {
                        "Draft", "DRAFT" -> GreyText
                        "Synced", "SYNCED" -> BlueText
                        "Ready for review", "READY_FOR_REVIEW" -> OrangeText
                        else -> GreyText
                    },
                    fontWeight = FontWeight.Medium
                )
            }

            // 3. Subtle Action Buttons
            Column(
                modifier = Modifier.padding(end = 8.dp, top = 6.dp, bottom = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = { onEditClick(site.id) },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = "Edit Site",
                        tint = subtleIconTint,
                        modifier = Modifier.size(19.dp)
                    )
                }
                IconButton(
                    onClick = { onDeleteClick(site.id) },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = "Delete Site",
                        tint = subtleIconTint,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}