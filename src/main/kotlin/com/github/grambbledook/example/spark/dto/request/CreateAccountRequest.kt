package com.github.grambbledook.example.spark.dto.request

import java.math.BigDecimal

data class CreateAccountRequest(val amount: BigDecimal, val owner: String)
