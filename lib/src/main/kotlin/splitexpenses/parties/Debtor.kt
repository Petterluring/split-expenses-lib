package splitexpenses.parties

/**
 * Represents individual owing money to a creditor in a group.
 */
class Debtor(
    name: String,
    share: Double,
) : Party(name, share) {
    /**
     * Return the debt for a given expense.
     * @param - Expense value. Must be >= 0.
     * @return - Debt value.
     */
    fun debt(expense: Double): Double {
        require(expense >= 0.0) { "Expense $expense must be >= 0.0." }
        return share * expense
    }
}
