package com.example.biogeo_check.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.biogeo_check.ui.theme.DarkEmeraldGreen
import com.example.biogeo_check.ui.theme.DarkGrayCard

enum class NavScreen {
    HOME, EMPLOYEES, DEPARTMENTS, PROFILE
}

@Composable
fun BottomNavBar(
    currentScreen: NavScreen,
    isJefe: Boolean = false,
    onNavigate: (NavScreen) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A0A0A)) // Black background
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavItem(
            icon = Icons.Default.Home,
            isSelected = currentScreen == NavScreen.HOME,
            onClick = { onNavigate(NavScreen.HOME) }
        )
        if (isJefe) {
            NavItem(
                icon = Icons.Default.Face,
                isSelected = currentScreen == NavScreen.EMPLOYEES,
                onClick = { onNavigate(NavScreen.EMPLOYEES) }
            )
            NavItem(
                icon = Icons.AutoMirrored.Filled.List,
                isSelected = currentScreen == NavScreen.DEPARTMENTS,
                onClick = { onNavigate(NavScreen.DEPARTMENTS) }
            )
        }
        NavItem(
            icon = Icons.Default.Person,
            isSelected = currentScreen == NavScreen.PROFILE,
            onClick = { onNavigate(NavScreen.PROFILE) }
        )
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) DarkEmeraldGreen else Color.Transparent
    val iconColor = if (isSelected) Color.White else Color(0xFF666666)

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
    }
}
