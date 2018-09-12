package com.github.grambbledook.example.spark.handler

import com.github.grambbledook.example.spark.dto.Account
import com.github.grambbledook.example.spark.dto.Failure
import com.github.grambbledook.example.spark.dto.Success
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.FIRST
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.NOT_FOUND
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.NO_MONEY
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.SECOND
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.THOUSAND_UNITS
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.ZERO_UNITS
import com.github.grambbledook.example.spark.service.AccountError
import com.github.grambbledook.example.spark.service.AccountService
import com.github.grambbledook.example.spark.dto.BusinessCode
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
            service.transfer(FIRST, SECOND, THOUSAND_UNITS)
        }.returns(
                Try.success(Account(FIRST, ZERO_UNITS, "John doe"))
        )

        val result = handler.process(TransferRequest(FIRST, SECOND, THOUSAND_UNITS)) as Success<Account>

        assertEquals(FIRST, result.payload.id)
        assertEquals(ZERO_UNITS, result.payload.amount, 1e-2)
    }

    @Test
    fun testTransferNoMoneyCode() {
        every {
            service.transfer(FIRST, SECOND, THOUSAND_UNITS)
        }.returns(
                Try.failure(AccountError(NO_MONEY, BusinessCode.INSUFFICIENT_FUNDS))
        )

        val result = handler.process(TransferRequest(FIRST, SECOND, THOUSAND_UNITS)) as Failure

        assertEquals(BusinessCode.INSUFFICIENT_FUNDS, result.businessCode)
        assertEquals(NO_MONEY, result.reason)
    }

    @Test
    fun testAccountNotFound() {
        every {
            service.transfer(FIRST, SECOND, THOUSAND_UNITS)
        }.returns(
                Try.failure(AccountError(NOT_FOUND, BusinessCode.ACCOUNT_NOT_FOUND))
        )

        val result = handler.process(TransferRequest(FIRST, SECOND, THOUSAND_UNITS)) as Failure

        assertEquals(BusinessCode.ACCOUNT_NOT_FOUND, result.businessCode)
        assertEquals(NOT_FOUND, result.reason)
    }

}