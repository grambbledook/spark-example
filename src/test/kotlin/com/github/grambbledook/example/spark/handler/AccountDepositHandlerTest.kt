package com.github.grambbledook.example.spark.handler

import com.github.grambbledook.example.spark.dto.error.AccountCode
import com.github.grambbledook.example.spark.dto.error.AccountCode.ACCOUNT_NOT_FOUND
import com.github.grambbledook.example.spark.dto.error.AccountCode.INVALID_AMOUNT
import com.github.grambbledook.example.spark.dto.response.TransactionType.DEPOSIT
import com.github.grambbledook.example.spark.ext.*
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

        val response = deposit(accountId, THOUSAND)
        assertEquals(DEPOSIT, response.operation())

        assertEquals(accountId, response.accountId())
        assertEquals(THOUSAND, response.transactionAmount())
        assertEquals(THOUSAND, response.balance())

        val info = getAccountInfo(accountId)
        assertEquals(THOUSAND, info.balance())
    }

    @Test
    fun `Test negative amount cannot be deposited to account`() {
        val accountId = createAccount(UserFixture.johnDoe, ZERO).accountId()

        val response = deposit(accountId, -THOUSAND).left()
        assertEquals(INVALID_AMOUNT, AccountCode.valueOf(response.code))

        val info = getAccountInfo(accountId)
        assertEquals(ZERO, info.balance())
    }

    fun `Test zero amount cannot be deposited to account`() {
        val accountId = createAccount(UserFixture.johnDoe, ZERO).accountId()

        val response = deposit(accountId, ZERO).left()
        assertEquals(INVALID_AMOUNT, AccountCode.valueOf(response.code))

        val info = getAccountInfo(accountId)
        assertEquals(ZERO, info.balance())
    }

    @Test
    fun `Test money cannot be deposited to not existing account`() {
        val response = deposit(-1, THOUSAND).left()
        assertEquals(ACCOUNT_NOT_FOUND, AccountCode.valueOf(response.code))
    }

}