package com.petterluring.splitexpenses.parties

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DebtorTest {
    private val debtor = Debtor("John", 0.20)

    @Test
    fun `initializes properties correctly`() {
        assertEquals("John", debtor.name)
        assertEquals(0.20, debtor.share)
    }

    @Test
    fun `throws exception for invalid share values`() {
        var exception =
            assertThrows<IllegalArgumentException> {
                Debtor("John", -0.1)
            }
        assertTrue(exception.message!!.contains("should be in [0, 1]"))

        exception =
            assertThrows<IllegalArgumentException> {
                Debtor("John", 1.1)
            }
        assertTrue(exception.message!!.contains("should be in [0, 1]"))
    }

    @Test
    fun `calculates debt correctly`() {
        assertEquals(200.0, debtor.debt(1000.0))
        assertEquals(400.0, debtor.debt(2000.0))
        assertEquals(0.0, debtor.debt(0.0))
    }

    @Test
    fun `throws exception during debt calculations when invalid expense value`() {
        val exception =
            assertThrows<IllegalArgumentException> {
                debtor.debt(-1.0)
            }
        assertTrue(exception.message!!.contains("must be >= 0.0"))
    }
}
