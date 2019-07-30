package com.github.grambbledook.example.spark.dto.request

import java.math.BigDecimal

data class AccountTransferRequest(val from: Long, val to: Long, val amount: BigDecimal)
