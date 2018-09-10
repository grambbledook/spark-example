package com.github.grambbledook.example.spark.handler

import com.github.grambbledook.example.spark.dto.Account
import com.github.grambbledook.example.spark.dto.Failure
import com.github.grambbledook.example.spark.dto.Success
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.ALL_MONEY
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.FIRST
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.NEW_AMOUNT
import com.github.grambbledook.example.spark.service.AccountService
import io.mockk.every
import io.mockk.mockk
import io.vavr.control.Try
import org.junit.Assert.assertEquals
import org.junit.Test

internal typealias Request = AccountDepositHandler.AccountDepositRequest

class AccountDepositHandlerTest : HandlerFixture {

    private val service = mockk<AccountService>()
    private val handler = AccountDepositHandler(service, mockk())

    @Test
    fun testDepositResultsInSuccessResult() {
        every {
            service.deposit(FIRST, ALL_MONEY)
        }.returns(
                Try.success(Account(FIRST, NEW_AMOUNT, "John doe"))
        )

        val result = handler.process(Request(FIRST, ALL_MONEY)) as Success<Account>

        assertEquals(FIRST, result.payload.id)
        assertEquals(NEW_AMOUNT, result.payload.amount, 1e-2)
    }

    @Test
    fun testInternalErrorCausesCode500() {
        every { service.deposit(any(), any()) }.returns(Try.failure(Exception("Error thrown")))

        val result = handler.process(Request(FIRST, ALL_MONEY)) as Failure

        assertEquals(500, result.code)
        assertEquals("Error thrown", result.reason)
    }
}