package com.caos.planahorrofamiliar

import com.example.planahorrofamiliar.data.model.Payment
import com.example.planahorrofamiliar.data.model.Plan
import com.example.planahorrofamiliar.ui.viewmodel.AhorroViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AhorroViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: `AhorroRepository.kt`
    private lateinit var viewModel: AhorroViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        coEvery { repository.obtenerPlanes() } returns Result.success(
            listOf(Plan("1", "Viaje familiar", "Vacaciones", 5000000.0, 12))
        )
        viewModel = AhorroViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `calcularTotalAhorrado y calcularProgreso funcionan correctamente`() {
        val pagos = listOf(
            Payment("p1", "m1", "1", 1000000.0),
            Payment("p2", "m2", "1", 1500000.0)
        )
        val total = viewModel.calcularTotalAhorrado(pagos)
        val progreso = viewModel.calcularProgreso(total, 5000000.0)

        assertEquals(2500000.0, total, 0.01)
        assertEquals(0.5f, progreso, 0.01f)
    }
}