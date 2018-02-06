package co.wrisk.platformservice.contracts

import java.math.BigDecimal

data class SectionContract(
        val name: String,
        val sumInsured: BigDecimal,
        val bundle:  BundleContract?,
        val namedItems: List<NamedItemContract>
)

