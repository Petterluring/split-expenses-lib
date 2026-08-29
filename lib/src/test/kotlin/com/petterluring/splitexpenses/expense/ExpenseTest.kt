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
        val tab = expense.tab()
        assertEquals(-1000.0, tab["Lisa"])
        assertEquals(-1000.0, tab["Garret"])
        assertEquals(4000.0, tab["John"])
        assertEquals(-2000.0, tab["Peter"])
        assertEquals(0.0, tab.values.sum())
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
    fun `derives global tab correctly`() {
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
        val tab = Expense.globalTab(expenses)
        assertEquals(350.0, tab["Lisa"])
        assertEquals(-350.0, tab["John"])
        assertEquals(100.0, tab["Garret"])
        assertEquals(150.0, tab["Peter"])
        assertEquals(-250.0, tab["Emma"])
        assertEquals(0.0, tab.values.sum())
    }
}
