package splitexpenses.payment

/**
 * Represents a payment that is to be made from a debtor to a creditor.
 * @property from - Name of debtor.
 * @property to - Name of creditor.
 * @property amount - Monetary value to pay to creditor.
 */
class Payment(
    val from: String,
    val to: String,
    val amount: Double,
) {
    init {
        require(amount >= 0.0) { "Amount must be >= 0, was $amount." }
    }
}
