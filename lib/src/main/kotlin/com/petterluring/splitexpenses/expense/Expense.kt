package com.petterluring.splitexpenses.expense

import com.petterluring.splitexpenses.parties.Creditor
import com.petterluring.splitexpenses.parties.Debtor
import com.petterluring.splitexpenses.payment.Payment
import kotlin.math.abs

/**
 * Represents a shared expense that was paid by one individual in a group. This individual becomes the creditor
 * while the remaining group members become debtors who pay their share to the creditor such that the expense is
 * settled fairly.
 */
class Expense(
    var name: String,
    var description: String,
    amount: Double,
    val creditor: Creditor,
    val debtors: List<Debtor>,
) {
    var amount: Double = amount
        set(value) {
            require(value >= 0.0) { "Amount $value must be >= 0.0." }
            field = value
        }

    init {
        val shares = creditor.share + debtors.sumOf { it.share }
        require(abs(1.0 - shares) <= TOLERANCE) { "Shares $shares should be close to 1.0. Acceptable tolerance: +-$TOLERANCE" }
        val names = debtors.map { it.name }.plus(creditor.name)
        require(names.size == names.distinct().size) { "Names of debtors and creditor must be unique." }
    }

    companion object {
        private const val TOLERANCE = 1e-6

        /**
         * Settle a set of shared expenses by returning a set of payments stating
         * how group members should pay each other.
         */
        fun settle(expenses: List<Expense>): List<Payment> = settleFromTab(globalTab(expenses))

        /**
         * Settle a set of shared expenses by returning a set of payments stating
         * how group members should pay each other.
         * @param tab - A map summarizing the net debts and credits of group members. Debts are negative values
         *              while credits are positive. The sum of all credits and debts should be close to 0.
         */
        fun settleFromTab(tab: Map<String, Double>): List<Payment> {
            require(abs(tab.values.sum()) <= TOLERANCE) { "Values in tab must approximately sum to 0" }

            val payments = mutableListOf<Payment>()
            val items = tab.entries.toList().sortedBy { it.value }

            var i = 0
            var j = items.size - 1

            var credit = 0.0
            var debt = 0.0
            while (i < j) {
                val debtor = items[i]
                val creditor = items[j]
                credit = if (abs(credit) <= TOLERANCE) creditor.value else credit
                debt = if (abs(debt) <= TOLERANCE) -debtor.value else debt // debtor.value becomes positive when applying -

                var payAmount: Double
                if (credit - debt >= 0) {
                    payAmount = debt
                    credit -= debt
                    debt = 0.0
                } else {
                    payAmount = credit
                    debt -= credit
                    credit = 0.0
                }

                payments.add(Payment(debtor.key, creditor.key, payAmount))

                if (abs(debt) <= TOLERANCE) i++
                if (abs(credit) <= TOLERANCE) j--
            }
            return payments.toList()
        }

        /**
         * Return a map with the names of the involving parties from a set of expenses as keys and their
         * accumulated debts/credits as values. Positive values represent net credits while negative values
         * represent net debts.
         */
        fun globalTab(expenses: List<Expense>): Map<String, Double> {
            val tab = mutableMapOf<String, Double>()
            expenses.forEach { expense ->
                val creditor = expense.creditor
                val debtors = expense.debtors
                val amount = expense.amount

                tab[creditor.name] = (tab[creditor.name] ?: 0.0) + creditor.credit(amount)
                debtors.forEach { debtor ->
                    tab[debtor.name] = (tab[debtor.name] ?: 0.0) - debtor.debt(amount)
                }
            }
            return tab
        }
    }

    /**
     * Return a map with the names of the involving parties as keys and their debt/credit as
     * values. Positive values represent credits while negative values represent debts.
     */
    fun tab(): Map<String, Double> =
        buildMap {
            put(creditor.name, creditor.credit(amount))
            debtors.forEach { debtor -> put(debtor.name, -debtor.debt(amount)) }
        }
}
