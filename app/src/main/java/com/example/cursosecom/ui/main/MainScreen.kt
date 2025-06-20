package com.example.cursosecom.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cursosecom.HomeScreen
import com.example.cursosecom.navigation.BottomNavItem
import com.example.cursosecom.ui.mycourses.MeusCursosScreen
import com.example.cursosecom.ui.profile.PerfilScreen

/**
 * Tela principal que contém a barra de navegação inferior e gerencia as telas
 * de Início, Meus Cursos e Perfil.
 */
@Composable
fun MainScreen(navControllerApp: NavController, userId: Int) {
    // NavController local para a navegação da barra inferior.
    val navControllerBottom = rememberNavController()

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.MyCourses,
        BottomNavItem.Profile
    )

    // Estrutura principal da tela com a barra de navegação na parte de baixo.
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navControllerBottom.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // Cria os ícones na barra de navegação.
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navControllerBottom.navigate(screen.route) {
                                popUpTo(navControllerBottom.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // "Miolo" da tela, onde o conteúdo de cada aba é exibido.
        NavHost(
            navController = navControllerBottom,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Define qual tela será exibida para cada rota da barra inferior.
            composable(BottomNavItem.Home.route) { HomeScreen(navController = navControllerApp, userId = userId) }
            composable(BottomNavItem.MyCourses.route) { MeusCursosScreen(navController = navControllerApp, userId = userId) }
            composable(BottomNavItem.Profile.route) { PerfilScreen(navController = navControllerApp, userId = userId) }
        }
    }
}
