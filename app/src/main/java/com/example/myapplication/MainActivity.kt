package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.myapplication.ui.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: ProductViewModel = viewModel()
            val nav = rememberNavController()

            NavHost(navController = nav, startDestination = Routes.LIST) {

                composable(Routes.LIST) {
                    ProductListScreen(
                        vm = vm,
                        onAdd = { nav.navigate(Routes.edit(null)) },
                        onOpen = { id -> nav.navigate(Routes.details(id)) },
                        onEdit = { id -> nav.navigate(Routes.edit(id)) }
                    )
                }

                composable(
                    route = Routes.DETAILS,
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { backStack ->
                    val id = backStack.arguments?.getString("id")!!
                    ProductDetailsScreen(
                        vm = vm,
                        id = id,
                        onBack = { nav.popBackStack() },
                        onEdit = { nav.navigate(Routes.edit(id)) }
                    )
                }

                composable(
                    route = Routes.EDIT,
                    arguments = listOf(navArgument("id") {
                        type = NavType.StringType
                        defaultValue = ""
                    })
                ) { backStack ->
                    val id = backStack.arguments?.getString("id").orEmpty().ifBlank { null }
                    ProductEditScreen(
                        vm = vm,
                        id = id,
                        onDone = { nav.popBackStack() },
                        onBack = { nav.popBackStack() }
                    )
                }
            }
        }
    }
}
