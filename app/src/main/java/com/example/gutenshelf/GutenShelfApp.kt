package com.example.gutenshelf

import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.gutenshelf.navigation.NavBarDestinations

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gutenshelf.navigation.AppNavGraph

@Composable
fun GutenShelfApp() {
    val navController = rememberNavController()

    val currentRoute = navController
        .currentBackStackEntryFlow
        .collectAsState(initial = navController.currentBackStackEntry)
        .value
        ?.destination
        ?.route

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            NavBarDestinations.forEach { navItem ->
                item(
                    icon = {
                        Icon(
                            painter = painterResource(navItem.icon),
                            contentDescription = navItem.label,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    label = { Text(navItem.label) },
                    selected = currentRoute == navItem.route,
                    onClick = {
                        navController.navigate(navItem.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) {
        AppNavGraph(navController)
    }
}