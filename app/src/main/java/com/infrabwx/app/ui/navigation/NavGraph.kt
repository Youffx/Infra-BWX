package com.infrabwx.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.infrabwx.app.InfraBwxApp
import com.infrabwx.app.data.preferences.AppPreferences
import com.infrabwx.app.ui.camera.CameraScreen
import com.infrabwx.app.ui.category.CategoryScreen
import com.infrabwx.app.ui.main.MainScreen
import com.infrabwx.app.ui.permissions.PermissionsScreen
import com.infrabwx.app.ui.splash.SplashScreen
import com.infrabwx.app.ui.terms.TermsScreen
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Permissions : Screen("permissions")
    data object Terms : Screen("terms")
    data object Main : Screen("main")
    data object Category : Screen("category/{categoryId}") {
        fun createRoute(categoryId: String) = "category/$categoryId"
    }
    data object Camera : Screen("camera/{categoryId}") {
        fun createRoute(categoryId: String) = "camera/$categoryId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    hasAcceptedTerms: Boolean,
    themeMode: String,
    preferences: AppPreferences
) {
    val scope = rememberCoroutineScope()
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    val destination = if (hasAcceptedTerms) {
                        Screen.Main.route
                    } else {
                        Screen.Permissions.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Permissions.route) {
            PermissionsScreen(
                onPermissionsGranted = {
                    navController.navigate(Screen.Terms.route) {
                        popUpTo(Screen.Permissions.route) { inclusive = true }
                    }
                },
                onDenied = {
                    val ctx = navController.context
                    if (ctx is android.app.Activity) {
                        ctx.finish()
                    }
                }
            )
        }

        composable(Screen.Terms.route) {
            val app = navController.context.applicationContext as InfraBwxApp
            TermsScreen(
                onAccepted = {
                    scope.launch {
                        app.preferences.setTermsAccepted(true)
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Terms.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                onCategoryClick = { categoryId ->
                    navController.navigate(Screen.Category.createRoute(categoryId))
                },
                themeMode = themeMode,
                preferences = preferences
            )
        }

        composable(
            route = Screen.Category.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: return@composable
            CategoryScreen(
                categoryId = categoryId,
                onBack = { navController.popBackStack() },
                onLapor = {
                    navController.navigate(Screen.Camera.createRoute(categoryId))
                }
            )
        }

        composable(
            route = Screen.Camera.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: return@composable
            CameraScreen(
                categoryId = categoryId,
                onBack = { navController.popBackStack() },
                onSubmitted = {
                    navController.popBackStack(Screen.Main.route, inclusive = false)
                }
            )
        }
    }
}
