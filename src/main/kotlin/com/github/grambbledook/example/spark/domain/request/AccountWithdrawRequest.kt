package com.github.grambbledook.example.spark.domain.request

import java.math.BigDecimal

data class AccountWithdrawRequest(val id: Long, val amount: BigDecimal)