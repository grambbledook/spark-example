package com.github.grambbledook.example.spark.handler

import com.github.grambbledook.example.spark.dto.Account
import com.github.grambbledook.example.spark.dto.Failure
import com.github.grambbledook.example.spark.dto.Success
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.ALL_MONEY
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.FIRST
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.NOT_FOUND
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.NO_MONEY
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.SECOND
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.ZERO_AMOUNT
import com.github.grambbledook.example.spark.service.AccountNotEnoughMoneyError
import com.github.grambbledook.example.spark.service.AccountNotFoundError
import com.github.grambbledook.example.spark.service.AccountService
import io.mockk.every
import io.mockk.mockk
import io.vavr.control.Try
import org.junit.Assert.assertEquals
import org.junit.Test

internal typealias TransferRequest = AccountTransferHandler.AccountTransferRequest

class AccountTransferHandlerTest : HandlerFixture {

    private val service = mockk<AccountService>()
    private val handler = AccountTransferHandler(service, mockk())

    @Test
    fun testTransferResultsInSuccessResult() {
        every {
            service.transfer(FIRST, SECOND, ALL_MONEY)
        }.returns(
                Try.success(Account(FIRST, ZERO_AMOUNT, "John doe"))
        )

        val result = handler.process(TransferRequest(FIRST, SECOND, ALL_MONEY)) as Success<Account>

        assertEquals(FIRST, result.payload.id)
        assertEquals(ZERO_AMOUNT, result.payload.amount, 1e-2)
    }

    @Test
    fun testTransferNoMoneyCode() {
        every {
            service.transfer(FIRST, SECOND, ALL_MONEY)
        }.returns(
                Try.failure(AccountNotEnoughMoneyError(NO_MONEY))
        )

        val result = handler.process(TransferRequest(FIRST, SECOND, ALL_MONEY)) as Failure

        assertEquals(402, result.code)
        assertEquals(NO_MONEY, result.reason)
    }

    @Test
    fun testAccountNotFound() {
        every {
            service.transfer(FIRST, SECOND, ALL_MONEY)
        }.returns(
                Try.failure(AccountNotFoundError(NOT_FOUND))
        )

        val result = handler.process(TransferRequest(FIRST, SECOND, ALL_MONEY)) as Failure

        assertEquals(404, result.code)
        assertEquals(NOT_FOUND, result.reason)
    }

}