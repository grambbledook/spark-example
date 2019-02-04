package com.github.grambbledook.example.spark.handler

import arrow.core.Left
import arrow.core.Right
import com.github.grambbledook.example.spark.dto.Account
import com.github.grambbledook.example.spark.dto.Failure
import com.github.grambbledook.example.spark.dto.Success
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.FIRST
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.SECOND
import com.github.grambbledook.example.spark.service.AccountError
import com.github.grambbledook.example.spark.service.AccountService
import com.github.grambbledook.example.spark.dto.BusinessCode
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

internal typealias TransferRequest = AccountTransferHandler.AccountTransferRequest

@Suppress("UNCHECKED_CAST")
class AccountTransferHandlerTest : HandlerFixture {

    private val service = mockk<AccountService>()
    private val handler = AccountTransferHandler(service, mockk())

    @Test
    fun testTransferResultsInSuccessResult() {
        every {
            service.transfer(FIRST, SECOND, BigDecimal(1000.00))
        }.returns(
                Right(Account(FIRST, BigDecimal.ZERO, "John doe"))
        )

        val result = handler.process(TransferRequest(FIRST, SECOND, BigDecimal(1000.00))) as Success<Account>

        assertEquals(FIRST, result.payload.id)
        assertEquals(BigDecimal.ZERO, result.payload.amount)

    }

    @Test
    fun testTransferNoMoneyCode() {
        every {
            service.transfer(FIRST, SECOND, BigDecimal(1000.00))
        }.returns(
                Left(AccountError(BusinessCode.INSUFFICIENT_FUNDS))
        )

        val result = handler.process(TransferRequest(FIRST, SECOND, BigDecimal(1000.00))) as Failure

        assertEquals(BusinessCode.INSUFFICIENT_FUNDS, result.businessCode)
        assertEquals(null, result.reason)
    }

    @Test
    fun testAccountNotFound() {
        every {
            service.transfer(FIRST, SECOND, BigDecimal(1000.00))
        }.returns(
                Left(AccountError(BusinessCode.ACCOUNT_NOT_FOUND))
        )

        val result = handler.process(TransferRequest(FIRST, SECOND, BigDecimal(1000.00))) as Failure

        assertEquals(BusinessCode.ACCOUNT_NOT_FOUND, result.businessCode)
        assertEquals(null, result.reason)
    }

}