package id.yuana.passkeys.playground.navigation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import id.yuana.passkeys.playground.ui.feature.welcome.WelcomeScreen

typealias OnNavigate = (UiEvent.Navigate) -> Unit
typealias OnPopBackStack = () -> Unit

@Composable
fun RootNav(
    navController: NavHostController = rememberNavController(),
    onNeedFinish: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        RootNavGraph(
            onNavigate = {
                navController.navigate(it.destinationRoute) {
                    it.navOptionsBuilder?.invoke(this)
                }
            },
            onPopBackStack = {
                if (navController.popBackStack().not()) {
                    onNeedFinish.invoke()
                }
            }
        )
    }
}

fun NavGraphBuilder.RootNavGraph(
    onNavigate: OnNavigate,
    onPopBackStack: OnPopBackStack,
) {
    composable(route = Screen.Welcome.route) {
        WelcomeScreen(onNavigate = onNavigate)
    }
}