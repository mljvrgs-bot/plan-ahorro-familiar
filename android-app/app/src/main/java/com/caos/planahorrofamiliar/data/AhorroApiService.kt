package com.caos.planahorrofamiliar.data.remote

import com.caos.planahorrofamiliar.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface AhorroApiService {

    // --- PLANES ---
    @GET("api/plans")
    suspend fun getPlans(): Response<List<Plan>>

    @POST("api/plans")
    suspend fun createPlan(@Body request: CreatePlanRequest): Response<Plan>

    @GET("api/plans/{id}")
    suspend fun getPlanById(@Path("id") planId: String): Response<Plan>

    // --- MIEMBROS ---
    @POST("api/members")
    suspend fun createMember(@Body request: CreateMemberRequest): Response<Member>

    @GET("api/members/plan/{planId}")
    suspend fun getMembersByPlan(@Path("planId") planId: String): Response<List<Member>>

    // --- PAGOS ---
    @POST("api/payments")
    suspend fun createPayment(@Body request: CreatePaymentRequest): Response<Payment>

    @GET("api/payments/plan/{planId}")
    suspend fun getPaymentsByPlan(@Path("planId") planId: String): Response<List<Payment>>
}