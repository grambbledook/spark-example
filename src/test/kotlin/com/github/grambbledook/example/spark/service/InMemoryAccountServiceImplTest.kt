package com.github.grambbledook.example.spark.service

import com.github.grambbledook.example.spark.dto.AccountError
import com.github.grambbledook.example.spark.dto.error.AccountCode.ACCOUNT_NOT_FOUND
import com.github.grambbledook.example.spark.dto.error.AccountCode.INSUFFICIENT_FUNDS
import com.github.grambbledook.example.spark.ext.left
import com.github.grambbledook.example.spark.ext.right
import com.github.grambbledook.example.spark.lock.AccountRWLock
import com.github.grambbledook.example.spark.repository.InMemoryAccountRepository
import com.github.grambbledook.example.spark.service.AccountFixture.Companion.FIRST
import com.github.grambbledook.example.spark.service.AccountFixture.Companion.SECOND
import com.github.grambbledook.example.spark.service.AccountFixture.Companion.UNKNOWN
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicLong

class InMemoryAccountServiceImplTest : AccountFixture {

    override var service = InMemoryAccountServiceImpl(AtomicLong(FIRST), InMemoryAccountRepository(), AccountRWLock())

    @Test
    fun testGetAccountInfoSucceeded() {
        val account = service.getInfo(FIRST)
        Assertions.assertEquals(100.00, account.right().amount.toDouble(), 1e-2)
    }

    @Test
    fun testGetNotExistingAccountInfoResultsInError() {
        val account = service.getInfo(UNKNOWN)
        Assertions.assertEquals(ACCOUNT_NOT_FOUND, (account.left() as AccountServiceError).error)
    }

    @Test
    fun testAccountDepositSucceeded() {
        val before = service.getInfo(SECOND)
        Assertions.assertEquals(0.00, before.right().amount.toDouble(), 1e-2)

        val result = service.deposit(SECOND, BigDecimal(100.00))
        Assertions.assertEquals(100.00, result.right().amount.toDouble(), 1e-2)

        val after = service.getInfo(SECOND)
        Assertions.assertEquals(100.00, after.right().amount.toDouble(), 1e-2)
    }

    @Test
    fun testWithdrawSucceeded() {
        val before = service.getInfo(FIRST)
        Assertions.assertEquals(100.00, before.right().amount.toDouble(), 1e-2)

        val result = service.withdraw(FIRST, BigDecimal(100.00))
        Assertions.assertEquals(0.00, result.right().amount.toDouble(), 1e-2)

        val after = service.getInfo(FIRST)
        Assertions.assertEquals(0.00, after.right().amount.toDouble(), 1e-2)
    }

    @Test
    fun testWithdrawFailsOnNoMoney() {
        val before = service.getInfo(FIRST)
        Assertions.assertEquals(100.00, before.right().amount.toDouble(), 1e-2)

        val result = service.withdraw(FIRST, BigDecimal(1000.00))
        Assertions.assertEquals(INSUFFICIENT_FUNDS, (result.left() as AccountServiceError).error)

        val after = service.getInfo(FIRST)
        Assertions.assertEquals(100.00, after.right().amount.toDouble(), 1e-2)
    }

    @Test
    fun testTransferSuccessful() {
        val firstBefore = service.getInfo(FIRST)
        Assertions.assertEquals(100.00, firstBefore.right().amount.toDouble(), 1e-2)

        val secondBefore = service.getInfo(SECOND)
        Assertions.assertEquals(0.00, secondBefore.right().amount.toDouble(), 1e-2)

        val result = service.transfer(FIRST, SECOND, BigDecimal(60.00))
        Assertions.assertEquals(40.00, result.right().amount.toDouble(), 1e-2)

        val firstAfter = service.getInfo(FIRST)
        Assertions.assertEquals(40.00, firstAfter.right().amount.toDouble(), 1e-2)

        val secondAfter = service.getInfo(SECOND)
        Assertions.assertEquals(60.00, secondAfter.right().amount.toDouble(), 1e-2)
    }

    @Test
    fun testTransferFailsOnNoMoney() {
        val firstBefore = service.getInfo(FIRST)
        Assertions.assertEquals(100.00, firstBefore.right().amount.toDouble(), 1e-2)

        val secondBefore = service.getInfo(SECOND)
        Assertions.assertEquals(0.00, secondBefore.right().amount.toDouble(), 1e-2)

        val result = service.transfer(FIRST, SECOND, BigDecimal(1000.00))
        Assertions.assertEquals(INSUFFICIENT_FUNDS, (result.left() as AccountServiceError).error)

        val firstAfter = service.getInfo(FIRST)
        Assertions.assertEquals(100.00, firstAfter.right().amount.toDouble(), 1e-2)

        val secondAfter = service.getInfo(SECOND)
        Assertions.assertEquals(0.00, secondAfter.right().amount.toDouble(), 1e-2)
    }

}