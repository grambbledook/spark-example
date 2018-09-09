package com.github.grambbledook.example.spark.service

import org.junit.Before

interface AccountFixture {
    val service: InMemoryAccountServiceImpl

    @Before
    fun setup() {
        service.create(100.00, "Jane Doe").get().id
        service.create(0.00, "Jane Doe").get().id
    }

    companion object {
        const val FIRST = 1L
        const val SECOND = 2L
        const val UNKNOWN = Long.MIN_VALUE
    }
}