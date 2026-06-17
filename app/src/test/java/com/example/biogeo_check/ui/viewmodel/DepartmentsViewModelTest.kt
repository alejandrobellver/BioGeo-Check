package com.example.biogeo_check.ui.viewmodel

import com.example.biogeo_check.data.model.Departamento
import com.example.biogeo_check.data.model.Trabajador
import com.example.biogeo_check.data.repository.FichajeRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DepartmentsViewModelTest {

    private lateinit var viewModel: DepartmentsViewModel
    private lateinit var repository: FichajeRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        // Configuramos el hilo principal de pruebas para las corrutinas
        Dispatchers.setMain(testDispatcher)
        
        // Creamos un "Mock" (doble de prueba) del repositorio
        repository = mockk()
        viewModel = DepartmentsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cargarDatosDepartamentosYPersonas actualiza los estados correctamente`() = runTest {
        // Preparación de datos simulados (Given)
        val deptosMock = listOf(
            Departamento(departamentoId = "d1", nombreDepartamento = "IT", empresaId = "e1", horaEntrada = "08:00", horaSalida = "15:00"),
            Departamento(departamentoId = "d2", nombreDepartamento = "RRHH", empresaId = "e1", horaEntrada = "09:00", horaSalida = "18:00")
        )
        val trabajadoresMock = listOf(
            Trabajador(trabajadorId = "t1", nombre = "Juan", departamentoId = "d1", email = "juan@test.com", empresaId = "e1"),
            Trabajador(trabajadorId = "t2", nombre = "Ana", departamentoId = "d1", email = "ana@test.com", empresaId = "e1")
        )

        // Comportamiento simulado del repositorio
        coEvery { repository.obtenerTodosLosDepartamentos() } returns deptosMock
        coEvery { repository.obtenerTodosLosTrabajadores() } returns trabajadoresMock

        // Ejecución (When)
        viewModel.cargarDatosDepartamentosYPersonas()

        // Verificación (Then)
        // 1. Verifica que la lista de departamentos se cargó
        assertEquals(2, viewModel.listaDepartamentosAdmin.value.size)
        assertEquals("IT", viewModel.listaDepartamentosAdmin.value[0].nombreDepartamento)

        // 2. Verifica que la lista de trabajadores se cargó
        assertEquals(2, viewModel.listaTrabajadoresAdmin.value.size)

        // 3. Verifica el conteo automático (2 trabajadores en IT 'd1')
        val conteo = viewModel.conteoEmpleadosPorDepto.value
        assertEquals(2, conteo["d1"])
        assertEquals(null, conteo["d2"]) // RRHH no tiene trabajadores asignados
    }
}
