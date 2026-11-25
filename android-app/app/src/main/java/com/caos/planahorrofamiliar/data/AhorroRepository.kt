package com.caos.planahorrofamiliar.data.repository

import com.caos.planahorrofamiliar.data.model.*
import com.caos.planahorrofamiliar.data.remote.AhorroApiService

class AhorroRepository(private val api: AhorroApiService) {

    suspend fun obtenerPlanes(): Result<List<Plan>> = runCatching {
        val response = api.getPlans()
        if (response.isSuccessful) response.body() ?: emptyList()
        else throw Exception("Error ${response.code()} al obtener planes")
    }

    suspend fun crearPlanConMiembros(
        nombre: String,
        motivo: String,
        meta: Double,
        meses: Int,
        nombresMiembros: List<String>
    ): Result<Plan> = runCatching {
        val planResponse = api.createPlan(
            CreatePlanRequest(
                name = nombre,
                motive = motivo,
                targetAmount = meta,
                months = meses
            )
        )
        if (!planResponse.isSuccessful || planResponse.body() == null) {
            throw Exception("Error ${planResponse.code()} al crear plan")
        }
        val planCreado = planResponse.body()!!

        val aporteMensual = if (nombresMiembros.isNotEmpty() && meses > 0) {
            meta / meses / nombresMiembros.size
        } else 0.0

        nombresMiembros.forEach { nombreMiembro ->
            api.createMember(
                CreateMemberRequest(
                    name = nombreMiembro,
                    planId = planCreado.id,
                    contributionPerMonth = aporteMensual
                )
            )
        }
        planCreado
    }

    suspend fun obtenerDetallePlan(planId: String): Result<Plan> = runCatching {
        val response = api.getPlanById(planId)
        if (response.isSuccessful && response.body() != null) response.body()!!
        else throw Exception("Plan no encontrado")
    }

    suspend fun obtenerMiembrosPlan(planId: String): Result<List<Member>> = runCatching {
        val response = api.getMembersByPlan(planId)
        if (response.isSuccessful) response.body() ?: emptyList()
        else emptyList()
    }

    suspend fun obtenerPagosPlan(planId: String): Result<List<Payment>> = runCatching {
        val response = api.getPaymentsByPlan(planId)
        when {
            response.isSuccessful -> response.body() ?: emptyList()
            response.code() == 404 -> emptyList()
            else -> throw Exception("Error ${response.code()} al consultar pagos")
        }
    }

    suspend fun registrarPago(planId: String, memberId: String, monto: Double): Result<Payment> = runCatching {
        val response = api.createPayment(
            CreatePaymentRequest(planId = planId, memberId = memberId, amount = monto)
        )
        if (response.isSuccessful && response.body() != null) response.body()!!
        else throw Exception("Error ${response.code()} al registrar pago")
    }
}