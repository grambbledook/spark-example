package com.github.grambbledook.example.spark.domain.request

import java.math.BigDecimal

data class AccountDepositRequest(val id: Long, val amount: BigDecimal)
