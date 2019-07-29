package com.github.grambbledook.example.spark.handler

import arrow.core.Left
import arrow.core.Right
import com.github.grambbledook.example.spark.domain.Account
import com.github.grambbledook.example.spark.domain.WorkflowFailure
import com.github.grambbledook.example.spark.domain.Success
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.FIRST
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.SECOND
import com.github.grambbledook.example.spark.domain.AccountError
import com.github.grambbledook.example.spark.service.AccountService
import com.github.grambbledook.example.spark.domain.error.BusinessCode
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

@Suppress("UNCHECKED_CAST")
class GetAccountInfoHandlerTest : HandlerFixture {

    private val service = mockk<AccountService>()
    private val handler = GetAccountInfoHandler(service, mockk())

    @Test
    fun testExistingAccountInfoSuccessResult() {
        val account = Account(FIRST, BigDecimal(1000.00), "John doe")

        every { service.getInfo(FIRST) }.returns(Right(account))

        val result = handler.process(FIRST) as Success<Account>
        assertEquals(account, result.payload)
    }

    @Test
    fun testAccountDoesNotExist() {
        every { service.getInfo(SECOND) }.returns(Left(AccountError(BusinessCode.ACCOUNT_NOT_FOUND)))

        val result = handler.process(SECOND) as WorkflowFailure

        assertEquals(BusinessCode.ACCOUNT_NOT_FOUND, result.code)
        assertEquals(null, result.message)
    }

}