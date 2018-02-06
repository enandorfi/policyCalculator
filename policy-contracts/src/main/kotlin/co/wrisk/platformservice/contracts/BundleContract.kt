package co.wrisk.platformservice.contracts

import java.math.BigDecimal

data class BundleContract(
        val sumInsured: BigDecimal,
        val excess: BigDecimal
)