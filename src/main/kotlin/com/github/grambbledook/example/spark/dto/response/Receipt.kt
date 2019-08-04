package com.github.grambbledook.example.spark.dto.response

import java.math.BigDecimal

data class Receipt(val operation: TransactionType,
                   val accountId: Long,
                   val amount: BigDecimal?,
                   val balance: BigDecimal)