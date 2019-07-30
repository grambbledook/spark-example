package com.github.grambbledook.example.spark.handler

import com.github.grambbledook.example.spark.dto.error.AccountCode
import com.github.grambbledook.example.spark.dto.error.AccountCode.ACCOUNT_NOT_FOUND
import com.github.grambbledook.example.spark.dto.error.AccountCode.INVALID_AMOUNT
import com.github.grambbledook.example.spark.dto.response.TransactionType.DEPOSIT
import com.github.grambbledook.example.spark.ext.accountId
import com.github.grambbledook.example.spark.ext.left
import com.github.grambbledook.example.spark.ext.right
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.THOUSAND
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.ZERO
import com.github.grambbledook.example.spark.fixture.RestFixture
import com.github.grambbledook.example.spark.fixture.UserFixture
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test


@Tag("INTEGRATION")
class AccountDepositHandlerTest : UserFixture, RestFixture {

    @Test
    fun `Test money successfully deposited to account`() {
        val accountId = createAccount(UserFixture.johnDoe, ZERO).accountId()

        val response = deposit(accountId, THOUSAND).right()
        assertEquals(DEPOSIT, response.operation)

        assertEquals(accountId, response.details.accountId)
        assertEquals(THOUSAND, response.details.amount)
        assertEquals(THOUSAND, response.details.available)

        val info = getAccountInfo(accountId).right()
        assertEquals(THOUSAND, info.details.available)
    }

    @Test
    fun `Test negative amount cannot be deposited to account`() {
        val accountId = createAccount(UserFixture.johnDoe, ZERO).accountId()

        val response = deposit(accountId, -THOUSAND).left()
        assertEquals(INVALID_AMOUNT, AccountCode.valueOf(response.code))

        val info = getAccountInfo(accountId).right()
        assertEquals(ZERO, info.details.available)
    }

    @Test
    fun `Test zero amount cannot be deposited to account`() {
        val accountId = createAccount(UserFixture.johnDoe, ZERO).accountId()

        val response = deposit(accountId, ZERO).left()
        assertEquals(INVALID_AMOUNT, AccountCode.valueOf(response.code))

        val info = getAccountInfo(accountId).right()
        assertEquals(ZERO, info.details.available)
    }

    @Test
    fun `Test money cannot be deposited to not existing account`() {
        val response = deposit(-1, THOUSAND).left()
        assertEquals(ACCOUNT_NOT_FOUND, AccountCode.valueOf(response.code))
    }

}