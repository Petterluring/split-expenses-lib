package com.petterluring.splitexpenses.expense

import com.petterluring.splitexpenses.parties.Creditor
import com.petterluring.splitexpenses.parties.Debtor
import kotlin.math.abs

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
        require(abs(1.0 - shares) < 1e-4) { "Shares $shares should close to 1.0. Acceptable tolerance: 1e-4" }
        val names = debtors.map { it.name }.plus(creditor.name)
        require(names.size == names.distinct().size) { "Names of debtors and creditor must be unique." }
    }

    companion object {
        /**
         * Return a map with the names of the involving parties from a set of expenses as keys and their
         * accumulated debts/credits as values. Positive values represent credits while negative values
         * represent debts.
         */
        fun globalTab(expenses: List<Expense>): Map<String, Double> {
            val balance = mutableMapOf<String, Double>()
            expenses.forEach { expense ->
                val creditor = expense.creditor
                val debtors = expense.debtors
                val amount = expense.amount

                balance[creditor.name] = (balance[creditor.name] ?: 0.0) + creditor.credit(amount)
                debtors.forEach { debtor ->
                    balance[debtor.name] = (balance[debtor.name] ?: 0.0) - debtor.debt(amount)
                }
            }
            return balance
        }
    }

    /**
     * Return a map with the names of the involving parties as keys and their debt/credit as
     * values. Positive values represent credits while negative values represent debts.
     */
    fun tab(): Map<String, Double> =
        buildMap {
            put(creditor.name, creditor.credit(_amount))
            debtors.forEach { debtor -> put(debtor.name, -debtor.debt(_amount)) }
        }
}
