package com.example.biogeo_check.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biogeo_check.ui.theme.BlackBackground
import com.example.biogeo_check.ui.theme.EmeraldGreen
import com.example.biogeo_check.ui.theme.PrimaryTextWhite
import com.example.biogeo_check.ui.theme.SecondaryTextGray

@Composable
fun LegalScreen(
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Términos y Condiciones",
            color = EmeraldGreen,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        val scrollState = rememberScrollState()
        
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Al usar esta aplicación, aceptas los siguientes términos sobre el registro de la jornada laboral:\n\n" +
                        "1. Registro de Horas: Esta aplicación registrará tus horas exactas de entrada y salida para asegurar la precisión de la nómina y el cumplimiento de las leyes laborales.\n\n" +
                        "2. Datos de Ubicación: Podremos recopilar tus datos de ubicación únicamente en el momento de fichar la entrada y salida para verificar tu lugar de trabajo, según lo acordado en tu contrato laboral.\n\n" +
                        "3. Privacidad de Datos: Tus datos de seguimiento se almacenan de forma segura y solo son accesibles por personal administrativo autorizado y tú mismo. No vendemos ni compartimos estos datos con terceros.\n\n" +
                        "4. Precisión: Eres responsable de garantizar que tus tiempos de entrada y salida reflejen con precisión tus horas trabajadas reales.\n\n" +
                        "5. Actualizaciones de Políticas: Nos reservamos el derecho de actualizar estos términos. El uso continuo de la aplicación constituye la aceptación de cualquier término modificado.",
                color = SecondaryTextGray,
                fontSize = 16.sp,
                fontFamily = FontFamily.SansSerif,
                lineHeight = 24.sp
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onAccept,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldGreen,
                    contentColor = PrimaryTextWhite
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Aceptar y Continuar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            
            OutlinedButton(
                onClick = onDecline,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = EmeraldGreen,
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(1.dp, EmeraldGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Rechazar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
