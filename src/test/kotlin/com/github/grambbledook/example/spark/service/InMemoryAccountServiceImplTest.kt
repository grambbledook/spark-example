package com.github.grambbledook.example.spark.service

import com.github.grambbledook.example.spark.dto.domain.Account
import com.github.grambbledook.example.spark.dto.error.AccountCode.ACCOUNT_NOT_FOUND
import com.github.grambbledook.example.spark.dto.error.AccountCode.INSUFFICIENT_FUNDS
import com.github.grambbledook.example.spark.ext.left
import com.github.grambbledook.example.spark.ext.right
import com.github.grambbledook.example.spark.fixture.AmountFixture
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.FORTY
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.HUNDRED
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.SIXTY
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.THOUSAND
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.ZERO
import com.github.grambbledook.example.spark.fixture.UserFixture
import com.github.grambbledook.example.spark.fixture.UserFixture.Companion.janeDoe
import com.github.grambbledook.example.spark.fixture.UserFixture.Companion.johnDoe
import com.github.grambbledook.example.spark.lock.AccountRWLock
import com.github.grambbledook.example.spark.repository.InMemoryAccountRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicLong

class InMemoryAccountServiceImplTest: AmountFixture, UserFixture {

    private var service = InMemoryAccountServiceImpl(AtomicLong(0), InMemoryAccountRepository(), AccountRWLock())

    private lateinit var first: Account
    private lateinit var second: Account

    @BeforeEach
    fun setup() {
        first = service.create(HUNDRED, johnDoe).right()
        second = service.create(ZERO, janeDoe).right()
    }

    @Test
    fun testGetAccountInfoSucceeded() {
        val account = service.getInfo(first.id)
        Assertions.assertEquals(HUNDRED, account.right().balance)
    }

    @Test
    fun testGetNotExistingAccountInfoResultsInError() {
        val account = service.getInfo(Long.MIN_VALUE)
        Assertions.assertEquals(ACCOUNT_NOT_FOUND, (account.left() as AccountServiceError).code)
    }

    @Test
    fun testAccountDepositSucceeded() {
        val before = service.getInfo(second.id)
        Assertions.assertEquals(ZERO, before.right().balance)

        val result = service.deposit(second.id, HUNDRED)
        Assertions.assertEquals(HUNDRED, result.right().balance)

        val after = service.getInfo(second.id)
        Assertions.assertEquals(HUNDRED, after.right().balance)
    }

    @Test
    fun testWithdrawSucceeded() {
        val before = service.getInfo(first.id)
        Assertions.assertEquals(HUNDRED, before.right().balance)

        val result = service.withdraw(first.id, HUNDRED)
        Assertions.assertEquals(ZERO, result.right().balance)

        val after = service.getInfo(first.id)
        Assertions.assertEquals(ZERO, after.right().balance)
    }


    @Test
    fun testWithdrawFailsOnNoMoney() {
        val before = service.getInfo(first.id)
        Assertions.assertEquals(HUNDRED, before.right().balance)

        val result = service.withdraw(first.id, THOUSAND)
        Assertions.assertEquals(INSUFFICIENT_FUNDS, (result.left() as AccountServiceError).code)

        val after = service.getInfo(first.id)
        Assertions.assertEquals(HUNDRED, after.right().balance)
    }



    @Test
    fun testTransferSuccessful() {
        val firstBefore = service.getInfo(first.id)
        Assertions.assertEquals(HUNDRED, firstBefore.right().balance)

        val secondBefore = service.getInfo(second.id)
        Assertions.assertEquals(ZERO, secondBefore.right().balance)

        val result = service.transfer(first.id, second.id, SIXTY)
        Assertions.assertEquals(FORTY, result.right().balance)

        val firstAfter = service.getInfo(first.id)
        Assertions.assertEquals(FORTY, firstAfter.right().balance)

        val secondAfter = service.getInfo(second.id)
        Assertions.assertEquals(SIXTY, secondAfter.right().balance)
    }

    @Test
    fun testTransferFailsOnNoMoney() {
        val firstBefore = service.getInfo(first.id)
        Assertions.assertEquals(HUNDRED, firstBefore.right().balance)

        val secondBefore = service.getInfo(second.id)
        Assertions.assertEquals(ZERO, secondBefore.right().balance)

        val result = service.transfer(first.id, second.id, THOUSAND)
        Assertions.assertEquals(INSUFFICIENT_FUNDS, (result.left() as AccountServiceError).code)

        val firstAfter = service.getInfo(first.id)
        Assertions.assertEquals(HUNDRED, firstAfter.right().balance)

        val secondAfter = service.getInfo(second.id)
        Assertions.assertEquals(ZERO, secondAfter.right().balance)
    }

}