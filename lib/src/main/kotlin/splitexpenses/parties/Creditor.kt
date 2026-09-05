package splitexpenses.parties

/**
 * Represents individual that collect debts from one or more debtors in a group with shared expenses.
 */
class Creditor(
    name: String,
    share: Double,
) : Party(name, share) {
    /**
     * Calculates the amount of money that a creditor should collect from the debtors.
     * @param - Expense value. Must be >= 0.
     * @return - Credit value.
     */
    fun credit(expense: Double): Double {
        require(expense >= 0) { "Expense $expense must be >= 0.0." }
        return expense * (1.0 - share)
    }
}
