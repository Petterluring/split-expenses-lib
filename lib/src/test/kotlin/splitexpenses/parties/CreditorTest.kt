package splitexpenses.parties

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreditorTest {
    private val creditor = Creditor("John", 0.2)

    @Test
    fun `calculates credit correctly`() {
        assertEquals(800.0, creditor.credit(1000.0))
        assertEquals(1600.0, creditor.credit(2000.0))
    }

    @Test
    fun `throws exception when expense value is invalid for credit calculation`() {
        val exception =
            assertThrows<IllegalArgumentException> {
                creditor.credit(-1.0)
            }
        assertTrue(exception.message!!.contains("must be >= 0.0"))
    }
}
