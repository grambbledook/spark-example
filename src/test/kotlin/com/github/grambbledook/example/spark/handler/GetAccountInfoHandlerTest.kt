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


class GetAccountInfoHandlerTest {

    private val service = mockk<AccountService>()
    private val handler = GetAccountInfoHandler(service, mockk())

    @Test
    fun testExistingAccountInfoSuccessResult() {
        val account = Account(100, 1000.00, "John doe")

        every {
            service.getInfo(100)
        }.returns(
                Try.success(account)
        )

        val result = handler.process(100) as Success<Account>
        assertEquals(account, result.payload)
    }

    @Test
    fun testAccountDoesNotExist() {
        every {
            service.getInfo(200)
        }.returns(
                Try.failure(AccountError("Account not found"))
        )

        val result = handler.process(200) as Failure

        assertEquals(404, result.code)
        assertEquals("Account not found", result.reason)
    }

}