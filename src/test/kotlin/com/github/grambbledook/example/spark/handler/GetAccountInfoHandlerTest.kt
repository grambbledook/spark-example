package com.github.grambbledook.example.spark.handler

import com.github.grambbledook.example.spark.fixture.RestFixture
import com.github.grambbledook.example.spark.fixture.SparkFixture
import com.github.grambbledook.example.spark.dto.domain.Account
import com.github.grambbledook.example.spark.dto.error.AccountCode
import com.github.grambbledook.example.spark.ext.left
import com.github.grambbledook.example.spark.ext.right
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@Suppress("UNCHECKED_CAST")
class GetAccountInfoHandlerTest : HandlerFixture, RestFixture, SparkFixture {

    @Test
    fun `Test account is successfully created for valid input parameters`(owner: String) {
        val account = createAccount(owner, BigDecimal(1000))
        Assertions.assertEquals(Account(1, BigDecimal(1000), "John Doe"), account.right())
    }

    @Test
    fun `Test INVALID_AMOUNT is returned when creating account with negative amount`() {
        val error = createAccount("John Doe", BigDecimal(-1000)).left()

        Assertions.assertEquals(AccountCode.INVALID_AMOUNT, AccountCode.valueOf(error.code))
    }

    @Test
    fun testAccountDoesNotExist() {
//        every { service.getInfo(SECOND) }.returns(Left(AccountServiceError(ErrorCode.ACCOUNT_NOT_FOUND)))
//
//        val result = handler.process(SECOND) as WorkflowFailure

//        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, result.code)
//        assertEquals(null, result.message)
    }

}