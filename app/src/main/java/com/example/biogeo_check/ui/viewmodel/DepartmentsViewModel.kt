package com.example.biogeo_check.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biogeo_check.data.model.Departamento
import com.example.biogeo_check.data.model.Trabajador
import com.example.biogeo_check.data.repository.FichajeRepository
import kotlinx.coroutines.launch


class DepartmentsViewModel(
    private val fichajeRepository: FichajeRepository
) : ViewModel() {
    // 🏢 =======================================================
    // NUEVOS ESTADOS PARA LA GESTIÓN DE DEPARTAMENTOS (Añade esto tal cual)
    // =======================================================
    var listaDepartamentosAdmin by mutableStateOf<List<Departamento>>(listOf())
    var conteoEmpleadosPorDepto by mutableStateOf<Map<String, Int>>(emptyMap())
    var listaTrabajadoresAdmin by mutableStateOf<List<Trabajador>>(listOf())

    fun cargarDatosDepartamentosYPersonas() {
        viewModelScope.launch {
            try {
                // 1. Nos bajamos los departamentos actualizados de Supabase
                listaDepartamentosAdmin = fichajeRepository.obtenerTodosLosDepartamentos()

                // 2. Nos bajamos todos los trabajadores
                val todosLosTrabajadores = fichajeRepository.obtenerTodosLosTrabajadores()
                listaTrabajadoresAdmin = todosLosTrabajadores

                // 3. Calculamos cuántos empleados pertenecen a cada ID de departamento
                conteoEmpleadosPorDepto = todosLosTrabajadores
                    .filter { it.departamentoId != null }
                    .groupBy { it.departamentoId!! }
                    .mapValues { it.value.size }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun crearNuevoDepartamento(dep: Departamento, onResultado: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                fichajeRepository.insertarDepartamento(dep)
                cargarDatosDepartamentosYPersonas()
                onResultado(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResultado(false)
            }
        }
    }

    fun actualizarEmpleadosDepartamento(departamentoId: String?, empleadosSeleccionadosIds: Set<String>) {
        viewModelScope.launch {
            try {
                listaTrabajadoresAdmin.forEach { trabajador ->
                    val perteneceAntes = trabajador.departamentoId == departamentoId
                    val perteneceAhora = empleadosSeleccionadosIds.contains(trabajador.trabajadorId)

                    if (perteneceAntes && !perteneceAhora) {
                        fichajeRepository.actualizarDepartamentoDeTrabajador(trabajador.trabajadorId, null)
                    } else if (!perteneceAntes && perteneceAhora) {
                        fichajeRepository.actualizarDepartamentoDeTrabajador(trabajador.trabajadorId, departamentoId)
                    }
                }
                // Refrescamos
                cargarDatosDepartamentosYPersonas()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}