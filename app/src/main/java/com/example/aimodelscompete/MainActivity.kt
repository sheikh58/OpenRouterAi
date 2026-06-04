package com.example.aimodelscompete

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aimodelscompete.presentation.Screen
import com.example.aimodelscompete.presentation.history.HistoryScreen
import com.example.aimodelscompete.presentation.leaderboard.LeaderboardScreen
import com.example.aimodelscompete.presentation.models.ModelsScreen
import com.example.aimodelscompete.presentation.playground.PlaygroundScreen
import com.example.aimodelscompete.presentation.settings.SettingsScreen
import com.example.aimodelscompete.ui.theme.AiModelsCompeteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AiModelsCompeteTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.ModelsList.route
    ) {
        composable(route = Screen.ModelsList.route) {
            ModelsScreen(
                onModelClick = { modelId ->
                    navController.navigate(Screen.Playground.createRoute(modelId))
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onHistoryClick = {
                    navController.navigate(Screen.History.route)
                },
                onLeaderboardClick = {
                    navController.navigate(Screen.Leaderboard.route)
                }
            )
        }
        composable(route = Screen.History.route) {
            HistoryScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = Screen.Leaderboard.route) {
            LeaderboardScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.Playground.route,
            arguments = listOf(
                navArgument("modelId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            PlaygroundScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
