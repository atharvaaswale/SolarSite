package com.unreal.solarsite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.unreal.solarsite.presentation.Dashboard
import com.unreal.solarsite.presentation.SitesScreen
import com.unreal.solarsite.presentation.navigation.SolarSiteBottomBar
import com.unreal.solarsite.presentation.navigation.TopLevelDestination
import com.unreal.solarsite.ui.theme.SolarSiteTheme
import android.graphics.Color as AndroidColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = AndroidColor.TRANSPARENT,
                darkScrim = AndroidColor.TRANSPARENT
            )
        )
        setContent {
            SolarSiteTheme {
                MainLayout()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    // Resolve the active screen title dynamically
    val currentTopBarTitle = TopLevelDestination.entries
        .firstOrNull { it.route == currentRoute }
        ?.label ?: "SolarSite"

    Scaffold(
        topBar = {
            /*TopAppBar(
                title = {
                    Text(
                        text = currentTopBarTitle,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

            )*/
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding() // Pushes below the clock/battery icons cleanly
                    .padding(horizontal = 20.dp, vertical = 8.dp) // Tight, controlled height
            ) {
                Text(
                    text = currentTopBarTitle,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        containerColor = Color(0xFFF8F9FF),
        bottomBar = {
            SolarSiteBottomBar(
                currentRoute = currentRoute,
                onNavigateToDestination = { destination ->
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        //avoid multi copies of same screen
                        launchSingleTop = true
                        restoreState = true //restore state when selecting previously selected tab
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.HOME.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(TopLevelDestination.HOME.route) {
                Dashboard(LocalContext.current)
            }
            composable(TopLevelDestination.SITES.route) {
                SitesScreen()
            }
            composable(TopLevelDestination.SYNC.route) {
                Text(text = "Sync Screen Content", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    SolarSiteTheme {
        MainLayout()
    }
}