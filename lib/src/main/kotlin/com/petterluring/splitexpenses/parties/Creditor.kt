package com.petterluring.splitexpenses.parties

/**
 * Represents individual that collects debts from one or more debtors in a group with shared expenses.
 */
class Creditor(
    name: String,
    share: Double,
) : Party(name, share) {
    fun credit(expense: Double): Double {
        require(expense >= 0) { "Expense $expense must be >= 0.0." }
        return expense * (1.0 - share)
    }
}
