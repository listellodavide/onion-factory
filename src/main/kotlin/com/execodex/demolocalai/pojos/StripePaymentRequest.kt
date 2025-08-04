package com.execodex.demolocalai.pojos

/**
 * Request object for creating a payment intent.
 */
data class CreatePaymentIntentRequest(
    val orderId: Long
)

/**
 * Response object containing payment intent details.
 */
data class PaymentIntentResponse(
    val clientSecret: String,
    val paymentIntentId: String,
    val amount: Long,
    val currency: String
)

/**
 * Request object for confirming a payment.
 */
data class ConfirmPaymentRequest(
    val paymentIntentId: String
)

/**
 * Response object for payment confirmation.
 */
data class PaymentConfirmationResponse(
    val orderId: Long,
    val status: String,
    val success: Boolean,
    val message: String
)

/**
 * Request object for creating a checkout session.
 */
data class CreateCheckoutSessionRequest(
    val orderId: Long,
    val successUrl: String = "https://example.com/success",
    val cancelUrl: String = "https://example.com/cancel"
)

/**
 * Response object containing checkout session details.
 */
data class CheckoutSessionResponse(
    val sessionId: String,
    val checkoutUrl: String
)