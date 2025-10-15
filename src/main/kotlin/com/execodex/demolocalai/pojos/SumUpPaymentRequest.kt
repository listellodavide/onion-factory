package com.execodex.demolocalai.pojos

import java.math.BigDecimal
import java.time.Instant

/**
 * Request to create a new SumUp checkout
 */
data class CreateCheckoutRequest(
    val checkoutReference: String,
    val amount: BigDecimal,
    val currency: String,
    val merchantCode: String,
    val description: String? = null,
    val returnUrl: String? = null,
    val payToEmail: String? = null
)

/**
 * Response from creating a SumUp checkout
 */
data class CreateCheckoutResponse(
    val id: String,
    val checkoutReference: String,
    val amount: BigDecimal,
    val currency: String,
    val status: CheckoutStatus,
    val date: Instant,
    val merchantCode: String,
    val description: String? = null,
    val returnUrl: String? = null,
    val transactions: List<Transaction>? = null
)

/**
 * Response from retrieving a checkout
 */
data class RetrieveCheckoutResponse(
    val id: String,
    val checkoutReference: String,
    val amount: BigDecimal,
    val currency: String,
    val status: CheckoutStatus,
    val date: Instant,
    val merchantCode: String,
    val description: String? = null,
    val returnUrl: String? = null,
    val transactions: List<Transaction>? = null,
    val validUntil: Instant? = null
)

/**
 * Request to process a checkout payment
 */
data class ProcessCheckoutRequest(
    val checkoutId: String,
    val paymentType: String,
    val token: String? = null,
    val installments: Int? = null
)

/**
 * Response from processing a checkout
 */
data class ProcessCheckoutResponse(
    val id: String,
    val checkoutReference: String,
    val amount: BigDecimal,
    val currency: String,
    val status: CheckoutStatus,
    val date: Instant,
    val merchantCode: String,
    val transactions: List<Transaction>
)

/**
 * Response from deactivating a checkout
 */
data class DeactivateCheckoutResponse(
    val id: String,
    val checkoutReference: String,
    val status: CheckoutStatus
)

/**
 * Response containing available payment methods
 */
data class PaymentMethodsResponse(
    val paymentMethods: List<PaymentMethod>
)

/**
 * Payment method details
 */
data class PaymentMethod(
    val type: String,
    val name: String,
    val enabled: Boolean
)

/**
 * Transaction details
 */
data class Transaction(
    val id: String,
    val transactionCode: String,
    val amount: BigDecimal,
    val currency: String,
    val timestamp: Instant,
    val status: TransactionStatus,
    val paymentType: String,
    val entryMode: String? = null,
    val authCode: String? = null,
    val installmentsCount: Int? = null
)

/**
 * Webhook callback payload from SumUp
 */
data class SumUpWebhookPayload(
    val eventType: String,
    val eventId: String,
    val timestamp: Instant,
    val checkoutId: String,
    val checkoutReference: String,
    val amount: BigDecimal,
    val currency: String,
    val status: CheckoutStatus,
    val transactionId: String? = null,
    val transactionCode: String? = null
)

/**
 * Checkout status enum
 */
enum class CheckoutStatus {
    PENDING,
    FAILED,
    PAID
}

/**
 * Transaction status enum
 */
enum class TransactionStatus {
    SUCCESSFUL,
    FAILED,
    CANCELLED,
    PENDING
}
