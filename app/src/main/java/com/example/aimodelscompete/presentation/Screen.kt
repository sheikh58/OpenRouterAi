package com.example.aimodelscompete.presentation

import android.net.Uri

sealed class Screen(val route: String) {
    object ModelsList : Screen("models_list")
    object Settings : Screen("settings")
    object Playground : Screen("playground?modelId={modelId}") {
        fun createRoute(modelId: String) = "playground?modelId=${Uri.encode(modelId)}"
    }
    object History : Screen("history")
    object Leaderboard : Screen("leaderboard")
}
