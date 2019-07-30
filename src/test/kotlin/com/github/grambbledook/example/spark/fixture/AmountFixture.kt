package com.github.grambbledook.example.spark.fixture

import java.math.BigDecimal

interface AmountFixture {
    companion object {
        val ZERO: BigDecimal = BigDecimal.ZERO
        val ONE: BigDecimal = BigDecimal.ONE

        val FORTY: BigDecimal = BigDecimal(40.00)
        val SIXTY: BigDecimal = BigDecimal(60.00)
        val HUNDRED: BigDecimal = BigDecimal(100.00)
        val THOUSAND: BigDecimal = BigDecimal(1000)
    }
}