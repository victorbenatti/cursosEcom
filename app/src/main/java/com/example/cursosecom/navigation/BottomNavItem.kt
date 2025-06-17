package com.example.cursosecom.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector

// Sealed class para representar cada item da nossa barra de navegação
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home_screen", Icons.Default.Home, "Início")
    object MyCourses : BottomNavItem("my_courses_screen", Icons.Default.VideoLibrary, "Meus Cursos")
    object Profile : BottomNavItem("profile_screen", Icons.Default.Person, "Perfil")
}