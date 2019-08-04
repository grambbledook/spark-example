package com.github.grambbledook.example.spark.handler

import com.github.grambbledook.example.spark.dto.error.AccountCode
import com.github.grambbledook.example.spark.dto.error.AccountCode.INVALID_AMOUNT
import com.github.grambbledook.example.spark.dto.response.TransactionType
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
class CreateAccountHandlerTest : UserFixture, AmountFixture, RestFixture {

    @Test
    fun `Test account is successfully created for valid input parameters`() {
        val response = createAccount(johnDoe, THOUSAND)

        assertEquals(TransactionType.CREATED, response.operation())
        assertEquals(THOUSAND, response.balance())
    }

    @Test
    fun `Test account with negative available amount cannot be created`() {
        val error = createAccount(johnDoe, -THOUSAND).left()

        assertEquals(INVALID_AMOUNT, AccountCode.valueOf(error.code))
    }

}