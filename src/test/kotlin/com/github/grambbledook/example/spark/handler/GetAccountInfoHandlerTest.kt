package com.github.grambbledook.example.spark.handler

import com.github.grambbledook.example.spark.dto.error.AccountCode
import com.github.grambbledook.example.spark.dto.error.AccountCode.ACCOUNT_NOT_FOUND
import com.github.grambbledook.example.spark.dto.response.TransactionType.INFO
import com.github.grambbledook.example.spark.ext.accountId
import com.github.grambbledook.example.spark.ext.balance
import com.github.grambbledook.example.spark.ext.left
import com.github.grambbledook.example.spark.ext.operation
import com.github.grambbledook.example.spark.fixture.AmountFixture
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.THOUSAND
import com.github.grambbledook.example.spark.fixture.RestFixture
import com.github.grambbledook.example.spark.fixture.UserFixture
import com.github.grambbledook.example.spark.fixture.UserFixture.Companion.johnDoe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test


@Tag("INTEGRATION")
class GetAccountInfoHandlerTest : UserFixture, AmountFixture, RestFixture {

    @Test
    fun `Test account is successfully created for valid input parameters`() {
        val accountId = createAccount(johnDoe, THOUSAND).accountId()

        val response = getAccountInfo(accountId)
        assertEquals(INFO, response.operation())
        assertEquals(THOUSAND, response.balance())
    }

    @Test
    fun `Test an error response returned if account does not exist`() {
        val response = getAccountInfo(-1).left()
        assertEquals(ACCOUNT_NOT_FOUND, AccountCode.valueOf(response.code))
    }

}