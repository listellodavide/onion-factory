package com.execodex.demolocalai.pojos

/**
 * Request object for Stripe webhook events.
 * This is a simplified version that captures the essential fields needed for webhook processing.
 */
data class StripeWebhookRequest(
    val id: String,
    val type: String,
    val data: StripeEventData
)

/**
 * Data object containing the Stripe event object.
 */
data class StripeEventData(
    val `object`: StripeObject
)

/**
 * Generic Stripe object with common fields needed for webhook processing.
 * This is a simplified version that can represent different Stripe objects.
 */
data class StripeObject(
    val id: String,
    val `object`: String,
    val status: String? = null,
    val metadata: Map<String, String>? = null
)

/**
 * Response object for webhook processing.
 */
data class WebhookResponse(
    val received: Boolean,
    val eventId: String
)