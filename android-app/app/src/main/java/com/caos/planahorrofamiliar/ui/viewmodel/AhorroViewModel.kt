package com.caos.planahorrofamiliar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.caos.planahorrofamiliar.data.model.*
import com.caos.planahorrofamiliar.data.repository.AhorroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AhorroUiState(
    val planes: List<Plan> = emptyList(),
    val planSeleccionado: Plan? = null,
    val miembrosDelPlan: List<Member> = emptyList(),
    val pagosDelPlan: List<Payment> = emptyList(),
    val totalAhorrado: Double = 0.0,
    val progreso: Float = 0f,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AhorroViewModel(private val repository: AhorroRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AhorroUiState())
    val uiState: StateFlow<AhorroUiState> = _uiState.asStateFlow()

    init {
        cargarPlanes()
    }

    fun cargarPlanes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.obtenerPlanes()
                .onSuccess { lista ->
                    _uiState.update { it.copy(planes = lista, isLoading = false) }
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
                }
        }
    }

    fun crearPlan(
        motivo: String,
        metaTexto: String,
        mesesTexto: String,
        miembrosTexto: String,
        onSuccess: () -> Unit
    ) {
        val meta = metaTexto.toDoubleOrNull() ?: 0.0
        val meses = mesesTexto.toIntOrNull() ?: 1
        val listaMiembros = miembrosTexto.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (motivo.isBlank() || meta <= 0 || meses <= 0) {
            _uiState.update { it.copy(errorMessage = "Completa todos los campos correctamente") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.crearPlanConMiembros(
                nombre = motivo,
                motivo = motivo,
                meta = meta,
                meses = meses,
                nombresMiembros = listaMiembros
            ).onSuccess {
                cargarPlanes()
                onSuccess()
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
            }
        }
    }

    fun cargarDetallePlan(planId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val plan = repository.obtenerDetallePlan(planId).getOrNull()
            val miembros = repository.obtenerMiembrosPlan(planId).getOrDefault(emptyList())
            val pagos = repository.obtenerPagosPlan(planId).getOrDefault(emptyList())

            val total = calcularTotalAhorrado(pagos)
            val porcentaje = calcularProgreso(total, plan?.targetAmount ?: 0.0)

            _uiState.update {
                it.copy(
                    planSeleccionado = plan,
                    miembrosDelPlan = miembros,
                    pagosDelPlan = pagos,
                    totalAhorrado = total,
                    progreso = porcentaje,
                    isLoading = false
                )
            }
        }
    }

    fun registrarPago(planId: String, memberId: String, montoTexto: String, onSuccess: () -> Unit) {
        val monto = montoTexto.toDoubleOrNull() ?: 0.0
        if (memberId.isBlank() || monto <= 0) {
            _uiState.update { it.copy(errorMessage = "Selecciona un miembro y un monto válido") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.registrarPago(planId, memberId, monto)
                .onSuccess {
                    cargarDetallePlan(planId)
                    onSuccess()
                }
                .onFailure { err ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
                }
        }
    }

    fun limpiarError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // Funciones puras para validación y pruebas unitarias
    fun calcularTotalAhorrado(pagos: List<Payment>): Double = pagos.sumOf { it.amount }

    fun calcularProgreso(totalAhorrado: Double, meta: Double): Float {
        if (meta <= 0.0) return 0f
        return (totalAhorrado / meta).toFloat().coerceIn(0f, 1f)
    }

    class Factory(private val repository: AhorroRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AhorroViewModel(repository) as T
        }
    }
}