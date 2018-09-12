package com.github.grambbledook.example.spark.handler

import com.github.grambbledook.example.spark.dto.Account
import com.github.grambbledook.example.spark.dto.Failure
import com.github.grambbledook.example.spark.dto.Success
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.FIRST
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.NOT_FOUND
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.SECOND
import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.THOUSAND_UNITS
import com.github.grambbledook.example.spark.service.AccountError
import com.github.grambbledook.example.spark.service.AccountService
import com.github.grambbledook.example.spark.dto.BusinessCode
import io.mockk.every
import io.mockk.mockk
import io.vavr.control.Try
import org.junit.Assert.assertEquals
import org.junit.Test

class GetAccountInfoHandlerTest : HandlerFixture {

    private val service = mockk<AccountService>()
    private val handler = GetAccountInfoHandler(service, mockk())

    @Test
    fun testExistingAccountInfoSuccessResult() {
        val account = Account(FIRST, THOUSAND_UNITS, "John doe")

        every {
            service.getInfo(FIRST)
        }.returns(
                Try.success(account)
        )

        val result = handler.process(FIRST) as Success<Account>
        assertEquals(account, result.payload)
    }

    @Test
    fun testAccountDoesNotExist() {
        every {
            service.getInfo(SECOND)
        }.returns(
                Try.failure(AccountError(NOT_FOUND, BusinessCode.ACCOUNT_NOT_FOUND))
        )

        val result = handler.process(SECOND) as Failure

        assertEquals(BusinessCode.ACCOUNT_NOT_FOUND, result.businessCode)
        assertEquals(NOT_FOUND, result.reason)
    }

}