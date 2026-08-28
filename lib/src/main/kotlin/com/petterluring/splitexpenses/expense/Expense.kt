package com.petterluring.splitexpenses.expense

import com.petterluring.splitexpenses.parties.Creditor
import com.petterluring.splitexpenses.parties.Debtor

/**
 * Represents a shared expense that was paid by one individual in a group. This individual becomes the creditor
 * while the remaining group members become debtors who pay their share to the creditor such that the expense is
 * settled fairly.
 */
class Expense(
    var name: String,
    var description: String,
    private var _amount: Double,
    val creditor: Creditor,
    val debtors: List<Debtor>,
) {
    var amount: Double
        get() = _amount
        set(value) {
            require(value >= 0.0) { "Value $value must be >= 0.0." }
            _amount = value
        }

    init {
        val shares = creditor.share + debtors.sumOf { it.share }
        require(shares == 1.0) { "Shares $shares should be equal to 1.0" }
    }

    fun resolveExpense() {
        // TODO: Implement function after the payment graph is developed.
    }
}
