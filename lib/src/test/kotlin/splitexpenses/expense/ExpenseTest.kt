package splitexpenses.expense

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import splitexpenses.parties.Creditor
import splitexpenses.parties.Debtor
import java.util.stream.Stream
import kotlin.math.abs

class ExpenseTest {
    private val expense =
        Expense(
            name = "Dinner at Cargos",
            "Dinner with friends, Saturday night.",
            5000.0,
            Creditor(
                "John",
                0.20,
            ),
            listOf(
                Debtor("Lisa", 0.20),
                Debtor("Garret", 0.20),
                Debtor("Peter", 0.40),
            ),
        )

    companion object {
        val expenses0 =
            listOf(
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
                ),
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
                ),
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
                ),
            )

        val expenses1 =
            listOf(
                Expense(
                    name = "Dinner at Restaurant",
                    "Dinner with friends, Saturday evening.",
                    1200.0,
                    Creditor(
                        "John",
                        0.25,
                    ),
                    listOf(
                        Debtor("Lisa", 0.25),
                        Debtor("Garret", 0.25),
                        Debtor("Peter", 0.25),
                    ),
                ),
                Expense(
                    name = "Movie night",
                    "Movies and snacks with friends.",
                    800.0,
                    Creditor(
                        "Garret",
                        0.30,
                    ),
                    listOf(
                        Debtor("John", 0.20),
                        Debtor("Lisa", 0.25),
                        Debtor("Peter", 0.25),
                    ),
                ),
            )

        val expenses2 =
            listOf(
                Expense(
                    name = "Bowling",
                    "Bowling with friends on Saturday.",
                    600.0,
                    Creditor(
                        "Lisa",
                        0.20,
                    ),
                    listOf(
                        Debtor("John", 0.30),
                        Debtor("Garret", 0.25),
                        Debtor("Peter", 0.25),
                    ),
                ),
                Expense(
                    name = "Breakfast",
                    "Breakfast with friends on Sunday morning.",
                    500.0,
                    Creditor(
                        "Peter",
                        0.25,
                    ),
                    listOf(
                        Debtor("John", 0.25),
                        Debtor("Lisa", 0.20),
                        Debtor("Garret", 0.30),
                    ),
                ),
            )

        @JvmStatic
        fun expensesProvider(): Stream<List<Expense>> = Stream.of(expenses0, expenses1, expenses2)
    }

    @Test
    fun `throws exception when shares sum != 1`() {
        val exception =
            assertThrows<IllegalArgumentException> {
                Expense(
                    name = "Dinner at Cargos",
                    "Dinner with friends, Saturday night.",
                    5000.0,
                    Creditor(
                        "John",
                        0.20,
                    ),
                    listOf(
                        Debtor("Lisa", 0.20),
                        Debtor("Garret", 0.20),
                        Debtor("Peter", 0.50),
                    ),
                ) // shares: 1.1
            }
        assertTrue(exception.message!!.contains("close to 1.0"))
    }

    @Test
    fun `throws exception when names are not unique`() {
        val exception =
            assertThrows<IllegalArgumentException> {
                Expense(
                    name = "Dinner at Cargos",
                    "Dinner with friends, Saturday night.",
                    5000.0,
                    Creditor(
                        "John",
                        0.20,
                    ),
                    listOf(
                        Debtor("Garret", 0.20),
                        Debtor("Garret", 0.20),
                        Debtor("Peter", 0.40),
                    ),
                ) // Garret appears twice
            }
        assertTrue(exception.message!!.contains("must be unique"))
    }

    @Test
    fun `derives tab correctly`() {
        val tab = Expense.globalTab(expenses0)
        assertEquals(350.0, tab["Lisa"])
        assertEquals(-350.0, tab["John"])
        assertEquals(100.0, tab["Garret"])
        assertEquals(150.0, tab["Peter"])
        assertEquals(-250.0, tab["Emma"])
        assertEquals(0.0, tab.values.sum())
    }

    @ParameterizedTest
    @MethodSource("expensesProvider")
    fun `settles expenses correctly`(expenses: List<Expense>) {
        val tab = Expense.globalTab(expenses).toMutableMap()
        val payments = Expense.settleFromTab(tab)

        // Test if the returned payments will zero out debts and credits.
        payments.forEach { payment ->
            tab[payment.to] = tab[payment.to]!! - payment.amount
            tab[payment.from] = tab[payment.from]!! + payment.amount
        }
        tab.values.forEach { assertEquals(0.0, it) }
    }

    @Test
    fun `settles expenses correctly when shares approximately equals 1`() {
        val expense =
            Expense(
                "Flight to the U.S",
                "Flying to the U.S for vacation",
                10000.0,
                Creditor("Allan", 1.0 / 3.0),
                listOf(
                    Debtor("John", 1.0 / 3.0),
                    Debtor("Gunnar", 1.0 / 3.0),
                ),
            )
        val tab = expense.tab().toMutableMap()
        val payments = Expense.settleFromTab(tab)
        payments.forEach { payment ->
            tab[payment.to] = tab[payment.to]!! - payment.amount
            tab[payment.from] = tab[payment.from]!! + payment.amount
        }
        tab.values.forEach { assertTrue(abs(it) <= 1e-6) }
    }
}
