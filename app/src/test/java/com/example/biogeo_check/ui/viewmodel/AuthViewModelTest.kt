package com.example.biogeo_check.ui.viewmodel

import com.example.biogeo_check.data.model.Trabajador
import com.example.biogeo_check.data.repository.AuthRepository
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private lateinit var repository: AuthRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = AuthViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login exitoso emite estado Success con el trabajador`() = runTest {
        val trabajadorMock = Trabajador(
            trabajadorId = "t1",
            empresaId = "e1",
            email = "user@test.com",
            rol = "TRABAJADOR"
        )

        // Simulamos que el repositorio devuelve el trabajador al hacer login
        coEvery { repository.login("user@test.com", "password123") } returns trabajadorMock

        // Ejecutamos el login en el ViewModel
        viewModel.login("user@test.com", "password123")

        // Verificamos que el estado final es Success y contiene el trabajador correcto
        val state = viewModel.authState.value
        assertTrue(state is AuthViewModel.AuthState.Success)
        assertEquals(trabajadorMock, (state as AuthViewModel.AuthState.Success).trabajador)
    }

    @Test
    fun `login fallido emite estado Error con el mensaje adecuado`() = runTest {
        // Simulamos que Supabase o el repo lanzan una excepción de credenciales
        coEvery { repository.login("user@test.com", "wrong_pass") } throws Exception("Credenciales incorrectas")

        viewModel.login("user@test.com", "wrong_pass")

        val state = viewModel.authState.value
        assertTrue(state is AuthViewModel.AuthState.Error)
        assertEquals("Credenciales incorrectas", (state as AuthViewModel.AuthState.Error).mensaje)
    }
}
