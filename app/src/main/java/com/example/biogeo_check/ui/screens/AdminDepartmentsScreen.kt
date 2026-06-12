package com.example.biogeo_check.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biogeo_check.data.model.Departamento
import com.example.biogeo_check.ui.components.BottomNavBar
import com.example.biogeo_check.ui.components.NavScreen
import com.example.biogeo_check.ui.theme.*
import com.example.biogeo_check.ui.viewmodel.DashboardViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.LaunchedEffect

@Composable
fun AdminDepartmentsScreen(
    vm: DashboardViewModel = viewModel(),
    onNavigate: (NavScreen) -> Unit
) {
    LaunchedEffect(Unit) {
        vm.cargarDatosIniciales()
    }
    // Dummy state for demonstration
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedDepartment by remember { mutableStateOf<Departamento?>(null) }
    
    var departments by remember { 
        mutableStateOf(listOf(
            Departamento("d1", "emp1", "Ventas", "09:00", "18:00", "Planta 1", "Mañana"),
            Departamento("d2", "emp1", "Soporte Técnico", "08:00", "16:00", "Planta 2", "Mañana")
        ))
    }
    
    // Map to keep track of employee counts (departamentoId -> count)
    var departmentCounts by remember { mutableStateOf(mapOf("d1" to 5, "d2" to 3)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Departamentos",
                    color = EmeraldGreen,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
                
                IconButton(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier
                        .background(EmeraldGreen, RoundedCornerShape(8.dp))
                        .size(40.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Crear Departamento", tint = PrimaryTextWhite)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(departments) { dep ->
                    DepartmentCard(
                        departamento = dep,
                        employeeCount = departmentCounts[dep.departamentoId] ?: 0,
                        onClick = { selectedDepartment = dep }
                    )
                }
            }
        }

        BottomNavBar(
            currentScreen = NavScreen.HISTORY, // This is the middle tab
            onNavigate = onNavigate
        )
    }

    // Create Department Dialog
    if (showCreateDialog) {
        CreateDepartmentDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { newDep ->
                departments = departments + newDep
                departmentCounts = departmentCounts + (newDep.departamentoId to 0)
                showCreateDialog = false
            }
        )
    }

    // Assign Employees Dialog
    if (selectedDepartment != null) {
        AssignEmployeesDialog(
            departamento = selectedDepartment!!,
            onDismiss = { selectedDepartment = null },
            onUpdateCount = { newCount ->
                departmentCounts = departmentCounts.toMutableMap().apply { 
                    put(selectedDepartment!!.departamentoId, newCount) 
                }
            }
        )
    }
}

@Composable
fun DepartmentCard(
    departamento: Departamento,
    employeeCount: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkGrayCard)
            .border(1.dp, Color(0xFF2A2A2A), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = departamento.nombreDepartamento,
                color = PrimaryTextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$employeeCount personas",
                color = EmeraldGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Horario: ${departamento.horaEntrada} - ${departamento.horaSalida} | Turno: ${departamento.turno}",
            color = SecondaryTextGray,
            fontSize = 12.sp
        )
        Text(
            text = "Ubicación: ${departamento.ubicacionDepartamento}",
            color = SecondaryTextGray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun CreateDepartmentDialog(
    onDismiss: () -> Unit,
    onCreate: (Departamento) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var horaEntrada by remember { mutableStateOf("") }
    var horaSalida by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var turno by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkGrayCard,
        title = { Text("Crear Departamento", color = EmeraldGreen, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre, onValueChange = { nombre = it },
                    label = { Text("Nombre del Departamento", color = SecondaryTextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PrimaryTextWhite,
                        unfocusedTextColor = PrimaryTextWhite,
                        focusedBorderColor = EmeraldGreen
                    ),
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = horaEntrada, onValueChange = { horaEntrada = it },
                        label = { Text("Entrada (ej: 09:00)", color = SecondaryTextGray) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PrimaryTextWhite,
                            unfocusedTextColor = PrimaryTextWhite,
                            focusedBorderColor = EmeraldGreen
                        ),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = horaSalida, onValueChange = { horaSalida = it },
                        label = { Text("Salida (ej: 18:00)", color = SecondaryTextGray) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PrimaryTextWhite,
                            unfocusedTextColor = PrimaryTextWhite,
                            focusedBorderColor = EmeraldGreen
                        ),
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = ubicacion, onValueChange = { ubicacion = it },
                    label = { Text("Ubicación", color = SecondaryTextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PrimaryTextWhite,
                        unfocusedTextColor = PrimaryTextWhite,
                        focusedBorderColor = EmeraldGreen
                    ),
                    singleLine = true
                )
                OutlinedTextField(
                    value = turno, onValueChange = { turno = it },
                    label = { Text("Turno (Mañana/Tarde/Noche)", color = SecondaryTextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PrimaryTextWhite,
                        unfocusedTextColor = PrimaryTextWhite,
                        focusedBorderColor = EmeraldGreen
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isNotBlank()) {
                        onCreate(
                            Departamento(
                                departamentoId = "d${System.currentTimeMillis()}",
                                empresaId = "emp1",
                                nombreDepartamento = nombre,
                                horaEntrada = horaEntrada,
                                horaSalida = horaSalida,
                                ubicacionDepartamento = ubicacion,
                                turno = turno
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = PrimaryTextWhite)
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = SecondaryTextGray)
            }
        }
    )
}

@Composable
fun AssignEmployeesDialog(
    departamento: Departamento,
    onDismiss: () -> Unit,
    onUpdateCount: (Int) -> Unit
) {
    // Dummy list of employees
    val allEmployees = listOf(
        "Maria Garcia", "John Doe", "Alice Smith", "Bob Johnson", 
        "Carlos Ruiz", "Elena Gomez", "Luis Perez"
    )
    
    // Track assigned employees
    var assignedEmployees by remember { mutableStateOf(setOf("Maria Garcia", "Alice Smith")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkGrayCard,
        title = { Text("Asignar a ${departamento.nombreDepartamento}", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            LazyColumn(modifier = Modifier.fillMaxHeight(0.6f)) {
                items(allEmployees) { employee ->
                    val isAssigned = assignedEmployees.contains(employee)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                assignedEmployees = if (isAssigned) {
                                    assignedEmployees - employee
                                } else {
                                    assignedEmployees + employee
                                }
                            }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = employee, color = PrimaryTextWhite, fontSize = 16.sp)
                        if (isAssigned) {
                            Icon(Icons.Default.Check, contentDescription = "Asignado", tint = EmeraldGreen)
                        }
                    }
                    HorizontalDivider(color = Color(0xFF2A2A2A))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onUpdateCount(assignedEmployees.size)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = PrimaryTextWhite)
            ) {
                Text("Aceptar")
            }
        }
    )
}
