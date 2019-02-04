package com.github.grambbledook.example.spark.dto

import java.math.BigDecimal

data class Account(val id: Long, val amount: BigDecimal, val owner: String)