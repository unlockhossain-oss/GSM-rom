package com.example.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.data.entity.CustomerEntity
import com.example.repository.GsmRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

sealed class AuthResult {
    data class Success(val user: AuthUser, val message: String = "Login successful") : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthManager(
    private val context: Context,
    private val repository: GsmRepository
) {
    private val firebaseAuth: FirebaseAuth? = try {
        FirebaseAuth.getInstance()
    } catch (e: Exception) {
        Log.w("AuthManager", "FirebaseAuth not initialized: ${e.message}")
        null
    }

    suspend fun loginCustomer(
        customerIdOrEmail: String,
        passwordInput: String
    ): AuthResult {
        if (customerIdOrEmail.isBlank()) {
            return AuthResult.Error("Please enter your Customer ID or Email")
        }
        if (passwordInput.isBlank()) {
            return AuthResult.Error("Please enter your password")
        }

        val trimmedQuery = customerIdOrEmail.trim()
        val customer = repository.findCustomerForLogin(trimmedQuery)
            ?: return AuthResult.Error("Customer account not found for '$trimmedQuery'")

        if (customer.isBlocked) {
            return AuthResult.Error("This customer account has been blocked by Admin. Please contact support.")
        }

        // Validate password (default fallback password is '123456' if not set)
        val validPass = if (customer.password.isNotBlank()) customer.password else "123456"
        if (passwordInput != validPass) {
            return AuthResult.Error("Incorrect password. Please check your credentials.")
        }

        val authUser = AuthUser(
            role = UserRole.CUSTOMER,
            customerId = if (customer.customerIdCode.isNotBlank()) customer.customerIdCode else "CUST-${customer.id}",
            customerName = customer.customerName,
            email = customer.gmail,
            mobileNumber = customer.mobileNumber,
            dbCustomerId = customer.id,
            isBlocked = customer.isBlocked
        )

        return AuthResult.Success(authUser, "Welcome back, ${customer.customerName}!")
    }

    suspend fun createCustomerAccount(
        name: String,
        customerId: String,
        mobileNumber: String,
        email: String,
        password: String,
        confirmPassword: String
    ): AuthResult {
        if (name.isBlank()) return AuthResult.Error("Customer Name is required")
        if (mobileNumber.isBlank()) return AuthResult.Error("Mobile Number is required")
        if (password.isBlank()) return AuthResult.Error("Password is required")
        if (password.length < 4) return AuthResult.Error("Password must be at least 4 characters")
        if (password != confirmPassword) return AuthResult.Error("Passwords do not match")

        val generatedId = if (customerId.isNotBlank()) customerId.trim().uppercase() else "CUST-${System.currentTimeMillis() % 10000}"

        // Check if customer ID already exists
        val existing = repository.findCustomerForLogin(generatedId)
        if (existing != null) {
            return AuthResult.Error("Customer ID '$generatedId' is already registered")
        }

        val newCustomer = CustomerEntity(
            customerIdCode = generatedId,
            password = password,
            isBlocked = false,
            customerName = name.trim(),
            mobileNumber = mobileNumber.trim(),
            gmail = email.trim(),
            status = "Pending",
            serviceType = "Software",
            problemDescription = "Account registered online via GSM ROM Portal",
            serviceNote = "New customer account created",
            createdAt = System.currentTimeMillis()
        )

        val insertedId = repository.insertCustomer(newCustomer)

        val authUser = AuthUser(
            role = UserRole.CUSTOMER,
            customerId = generatedId,
            customerName = name.trim(),
            email = email.trim(),
            mobileNumber = mobileNumber.trim(),
            dbCustomerId = insertedId,
            isBlocked = false
        )

        return AuthResult.Success(authUser, "Account created successfully!")
    }

    suspend fun loginAdmin(adminIdInput: String, adminPasswordInput: String, expectedId: String = "admin", expectedPass: String = "admin123"): AuthResult {
        if (adminIdInput.isBlank()) return AuthResult.Error("Please enter Admin ID")
        if (adminPasswordInput.isBlank()) return AuthResult.Error("Please enter Admin Password")

        val validId = if (expectedId.isNotBlank()) expectedId else "admin"
        val validPass = if (expectedPass.isNotBlank()) expectedPass else "admin123"

        if (adminIdInput.trim().equals(validId, ignoreCase = true) && adminPasswordInput == validPass) {
            val adminUser = AuthUser(
                role = UserRole.ADMIN,
                customerId = "ADMIN-01",
                customerName = "System Administrator",
                email = "admin@gsmrom.com"
            )
            return AuthResult.Success(adminUser, "Admin login authorized.")
        } else {
            return AuthResult.Error("Invalid Admin ID or Password. Default is admin / admin123")
        }
    }

    suspend fun signInWithGoogleCredential(credentialIdToken: String, displayName: String?, email: String?): AuthResult {
        try {
            if (firebaseAuth != null) {
                val credential = GoogleAuthProvider.getCredential(credentialIdToken, null)
                val authResult = firebaseAuth.signInWithCredential(credential).await()
                val fbUser = authResult.user
                val name = fbUser?.displayName ?: displayName ?: "Google User"
                val userEmail = fbUser?.email ?: email ?: "user@gmail.com"
                
                // Find or create customer
                var customer = repository.findCustomerForLogin(userEmail)
                if (customer == null) {
                    val code = "CUST-G${System.currentTimeMillis() % 10000}"
                    val newCust = CustomerEntity(
                        customerIdCode = code,
                        password = "123456",
                        isBlocked = false,
                        customerName = name,
                        mobileNumber = "",
                        gmail = userEmail,
                        status = "Pending",
                        serviceType = "Software",
                        problemDescription = "Google Authentication User",
                        serviceNote = "Authenticated via Google Sign-In"
                    )
                    val id = repository.insertCustomer(newCust)
                    customer = newCust.copy(id = id)
                }

                if (customer.isBlocked) {
                    return AuthResult.Error("This customer account is blocked by Admin.")
                }

                return AuthResult.Success(
                    AuthUser(
                        role = UserRole.CUSTOMER,
                        customerId = customer.customerIdCode.ifBlank { "CUST-${customer.id}" },
                        customerName = customer.customerName,
                        email = customer.gmail,
                        mobileNumber = customer.mobileNumber,
                        dbCustomerId = customer.id,
                        photoUrl = fbUser?.photoUrl?.toString()
                    ),
                    "Google Sign-In successful"
                )
            } else {
                // Fallback simulation/offline Google Sign-In
                val emailVal = email ?: "technician.user@gmail.com"
                val nameVal = displayName ?: "Google Customer"
                var customer = repository.findCustomerForLogin(emailVal)
                if (customer == null) {
                    val code = "CUST-G${System.currentTimeMillis() % 10000}"
                    val newCust = CustomerEntity(
                        customerIdCode = code,
                        password = "123456",
                        isBlocked = false,
                        customerName = nameVal,
                        mobileNumber = "01700000000",
                        gmail = emailVal,
                        status = "Pending"
                    )
                    val id = repository.insertCustomer(newCust)
                    customer = newCust.copy(id = id)
                }
                return AuthResult.Success(
                    AuthUser(
                        role = UserRole.CUSTOMER,
                        customerId = customer.customerIdCode.ifBlank { "CUST-${customer.id}" },
                        customerName = customer.customerName,
                        email = customer.gmail,
                        mobileNumber = customer.mobileNumber,
                        dbCustomerId = customer.id
                    ),
                    "Google Sign-In authorized"
                )
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Google sign-in error", e)
            return AuthResult.Error("Google Sign-In failed: ${e.localizedMessage ?: "Unknown error"}")
        }
    }
}
