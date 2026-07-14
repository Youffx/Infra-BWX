package com.infrabwx.app.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
    preferences: AppPreferences,
    isDark: Boolean = false
) {
    val scope = rememberCoroutineScope()
    val animDuration = 300

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(
            route = Screen.Splash.route,
            enterTransition = { fadeIn(animationSpec = tween(animDuration)) },
            exitTransition = { fadeOut(animationSpec = tween(animDuration)) }
        ) {
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
                },
                isDark = isDark
            )
        }

        composable(
            route = Screen.Permissions.route,
            enterTransition = { slideInHorizontally { it } + fadeIn(animationSpec = tween(animDuration)) },
            exitTransition = { slideOutHorizontally { -it } + fadeOut(animationSpec = tween(animDuration)) },
            popEnterTransition = { slideInHorizontally { -it } + fadeIn(animationSpec = tween(animDuration)) },
            popExitTransition = { slideOutHorizontally { it } + fadeOut(animationSpec = tween(animDuration)) }
        ) {
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

        composable(
            route = Screen.Terms.route,
            enterTransition = { slideInHorizontally { it } + fadeIn(animationSpec = tween(animDuration)) },
            exitTransition = { slideOutHorizontally { -it } + fadeOut(animationSpec = tween(animDuration)) },
            popEnterTransition = { slideInHorizontally { -it } + fadeIn(animationSpec = tween(animDuration)) },
            popExitTransition = { slideOutHorizontally { it } + fadeOut(animationSpec = tween(animDuration)) }
        ) {
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

        composable(
            route = Screen.Main.route,
            enterTransition = { fadeIn(animationSpec = tween(animDuration)) },
            exitTransition = { fadeOut(animationSpec = tween(animDuration)) }
        ) {
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
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType }),
            enterTransition = { slideInHorizontally { it } + fadeIn(animationSpec = tween(animDuration)) },
            exitTransition = { slideOutHorizontally { -it } + fadeOut(animationSpec = tween(animDuration)) },
            popEnterTransition = { slideInHorizontally { -it } + fadeIn(animationSpec = tween(animDuration)) },
            popExitTransition = { slideOutHorizontally { it } + fadeOut(animationSpec = tween(animDuration)) }
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
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType }),
            enterTransition = { slideInHorizontally { it } + fadeIn(animationSpec = tween(animDuration)) },
            exitTransition = { slideOutHorizontally { -it } + fadeOut(animationSpec = tween(animDuration)) },
            popEnterTransition = { slideInHorizontally { -it } + fadeIn(animationSpec = tween(animDuration)) },
            popExitTransition = { slideOutHorizontally { it } + fadeOut(animationSpec = tween(animDuration)) }
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
