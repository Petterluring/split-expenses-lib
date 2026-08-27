package com.petterluring.splitexpenses.actors

/**
 * Represents a debtor for a particular expense.
 * @property share - States the portion of the expense that the debtor owes. Valid values are [0.0, 1.0].
 */
class Debtor(
    val share: Double,
) {
    init {
        require(share in 0.0..1.0) { "Share: $share, should be in [0, 1]." }
    }

    fun debt(expense: Double): Double {
        require(expense >= 0.0) { "Expense $expense must be >= 0.0." }
        return share * expense
    }
}
