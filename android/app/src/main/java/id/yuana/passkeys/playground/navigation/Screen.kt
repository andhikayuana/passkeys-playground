package id.yuana.passkeys.playground.navigation

import androidx.navigation.NamedNavArgument

sealed class Screen(
    val route: String,
    val args: List<NamedNavArgument> = emptyList()
) {

    object Splash : Screen(route = "splash")
    object Welcome : Screen(route = "welcome")
    object Home : Screen(route = "home")

}