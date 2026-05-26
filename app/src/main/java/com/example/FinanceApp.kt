package com.example

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.viewmodels.FinanceViewModel
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceApp(viewModel: FinanceViewModel) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            var menuExpanded by remember { mutableStateOf(false) }
            
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Finance Tracker", fontWeight = FontWeight.Bold) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White,
                            titleContentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        actions = {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Home") },
                                    onClick = { 
                                        menuExpanded = false 
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Dashboard") },
                                    onClick = { 
                                        navController.navigate("dashboard")
                                        menuExpanded = false 
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Settings") },
                                    onClick = { 
                                        navController.navigate("settings")
                                        menuExpanded = false 
                                    }
                                )
                            }
                        }
                    )
                },
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
            ) { paddingValues ->
                HomeScreen(viewModel, paddingValues)
            }
        }
        composable("dashboard") {
            DashboardScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
        composable("settings") {
            SettingsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
    }
}
