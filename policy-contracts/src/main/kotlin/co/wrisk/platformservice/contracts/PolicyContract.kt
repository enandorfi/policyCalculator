package co.wrisk.platformservice.contracts

import java.math.BigDecimal

data class PolicyContract(
        val highestExcess: BigDecimal,
        val totalSumInsured: BigDecimal,
        val sections: List<SectionContract>
)