package com.example.biogeo_check.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biogeo_check.data.model.Departamento
import com.example.biogeo_check.data.model.Trabajador
import com.example.biogeo_check.ui.components.BottomNavBar
import com.example.biogeo_check.ui.components.NavScreen
import com.example.biogeo_check.ui.theme.BlackBackground
import com.example.biogeo_check.ui.theme.DarkGrayCard
import com.example.biogeo_check.ui.theme.EmeraldGreen
import com.example.biogeo_check.ui.theme.PrimaryTextWhite
import com.example.biogeo_check.ui.theme.SecondaryTextGray
import com.example.biogeo_check.ui.viewmodel.DepartmentsViewModel

@Composable
fun AdminDepartmentsScreen(
    vm: DepartmentsViewModel = viewModel(),
    onNavigate: (NavScreen) -> Unit
) {
    LaunchedEffect(Unit) {
        vm.cargarDatosDepartamentosYPersonas()
    }

    val listaDepartamentos by vm.listaDepartamentosAdmin.collectAsState()
    val listaTrabajadores by vm.listaTrabajadoresAdmin.collectAsState()
    val conteoEmpleados by vm.conteoEmpleadosPorDepto.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedDepartment by remember { mutableStateOf<Departamento?>(null) }

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
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Crear Departamento",
                        tint = PrimaryTextWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(listaDepartamentos) { dep ->
                    DepartmentCard(
                        departamento = dep,

                        employeeCount = conteoEmpleados[dep.departamentoId] ?: 0,
                        onClick = { selectedDepartment = dep }
                    )
                }
            }
        }

        BottomNavBar(
            currentScreen = NavScreen.DEPARTMENTS,
            isJefe = true,
            onNavigate = onNavigate
        )
    }

    // Diálogo de creación conectado al ViewModel
    if (showCreateDialog) {
        CreateDepartmentDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { newDep ->
                vm.crearNuevoDepartamento(newDep) { exito ->
                    if (exito) showCreateDialog = false
                }
            }
        )
    }

    // Diálogo de asignación conectado al ViewModel
    if (selectedDepartment != null) {
        AssignEmployeesDialog(
            departamento = selectedDepartment!!,
            allEmployees = listaTrabajadores, // 🚀 Pasamos la lista limpia desenvuelta
            allDepartments = listaDepartamentos, // 🚀 Pasamos la lista limpia desenvuelta
            onDismiss = { selectedDepartment = null },
            onConfirmAssignments = { listaIds ->
                vm.actualizarEmpleadosDepartamento(selectedDepartment!!.departamentoId, listaIds)
                selectedDepartment = null
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
                    if (nombre.isNotBlank() && horaEntrada.isNotBlank() && horaSalida.isNotBlank()) {
                        val datosFormulario = Departamento(
                            departamentoId = null,
                            empresaId = "",
                            nombreDepartamento = nombre.trim(),
                            horaEntrada = horaEntrada.trim(),
                            horaSalida = horaSalida.trim(),
                            ubicacionDepartamento = if (ubicacion.isBlank()) null else ubicacion.trim(),
                            turno = if (turno.isBlank()) null else turno.trim()
                        )
                        onCreate(datosFormulario)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldGreen,
                    contentColor = PrimaryTextWhite
                )
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
    allEmployees: List<Trabajador>,
    allDepartments: List<Departamento>,
    onDismiss: () -> Unit,
    onConfirmAssignments: (Set<String>) -> Unit
) {
    var assignedEmployeeIds by remember {
        mutableStateOf(
            allEmployees.filter { it.departamentoId == departamento.departamentoId }
                .map { it.trabajadorId }.toSet()
        )
    }

    var showConflictDialog by remember { mutableStateOf(false) }
    var conflictMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkGrayCard,
        title = {
            Text(
                "Asignar a ${departamento.nombreDepartamento}",
                color = EmeraldGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            LazyColumn(modifier = Modifier.fillMaxHeight(0.6f)) {
                items(allEmployees) { employee ->
                    val isAssigned = assignedEmployeeIds.contains(employee.trabajadorId)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isAssigned) {
                                    assignedEmployeeIds =
                                        assignedEmployeeIds - employee.trabajadorId
                                } else {
                                    val deptoActualId = employee.departamentoId

                                    if (deptoActualId != null && deptoActualId != departamento.departamentoId) {
                                        val nombreDeptoOcupado =
                                            allDepartments.find { it.departamentoId == deptoActualId }?.nombreDepartamento
                                                ?: "otro"
                                        conflictMessage =
                                            "${employee.nombre} ya está en el departamento $nombreDeptoOcupado. Debes desasignarlo de allí primero."
                                        showConflictDialog = true
                                    } else {
                                        assignedEmployeeIds =
                                            assignedEmployeeIds + employee.trabajadorId
                                    }
                                }
                            }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${employee.nombre} ${employee.apellidos ?: ""}",
                            color = PrimaryTextWhite,
                            fontSize = 16.sp
                        )
                        if (isAssigned) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Asignado",
                                tint = EmeraldGreen
                            )
                        }
                    }
                    HorizontalDivider(color = Color(0xFF2A2A2A))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmAssignments(assignedEmployeeIds)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldGreen,
                    contentColor = PrimaryTextWhite
                )
            ) {
                Text("Aceptar")
            }
        }
    )

    if (showConflictDialog) {
        AlertDialog(
            onDismissRequest = { showConflictDialog = false },
            containerColor = Color(0xFF1E1E1E),
            title = { Text("Trabajador Ocupado", color = Color.Red, fontWeight = FontWeight.Bold) },
            text = { Text(conflictMessage, color = PrimaryTextWhite) },
            confirmButton = {
                TextButton(onClick = { showConflictDialog = false }) {
                    Text("Entendido", color = EmeraldGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}