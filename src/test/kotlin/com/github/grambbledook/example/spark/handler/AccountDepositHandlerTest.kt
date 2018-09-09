package com.github.grambbledook.example.spark.handler

import com.github.grambbledook.example.spark.dto.Account
import com.github.grambbledook.example.spark.dto.Failure
import com.github.grambbledook.example.spark.dto.Success
import com.github.grambbledook.example.spark.service.AccountService

import io.mockk.every
import io.mockk.mockk
import io.vavr.control.Try
import org.junit.Assert.assertEquals
import org.junit.Test

internal typealias Request = AccountDepositHandler.AccountDepositRequest

class AccountDepositHandlerTest {

    private val service = mockk<AccountService>()
    private val handler = AccountDepositHandler(service, mockk())

    @Test
    fun testDepositResultsInSuccessResult() {
        every {
            service.deposit(100, 1000.00)
        }.returns(
                Try.success(Account(100, 1200.00, "John doe"))
        )

        val result = handler.process(Request(100, 1000.00)) as Success<Account>

        assertEquals(100, result.payload.id)
        assertEquals(1200.00, result.payload.amount, 1e-2)
    }

    @Test
    fun testInternalErrorCausesCode500() {
        every { service.deposit(any(), any()) }.returns(Try.failure(Exception("Error thrown")))

        val result = handler.process(Request(100, 1000.00)) as Failure

        assertEquals(500, result.code)
        assertEquals("Error thrown", result.reason)
    }
}