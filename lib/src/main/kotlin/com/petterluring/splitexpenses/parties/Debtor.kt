package com.petterluring.splitexpenses.parties

/**
 * Represents individual owing money to a creditor in a group.
 */
class Debtor(
    name: String,
    share: Double,
) : Party(name, share) {
    fun debt(expense: Double): Double {
        require(expense >= 0.0) { "Expense $expense must be >= 0.0." }
        return share * expense
    }
}
