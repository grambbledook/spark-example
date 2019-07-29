package com.github.grambbledook.example.spark.domain.request

import java.math.BigDecimal

data class CreateAccountRequest(val amount: BigDecimal, val owner: String)
