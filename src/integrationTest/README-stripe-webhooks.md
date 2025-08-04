# Stripe Webhook Integration

This document explains how Stripe webhooks are implemented in the application to handle payment confirmations automatically.

## How Stripe Webhooks Work

1. **Webhook Flow**:
   - When a payment event occurs in Stripe (like a successful payment), Stripe sends an HTTP POST request to your webhook endpoint.
   - Your application processes this event and updates the order status accordingly.
   - Your application returns a 200 OK response to acknowledge receipt of the event.

2. **Key Events**:
   - `checkout.session.completed`: Triggered when a customer completes the checkout process.
   - `payment_intent.succeeded`: Triggered when a payment is successfully processed.

## Implementation Details

### 1. Webhook Endpoint

The application exposes a webhook endpoint at:
```
POST /api/payments/webhook
```

This endpoint receives events from Stripe and processes them based on the event type.

### 2. Event Processing

The application handles two main types of events:

- **checkout.session.completed**: When a customer completes the checkout process
- **payment_intent.succeeded**: When a payment is successfully processed

For both events, the application:
1. Extracts the order ID from the event metadata
2. Retrieves the order from the database
3. Updates the order status to "PAID"
4. Returns a success response to Stripe

### 3. Security Considerations

In a production environment, you should implement additional security measures:

- **Signature Verification**: Verify that the webhook request actually came from Stripe using the Stripe-Signature header.
- **Idempotency**: Ensure events are processed only once, even if Stripe sends duplicate events.

## Testing Webhooks

For testing purposes, you can use the provided test script:

```bash
cd src/integrationTest/scripts
./stripe-webhook-test.sh
```

This script simulates Stripe sending webhook events to your application.

## Production Setup

For a production environment, you need to:

1. **Register Your Webhook URL**: In your Stripe Dashboard, go to Developers > Webhooks and add your webhook endpoint URL.

2. **Configure Event Types**: Select which events you want to receive (at minimum, `checkout.session.completed` and `payment_intent.succeeded`).

3. **Get Your Webhook Secret**: Stripe provides a signing secret that you should store securely in your application configuration.

4. **Implement Signature Verification**: Update the webhook handler to verify the Stripe-Signature header using the webhook secret.

## Troubleshooting

If webhooks aren't working as expected:

1. Check your application logs for any errors in processing webhook events.
2. Verify that the webhook URL is accessible from the internet.
3. In the Stripe Dashboard, you can view recent webhook attempts and their status.
4. Use Stripe's webhook testing tools to send test events to your endpoint.