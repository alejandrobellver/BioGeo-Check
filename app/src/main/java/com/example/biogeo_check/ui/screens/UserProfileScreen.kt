package com.example.biogeo_check.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biogeo_check.ui.components.BottomNavBar
import com.example.biogeo_check.ui.components.NavScreen
import com.example.biogeo_check.ui.theme.*

@Composable
fun UserProfileScreen(
    onNavigate: (NavScreen) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF333333)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar Placeholder",
                    tint = PrimaryTextWhite,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Info
            Text(
                text = "Maria Garcia",
                color = PrimaryTextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "Desarrolladora de Software",
                color = SecondaryTextGray,
                fontSize = 16.sp,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ID: EMP-0429 • Contratada: Ene 2023",
                color = Color(0xFF666666),
                fontSize = 12.sp,
                fontFamily = FontFamily.SansSerif
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Card 1: Personal Information
            ProfileCard(title = "Información Personal", actionIcon = true) {
                ProfileInfoRow(label = "Correo electrónico", value = "maria.garcia@company.com")
                Spacer(modifier = Modifier.height(12.dp))
                ProfileInfoRow(label = "Teléfono", value = "+34 600 123 456")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card 2: Work Statistics
            ProfileCard(title = "Estadísticas de Trabajo", actionIcon = false) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Horas Totales este Mes", color = SecondaryTextGray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "164h 30m", color = EmeraldGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(text = "Horas Extra", color = SecondaryTextGray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "12h 15m", color = EmeraldGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Change Password Link
            Text(
                text = "Cambiar Contraseña",
                color = EmeraldGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable { /* Handle Change Password */ }
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Log Out Button
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryTextWhite,
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(1.dp, EmeraldGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cerrar Sesión", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }

        BottomNavBar(
            currentScreen = NavScreen.PROFILE,
            onNavigate = onNavigate
        )
    }
}

@Composable
fun ProfileCard(title: String, actionIcon: Boolean, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkGrayCard)
            .border(1.dp, Color(0xFF2A2A2A), RoundedCornerShape(8.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = PrimaryTextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif
            )
            if (actionIcon) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = EmeraldGreen,
                    modifier = Modifier.size(20.dp).clickable { /* Edit action */ }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        content()
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Column {
        Text(text = label, color = SecondaryTextGray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = PrimaryTextWhite, fontSize = 16.sp)
    }
}
