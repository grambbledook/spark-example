package com.github.grambbledook.example.spark.handler

import com.github.grambbledook.example.spark.dto.error.AccountCode
import com.github.grambbledook.example.spark.dto.error.AccountCode.*
import com.github.grambbledook.example.spark.dto.response.TransactionType.WITHDRAWAL
import com.github.grambbledook.example.spark.ext.*
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.ONE
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.THOUSAND
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.ZERO
import com.github.grambbledook.example.spark.fixture.RestFixture
import com.github.grambbledook.example.spark.fixture.UserFixture
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test


@Tag("INTEGRATION")
class AccountWithdrawHandlerTest : UserFixture, RestFixture {

    @Test
    fun `Test available money amount is successfully withdrawn from account`() {
        val accountId = createAccount(UserFixture.johnDoe, THOUSAND).accountId()

        val response = withdraw(accountId, THOUSAND)
        assertEquals(WITHDRAWAL, response.operation())

        assertEquals(accountId, response.accountId())
        assertEquals(THOUSAND, response.transactionAmount())
        assertEquals(ZERO, response.balance())

        val info = getAccountInfo(accountId)
        assertEquals(ZERO, info.balance())
    }

    @Test
    fun `Test negative amount cannot be withdrawn from account`() {
        val accountId = createAccount(UserFixture.johnDoe, THOUSAND).accountId()

        val response = withdraw(accountId, -THOUSAND).left()
        assertEquals(INVALID_AMOUNT, AccountCode.valueOf(response.code))

        val info = getAccountInfo(accountId)
        assertEquals(THOUSAND, info.balance())
    }

    @Test
    fun `Test amount greater than available money cannot be withdrawn from account`() {
        val accountId = createAccount(UserFixture.johnDoe, ONE).accountId()

        val response = withdraw(accountId, THOUSAND).left()
        assertEquals(INSUFFICIENT_FUNDS, AccountCode.valueOf(response.code))

        val info = getAccountInfo(accountId)
        assertEquals(ONE, info.balance())
    }


    @Test
    fun `Test money cannot be withdrawn from not existing account`() {
        val response = withdraw(-1, THOUSAND).left()
        assertEquals(ACCOUNT_NOT_FOUND, AccountCode.valueOf(response.code))
    }

}