package com.github.grambbledook.example.spark.handler

import com.github.grambbledook.example.spark.dto.Account
import com.github.grambbledook.example.spark.dto.Failure
import com.github.grambbledook.example.spark.dto.Success
import com.github.grambbledook.example.spark.service.AccountError
import com.github.grambbledook.example.spark.service.AccountService

import io.mockk.every
import io.mockk.mockk
import io.vavr.control.Try
import org.junit.Assert.assertEquals
import org.junit.Test

internal typealias WithdrawRequest = AccountWithdrawHandler.AccountWithdrawRequest

class AccountWithdrawHandlerTest {

    private val service = mockk<AccountService>()
    private val handler = AccountWithdrawHandler(service, mockk())

    @Test
    fun testDepositResultsInSuccessResult() {
        every {
            service.deposit(100, 1000.00)
        }.returns(
                Try.success(Account(100, 0.00, "John doe"))
        )

        val result = handler.process(WithdrawRequest(100, 1000.00)) as Success<Account>

        assertEquals(100, result.payload.id)
        assertEquals(0.00, result.payload.amount, 1e-2)
    }

    @Test
    fun testWithdrawNoMoneyCode400() {
        every {
            service.deposit(100, 1000.00)
        }.returns(
                Try.failure(AccountError("No money"))
        )

        val result = handler.process(WithdrawRequest(100, 1000.00)) as Failure

        assertEquals(400, result.code)
        assertEquals("No money", result.reason)
    }

}