package com.github.grambbledook.example.spark.service

import org.junit.Before

interface AccountFixture {
    val service: InMemoryAccountServiceImpl

    @Before
    fun setup() {
        service.create(FIRST, 100.00, "John Doe")
        service.create(SECOND, 0.00, "John Doe")
    }

    companion object {
        const val FIRST = 1L
        const val SECOND = 2L
        const val UNKNOWN = Long.MIN_VALUE
    }
}