import com.petterluring.splitexpenses.expense.Expense
import com.petterluring.splitexpenses.parties.Creditor
import com.petterluring.splitexpenses.parties.Debtor

fun main() {
    val bistro =
        Expense(
            name = "Lunch at Bistro",
            "Lunch with friends, Sunday afternoon.",
            1000.0,
            Creditor(
                "Lisa",
                0.25,
            ),
            listOf(
                Debtor("John", 0.15),
                Debtor("Garret", 0.30),
                Debtor("Peter", 0.30),
            ),
        )

    val pizzaNight =
        Expense(
            name = "Pizza night",
            "Pizza with friends, Friday evening.",
            1000.0,
            Creditor(
                "Peter",
                0.30,
            ),
            listOf(
                Debtor("John", 0.20),
                Debtor("Lisa", 0.25),
                Debtor("Garret", 0.25),
            ),
        )

    val movieTickets =
        Expense(
            name = "Movie tickets",
            "Movie night with friends.",
            1000.0,
            Creditor(
                "Garret",
                0.35,
            ),
            listOf(
                Debtor("Lisa", 0.15),
                Debtor("Peter", 0.25),
                Debtor("Emma", 0.25),
            ),
        )
    val expenses = listOf(bistro, pizzaNight, movieTickets)
    val payments = Expense.settle(expenses)

    payments.forEach {
        println(
            it.from + " " + it.to + " " + it.amount,
        )
    }
}
