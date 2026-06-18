package com.roota.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.roota.app.presentation.MainViewModel
import com.roota.app.presentation.navigation.AppNavGraph
import com.roota.app.presentation.navigation.RootABottomNavBar
import com.roota.app.presentation.navigation.shouldShowBottomBar
import com.roota.app.presentation.ui.theme.RootATheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RootATheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val mainViewModel: MainViewModel = hiltViewModel()
                    val startDestination by mainViewModel.startDestination.collectAsState()

                    if (startDestination == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        val navController = rememberNavController()
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route
                        val showBottomBar = shouldShowBottomBar(currentRoute)

                        Scaffold(
                            bottomBar = {
                                if (showBottomBar) {
                                    RootABottomNavBar(
                                        currentRoute = currentRoute,
                                        onNavigate = { route ->
                                            navController.navigate(route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        ) { padding ->
                            AppNavGraph(
                                navController = navController,
                                startDestination = startDestination!!,
                                modifier = Modifier.padding(padding)
                            )
                        }
                    }
                }
            }
        }
    }
}
