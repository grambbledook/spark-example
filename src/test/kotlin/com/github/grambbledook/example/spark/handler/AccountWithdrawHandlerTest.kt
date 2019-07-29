package com.github.grambbledook.example.spark.handler

import arrow.core.Left
import arrow.core.Right
import com.github.grambbledook.example.spark.domain.Account
import com.github.grambbledook.example.spark.domain.WorkflowFailure
import com.github.grambbledook.example.spark.domain.Success
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.FIRST
import com.github.grambbledook.example.spark.domain.AccountError
import com.github.grambbledook.example.spark.service.AccountService
import com.github.grambbledook.example.spark.domain.error.BusinessCode
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

internal typealias WithdrawRequest = AccountWithdrawHandler.AccountWithdrawRequest

@Suppress("UNCHECKED_CAST")
class AccountWithdrawHandlerTest : HandlerFixture {

    private val service = mockk<AccountService>()
    private val handler = AccountWithdrawHandler(service, mockk())

    @Test
    fun testWithdrawResultsInSuccessResult() {
        every {
            service.withdraw(FIRST, BigDecimal(1000.00))
        }.returns(
                Right(Account(FIRST, BigDecimal.ZERO, "John doe"))
        )

        val result = handler.process(WithdrawRequest(FIRST, BigDecimal(1000.00))) as Success<Account>

        assertEquals(FIRST, result.payload.id)
        assertEquals(BigDecimal.ZERO, result.payload.amount)
    }

    @Test
    fun testWithdrawNoMoneyCode() {
        every {
            service.withdraw(FIRST, BigDecimal(1000.00))
        }.returns(
               Left(AccountError(BusinessCode.INSUFFICIENT_FUNDS))
        )

        val result = handler.process(WithdrawRequest(FIRST, BigDecimal(1000.00))) as WorkflowFailure

        assertEquals(BusinessCode.INSUFFICIENT_FUNDS, result.code)
        assertEquals(null, result.message)
    }

}