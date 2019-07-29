package com.github.grambbledook.example.spark.handler

import arrow.core.Left
import arrow.core.Right
import com.github.grambbledook.example.spark.domain.Account
import com.github.grambbledook.example.spark.domain.WorkflowSuccess
import com.github.grambbledook.example.spark.domain.request.AccountDepositRequest
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.FIRST
import com.github.grambbledook.example.spark.service.AccountService
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal


internal typealias Request = AccountDepositRequest

@Suppress("UNCHECKED_CAST")
class AccountDepositHandlerTest : HandlerFixture {

    private val service = mockk<AccountService>()
    private val handler = AccountDepositHandler(service, mockk())

    @Test
    fun testDepositResultsInSuccessResult() {
        every {
            service.deposit(FIRST, BigDecimal(1000.00))
        }.returns(
                Right( Account(FIRST, BigDecimal(1200.00), "John doe") )
        )

        val result = handler.process(Request(FIRST, BigDecimal(1000.00))) as WorkflowSuccess<Account>

        assertEquals(FIRST, result.payload.id)
        assertEquals(BigDecimal(1200.00), result.payload.amount)
    }

    @Test
    fun testInternalErrorCausesCode500() {
        val internalError = Exception("Error thrown")
        every { service.deposit(any(), any()) }.returns(Left(UnknownError(internalError)))

        val result = handler.process(Request(FIRST, BigDecimal(1000.00))) as Error

        assertEquals(internalError, result.reason)
    }
}