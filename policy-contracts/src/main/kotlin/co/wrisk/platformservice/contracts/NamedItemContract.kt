package co.wrisk.platformservice.contracts

import java.math.BigDecimal

data class NamedItemContract(
        val name: String,
        val sumInsured: BigDecimal,
        val excess: BigDecimal
)