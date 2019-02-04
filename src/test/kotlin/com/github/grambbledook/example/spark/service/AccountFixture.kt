package com.github.grambbledook.example.spark.service

import com.github.grambbledook.example.spark.ext.right
import org.junit.Before
import java.math.BigDecimal

interface AccountFixture {
    val service: InMemoryAccountServiceImpl

    @Before
    fun setup() {
        service.create(BigDecimal(100.00), "Jane Doe").right().id
        service.create(BigDecimal(0.00), "Jane Doe").right().id
    }

    companion object {
        const val FIRST = 1L
        const val SECOND = 2L
        const val UNKNOWN = Long.MIN_VALUE
    }
}