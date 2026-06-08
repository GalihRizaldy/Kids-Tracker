package com.android.kidstracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ChildDevBottomNavigationBar(
    currentRoute: String,
    onNavigateToHome: () -> Unit = {},
    onNavigateToGrowth: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        NavigationBarItem(
            selected = currentRoute.contains("Dashboard", ignoreCase = true) || currentRoute == "home",
            onClick = onNavigateToHome,
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )
        NavigationBarItem(
            selected = currentRoute.contains("Development", ignoreCase = true) || currentRoute.contains("Task", ignoreCase = true) || currentRoute.contains("Result", ignoreCase = true) || currentRoute == "growth",
            onClick = onNavigateToGrowth,
            icon = { Icon(Icons.Default.MonitorHeart, contentDescription = "Growth") },
            label = { Text("Growth") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )
        NavigationBarItem(
            selected = currentRoute.contains("Profile", ignoreCase = true) || currentRoute == "profile",
            onClick = onNavigateToProfile,
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )
    }
}
