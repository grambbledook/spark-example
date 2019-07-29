package com.github.grambbledook.example.spark.domain.request

import java.math.BigDecimal

data class AccountTransferRequest(val from: Long, val to: Long, val amount: BigDecimal)
