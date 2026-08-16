package com.example.auth

enum class UserRole {
    UNAUTHENTICATED,
    CUSTOMER,
    ADMIN
}

data class AuthUser(
    val role: UserRole = UserRole.UNAUTHENTICATED,
    val customerId: String = "",
    val customerName: String = "",
    val email: String = "",
    val mobileNumber: String = "",
    val dbCustomerId: Long = 0L,
    val isBlocked: Boolean = false,
    val photoUrl: String? = null
)
