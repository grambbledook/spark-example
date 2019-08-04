package com.github.grambbledook.example.spark.handler

import com.github.grambbledook.example.spark.dto.error.AccountCode
import com.github.grambbledook.example.spark.dto.error.AccountCode.*
import com.github.grambbledook.example.spark.dto.response.TransactionType.TRANSFER
import com.github.grambbledook.example.spark.ext.*
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.ONE
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.THOUSAND
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.ZERO
import com.github.grambbledook.example.spark.fixture.RestFixture
import com.github.grambbledook.example.spark.fixture.UserFixture
import com.github.grambbledook.example.spark.fixture.UserFixture.Companion.johnDoe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test


@Tag("INTEGRATION")
class AccountTransferHandlerTest : UserFixture, RestFixture {

    @Test
    fun `Test available amount successfully transferred to another account`() {
        val sourceId = createAccount(johnDoe, THOUSAND).accountId()
        val destinationId = createAccount(johnDoe, ZERO).accountId()

        val response = transfer(sourceId, destinationId, THOUSAND)
        assertEquals(TRANSFER, response.operation())
        assertEquals(sourceId, response.accountId())
        assertEquals(THOUSAND, response.transactionAmount())
        assertEquals(ZERO, response.balance())

        val sourceAccount = getAccountInfo(sourceId)
        assertEquals(ZERO, sourceAccount.balance())

        val destinationAccount = getAccountInfo(destinationId)
        assertEquals(THOUSAND, destinationAccount.balance())
    }

    @Test
    fun `Test amount greater than available money cannot be transferred to another account`() {
        val sourceId = createAccount(johnDoe, ONE).accountId()
        val destinationId = createAccount(johnDoe, ZERO).accountId()

        val response = transfer(sourceId, destinationId, THOUSAND).left()
        assertEquals(INSUFFICIENT_FUNDS, AccountCode.valueOf(response.code))

        val sourceAccount = getAccountInfo(sourceId)
        assertEquals(ONE, sourceAccount.balance())

        val destinationAccount = getAccountInfo(destinationId)
        assertEquals(ZERO, destinationAccount.balance())
    }

    @Test
    fun `Test negative amount cannot be transferred to another account`() {
        val sourceId = createAccount(johnDoe, ONE).accountId()
        val destinationId = createAccount(johnDoe, ZERO).accountId()

        val response = transfer(sourceId, destinationId, -ONE).left()
        assertEquals(INVALID_AMOUNT, AccountCode.valueOf(response.code))

        val sourceAccount = getAccountInfo(sourceId)
        assertEquals(ONE, sourceAccount.balance())

        val destinationAccount = getAccountInfo(destinationId)
        assertEquals(ZERO, destinationAccount.balance())
    }

    @Test
    fun `Test money cannot be transferred from not existing account`() {
        val destinationId = createAccount(johnDoe, ZERO).accountId()

        val response = transfer(-1, destinationId, THOUSAND).left()
        assertEquals(ACCOUNT_NOT_FOUND, AccountCode.valueOf(response.code))

        val destinationAccount = getAccountInfo(destinationId)
        assertEquals(ZERO, destinationAccount.balance())
    }

    @Test
    fun `Test money cannot be transferred to not existing account`() {
        val sourceId = createAccount(johnDoe, THOUSAND).accountId()

        val response = transfer(sourceId, -1, THOUSAND).left()
        assertEquals(ACCOUNT_NOT_FOUND, AccountCode.valueOf(response.code))

        val sourceAccount = getAccountInfo(sourceId)
        assertEquals(THOUSAND, sourceAccount.balance())
    }

}