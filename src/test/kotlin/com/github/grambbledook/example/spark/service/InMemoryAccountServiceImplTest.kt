package com.github.grambbledook.example.spark.service

import com.github.grambbledook.example.spark.lock.AccountRWLockKotlin
import com.github.grambbledook.example.spark.repository.InMemoryAccountRepository

import com.github.grambbledook.example.spark.service.AccountFixture.Companion.FIRST
import com.github.grambbledook.example.spark.service.AccountFixture.Companion.SECOND
import com.github.grambbledook.example.spark.service.AccountFixture.Companion.UNKNOWN
import org.junit.Assert
import org.junit.Test

class InMemoryAccountServiceImplTest : AccountFixture {

    override val service = InMemoryAccountServiceImpl(InMemoryAccountRepository(), AccountRWLockKotlin())

    @Test
    fun testGetAccountInfoSucceeded() {
        val account = service.getInfo(FIRST)
        Assert.assertEquals(100.00, account.get().amount, 1e-2)
    }

    @Test
    fun testGetNotExistingAccountInfoResultsInError() {
        val account = service.getInfo(UNKNOWN)
        Assert.assertTrue(account.isFailure)
        Assert.assertTrue(account.cause is AccountError)
        Assert.assertEquals("Account [$UNKNOWN] not found", account.cause.message)
    }

    @Test
    fun testAccountDepositSucceeded() {
        val before = service.getInfo(SECOND)
        Assert.assertEquals(0.00, before.get().amount, 1e-2)

        val result = service.deposit(SECOND, 100.00)
        Assert.assertEquals(100.00, result.get().amount, 1e-2)

        val after = service.getInfo(SECOND)
        Assert.assertEquals(100.00, after.get().amount, 1e-2)
    }

    @Test
    fun testWithdrawSucceeded() {
        val before = service.getInfo(FIRST)
        Assert.assertEquals(100.00, before.get().amount, 1e-2)

        val result = service.withdraw(FIRST, 100.00)
        Assert.assertEquals(0.00, result.get().amount, 1e-2)

        val after = service.getInfo(FIRST)
        Assert.assertEquals(0.00, after.get().amount, 1e-2)
    }

    @Test
    fun testWithdrawFailsOnNoMoney() {
        val before = service.getInfo(FIRST)
        Assert.assertEquals(100.00, before.get().amount, 1e-2)

        val result = service.withdraw(FIRST, 1000.00)
        Assert.assertEquals("Not enough money on account [$FIRST]", result.cause.message)

        val after = service.getInfo(FIRST)
        Assert.assertEquals(100.00, after.get().amount, 1e-2)
    }

    @Test
    fun testTransferSuccessful() {
        val firstBefore = service.getInfo(FIRST)
        Assert.assertEquals(100.00, firstBefore.get().amount, 1e-2)

        val secondBefore = service.getInfo(SECOND)
        Assert.assertEquals(0.00, secondBefore.get().amount, 1e-2)

        val result = service.transfer(FIRST, SECOND, 50.00)
        Assert.assertEquals(50.00, result.get().amount, 1e-2)

        val firstAfter = service.getInfo(FIRST)
        Assert.assertEquals(50.00, firstAfter.get().amount, 1e-2)

        val secondAfter = service.getInfo(SECOND)
        Assert.assertEquals(50.00, secondAfter.get().amount, 1e-2)
    }

    @Test
    fun testTransferFailsOnNoMoney() {
        val firstBefore = service.getInfo(FIRST)
        Assert.assertEquals(100.00, firstBefore.get().amount, 1e-2)

        val secondBefore = service.getInfo(SECOND)
        Assert.assertEquals(0.00, secondBefore.get().amount, 1e-2)

        val result = service.transfer(FIRST, SECOND, 1000.00)
        Assert.assertEquals("Not enough money on account [$FIRST]", result.cause.message)

        val firstAfter = service.getInfo(FIRST)
        Assert.assertEquals(100.00, firstAfter.get().amount, 1e-2)

        val secondAfter = service.getInfo(SECOND)
        Assert.assertEquals(0.00, secondAfter.get().amount, 1e-2)
    }

}