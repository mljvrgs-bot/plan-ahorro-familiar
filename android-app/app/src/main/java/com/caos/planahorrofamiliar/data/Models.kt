package com.caos.planahorrofamiliar.data.model

import com.google.gson.annotations.SerializedName

data class Plan(
    @SerializedName("_id") val id: String = "",
    val name: String,
    val motive: String? = "",
    val targetAmount: Double,
    val months: Int,
    val createdAt: String? = null
)

data class CreatePlanRequest(
    val name: String,
    val motive: String,
    val targetAmount: Double,
    val months: Int
)

data class Member(
    @SerializedName("_id") val id: String = "",
    val name: String,
    val planId: String,
    val contributionPerMonth: Double,
    val joinedAt: String? = null
)

data class CreateMemberRequest(
    val name: String,
    val planId: String,
    val contributionPerMonth: Double
)

data class Payment(
    @SerializedName("_id") val id: String = "",
    val memberId: String,
    val planId: String,
    val amount: Double,
    val date: String? = null
)

data class CreatePaymentRequest(
    val planId: String,
    val memberId: String,
    val amount: Double
)