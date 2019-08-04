package com.github.grambbledook.example.spark.dto.domain

import java.math.BigDecimal

data class Account(val id: Long, val balance: BigDecimal, val owner: String)