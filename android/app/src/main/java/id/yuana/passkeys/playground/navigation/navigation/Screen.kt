package coder.anthony.treasure.hunt.navigation

import androidx.navigation.NamedNavArgument

sealed class Screen(
    val route: String,
    val args: List<NamedNavArgument> = emptyList()
) {

    object Home : Screen(route = "home")
    object SelectApps : Screen(route = "select-apps")

}