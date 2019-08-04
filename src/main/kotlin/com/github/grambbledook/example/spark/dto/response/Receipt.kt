package com.github.grambbledook.example.spark.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Receipt(val operation: TransactionType,
                   val accountId: Long,
                   val amount: BigDecimal?,
                   val balance: BigDecimal)