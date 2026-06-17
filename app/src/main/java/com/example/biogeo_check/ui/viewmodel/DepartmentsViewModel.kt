package com.example.biogeo_check.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biogeo_check.data.model.Departamento
import com.example.biogeo_check.data.model.Trabajador
import com.example.biogeo_check.data.repository.FichajeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de la gestión, creación y asignación de departamentos dentro de la organización.
 *
 * Provee a la interfaz de administración los estados reactivos necesarios para listar áreas de la empresa,
 * calcular la densidad de la plantilla por departamento y realizar asignaciones masivas de empleados de forma asíncrona.
 *
 * @property fichajeRepository Repositorio que proporciona el acceso a las operaciones CRUD de departamentos y personal.
 */
class DepartmentsViewModel(
    private val fichajeRepository: FichajeRepository
) : ViewModel() {

    // 🏢 =======================================================
    // ENCAPSULAMIENTO DEL ESTADO DE LA INTERFAZ
    // =======================================================

    private val _listaDepartamentosAdmin = MutableStateFlow<List<Departamento>>(listOf())

    /** Catálogo global de departamentos de la empresa destinados a la vista del administrador. */
    val listaDepartamentosAdmin: StateFlow<List<Departamento>> =
        _listaDepartamentosAdmin.asStateFlow()

    private val _conteoEmpleadosPorDepto = MutableStateFlow<Map<String, Int>>(emptyMap())

    /** Mapa asociativo que vincula el ID de cada departamento con el número total de trabajadores adscritos a él. */
    val conteoEmpleadosPorDepto: StateFlow<Map<String, Int>> =
        _conteoEmpleadosPorDepto.asStateFlow()

    private val _listaTrabajadoresAdmin = MutableStateFlow<List<Trabajador>>(listOf())

    /** Censo completo de trabajadores de la organización para su gestión y asignación en el panel. */
    val listaTrabajadoresAdmin: StateFlow<List<Trabajador>> = _listaTrabajadoresAdmin.asStateFlow()


    /**
     * Sincroniza y descarga de forma simultánea los departamentos y el personal de la empresa desde el servidor.
     *
     * Tras recuperar los datos, procesa en memoria la distribución de la plantilla agrupando a los trabajadores
     * por su identificador de departamento para actualizar el contador de métricas visuales.
     */
    fun cargarDatosDepartamentosYPersonas() {
        viewModelScope.launch {
            try {
                // 1. Nos bajamos los departamentos actualizados de Supabase
                _listaDepartamentosAdmin.value = fichajeRepository.obtenerTodosLosDepartamentos()

                // 2. Nos bajamos todos los trabajadores
                val todosLosTrabajadores = fichajeRepository.obtenerTodosLosTrabajadores()
                _listaTrabajadoresAdmin.value = todosLosTrabajadores

                // 3. Calculamos cuántos empleados pertenecen a cada ID de departamento
                _conteoEmpleadosPorDepto.value = todosLosTrabajadores
                    .filter { it.departamentoId != null }
                    .groupBy { it.departamentoId!! }
                    .mapValues { it.value.size }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Registra un nuevo departamento en el sistema de forma asíncrona.
     *
     * @param dep Instancia del modelo [Departamento] con los datos a insertar.
     * @param onResultado Callback de retorno que notifica a la UI mediante un booleano si la operación fue exitosa o fallida.
     */
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

    /**
     * Evalúa y actualiza de manera masiva la adscripción de los empleados a un departamento específico.
     *
     * Compara el estado previo del trabajador con los nuevos identificadores seleccionados en la UI:
     * - Si el trabajador pertenecía al área pero ya no está seleccionado, se le desvincula (pasa a null).
     * - Si el trabajador no pertenecía pero ha sido seleccionado, se le asigna el nuevo [departamentoId].
     *
     * Al finalizar las actualizaciones, fuerza un refresco de los estados locales para reflejar los cambios en la UI.
     *
     * @param departamentoId ID del departamento objetivo sobre el que se realizan las altas o bajas.
     * @param empleadosSeleccionadosIds Conjunto (Set) de identificadores únicos de los empleados que deben quedar asignados a dicho departamento.
     */
    fun actualizarEmpleadosDepartamento(
        departamentoId: String?,
        empleadosSeleccionadosIds: Set<String>
    ) {
        viewModelScope.launch {
            try {
                _listaTrabajadoresAdmin.value.forEach { trabajador ->
                    val perteneceAntes = trabajador.departamentoId == departamentoId
                    val perteneceAhora = empleadosSeleccionadosIds.contains(trabajador.trabajadorId)

                    if (perteneceAntes && !perteneceAhora) {
                        fichajeRepository.actualizarDepartamentoDeTrabajador(
                            trabajador.trabajadorId,
                            null
                        )
                    } else if (!perteneceAntes && perteneceAhora) {
                        fichajeRepository.actualizarDepartamentoDeTrabajador(
                            trabajador.trabajadorId,
                            departamentoId
                        )
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