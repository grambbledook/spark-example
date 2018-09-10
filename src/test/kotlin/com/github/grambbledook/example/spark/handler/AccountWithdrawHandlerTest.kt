package com.github.grambbledook.example.spark.handler

import com.github.grambbledook.example.spark.dto.Account
import com.github.grambbledook.example.spark.dto.Failure
import com.github.grambbledook.example.spark.dto.Success
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.ALL_MONEY
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.FIRST
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.NO_MONEY
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.ZERO_AMOUNT
import com.github.grambbledook.example.spark.service.AccountNotEnoughMoneyError
import com.github.grambbledook.example.spark.service.AccountService
import io.mockk.every
import io.mockk.mockk
import io.vavr.control.Try
import org.junit.Assert.assertEquals
import org.junit.Test

internal typealias WithdrawRequest = AccountWithdrawHandler.AccountWithdrawRequest

class AccountWithdrawHandlerTest : HandlerFixture {

    private val service = mockk<AccountService>()
    private val handler = AccountWithdrawHandler(service, mockk())

    @Test
    fun testWithdrawResultsInSuccessResult() {
        every {
            service.withdraw(FIRST, ALL_MONEY)
        }.returns(
                Try.success(Account(FIRST, ZERO_AMOUNT, "John doe"))
        )

        val result = handler.process(WithdrawRequest(FIRST, ALL_MONEY)) as Success<Account>

        assertEquals(FIRST, result.payload.id)
        assertEquals(ZERO_AMOUNT, result.payload.amount, 1e-2)
    }

    @Test
    fun testWithdrawNoMoneyCode() {
        every {
            service.withdraw(FIRST, ALL_MONEY)
        }.returns(
                Try.failure(AccountNotEnoughMoneyError(NO_MONEY))
        )

        val result = handler.process(WithdrawRequest(FIRST, ALL_MONEY)) as Failure

        assertEquals(402, result.code)
        assertEquals(NO_MONEY, result.reason)
    }

}