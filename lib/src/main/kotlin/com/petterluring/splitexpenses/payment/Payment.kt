package com.petterluring.splitexpenses.payment

class Payment(
    val from: String,
    val to: String,
    val amount: Double,
) {
    init {
        require(amount >= 0.0) { "Amount must be >= 0, was $amount." }
    }
}
