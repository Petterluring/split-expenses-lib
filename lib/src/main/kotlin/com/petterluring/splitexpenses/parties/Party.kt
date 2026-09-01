package com.petterluring.splitexpenses.parties

/**
 * Represents individual in a group who has a debt or credit for a shared expense.
 * @property name - Name of the individual.
 * @property share - States the share/portion of an expense that the individual owns. Valid values are [0.0, 1.0].
 */
abstract class Party(
    val name: String,
    val share: Double,
) {
    init {
        require(share in 0.0..1.0) { "Share: $share, should be in [0, 1]." }
    }
}
