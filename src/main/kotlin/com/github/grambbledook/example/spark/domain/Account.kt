package com.github.grambbledook.example.spark.domain

import java.math.BigDecimal

data class Account(val id: Long, val amount: BigDecimal, val owner: String)