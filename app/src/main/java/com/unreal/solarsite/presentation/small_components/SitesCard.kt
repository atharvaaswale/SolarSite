package com.unreal.solarsite.presentation.small_components

import android.view.RoundedCorner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unreal.solarsite.R
import com.unreal.solarsite.ui.theme.BlueGrey
import com.unreal.solarsite.ui.theme.BlueText
import com.unreal.solarsite.ui.theme.DarkBlueContainer
import com.unreal.solarsite.ui.theme.DarkBlueText
import com.unreal.solarsite.ui.theme.DullBlue
import com.unreal.solarsite.ui.theme.DullOrange
import com.unreal.solarsite.ui.theme.GreyText
import com.unreal.solarsite.ui.theme.LightBlueContainer
import com.unreal.solarsite.ui.theme.OrangeContainer
import com.unreal.solarsite.ui.theme.OrangeText

data class SiteItemUiModel(
    val id: String,
    val name: String,
    val location: String,
    val capacityKwp: Int,
    val status: String,
    val imageRes: Int? = null // local drawable or placeholder
)

@Composable
fun SiteCard(
    site: SiteItemUiModel,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        onClick = {}
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = LightBlueContainer,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(site.imageRes!!),
                    contentDescription = "leadingIcon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2. Main Site Info
            Column(
                modifier = Modifier.weight(1f),
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
                    text = "${site.location} | ${site.capacityKwp} kWp",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = site.status,
                    style = MaterialTheme.typography.labelSmall,
                    color = when (site.status) {
                        "Draft" -> GreyText
                        "Synced" -> BlueText
                        "Ready for review" -> OrangeText
                        else -> GreyText
                    },
                    fontWeight = FontWeight.Medium
                )
            }

            // 3. Action Buttons
            Column {
                IconButton(
                    onClick = { onEditClick(site.id) },
                    modifier = Modifier.size(36.dp).clip(CircleShape)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = "Edit Site",
                        tint = Color(0xFF71717A),
                        modifier = Modifier.size(22.dp)
                    )
                }
                IconButton(
                    onClick = { onDeleteClick(site.id) },
                    modifier = Modifier.size(36.dp).clip(CircleShape)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = "Delete Site",
                        tint = Color(0xFF71717A),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun ShowSiteCard() {
    SiteCard(
        SiteItemUiModel(
            id = "1",
            name = "Northstar Manufacturing",
            location = "Panvel",
            capacityKwp = 95,
            status = "Ready for review",
            imageRes = R.drawable.solar_site_1
        ),
        {},
        {}
    )
}
