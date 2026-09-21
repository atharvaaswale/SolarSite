package com.unreal.solarsite.presentation.navigation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unreal.solarsite.ui.theme.DullOrange
import com.unreal.solarsite.ui.theme.InactiveGray

@Composable
fun SolarSiteBottomBar(
    currentRoute: String?,
    onNavigateToDestination: (TopLevelDestination) -> Unit
) {

    NavigationBar(
        modifier = Modifier.height(58.dp),
        containerColor = Color.White,
        tonalElevation = 3.dp
    ) {
        TopLevelDestination.entries.forEach { destination ->
            val isSelected = currentRoute == destination.route

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigateToDestination(destination) },
                interactionSource = remember { MutableInteractionSource() },
                icon = {
                    Icon(
                        painter = painterResource(id = destination.iconRes),
                        contentDescription = destination.label,
                        modifier = Modifier.size(18.dp)
                    )
                },
                label = {
                    Text(
                        text = destination.label,
                        fontSize = 11.sp,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DullOrange,
                    selectedTextColor = DullOrange,
                    unselectedIconColor = InactiveGray,
                    unselectedTextColor = InactiveGray,
                    indicatorColor = Color.Transparent
                    )
            )
        }
    }
}