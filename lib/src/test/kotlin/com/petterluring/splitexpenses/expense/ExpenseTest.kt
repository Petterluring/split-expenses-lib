package com.petterluring.splitexpenses.expense

import com.petterluring.splitexpenses.parties.Creditor
import com.petterluring.splitexpenses.parties.Debtor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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

    @Test
    fun `derives tab correctly`() {
        val balance = expense.tab()
        assertEquals(-1000.0, balance["Lisa"])
        assertEquals(-1000.0, balance["Garret"])
        assertEquals(4000.0, balance["John"])
        assertEquals(-2000.0, balance["Peter"])
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
}
