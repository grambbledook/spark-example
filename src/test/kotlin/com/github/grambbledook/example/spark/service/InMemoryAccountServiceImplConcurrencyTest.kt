package com.github.grambbledook.example.spark.service

import arrow.core.Either
import com.github.grambbledook.example.spark.dto.error.ServiceError
import com.github.grambbledook.example.spark.dto.domain.Account
import com.github.grambbledook.example.spark.dto.error.AccountCode
import com.github.grambbledook.example.spark.ext.left
import com.github.grambbledook.example.spark.ext.right
import com.github.grambbledook.example.spark.fixture.AmountFixture
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.FORTY
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.HUNDRED
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.SIXTY
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.ZERO
import com.github.grambbledook.example.spark.fixture.UserFixture
import com.github.grambbledook.example.spark.fixture.UserFixture.Companion.johnDoe
import com.github.grambbledook.example.spark.lock.AccountRWLock
import com.github.grambbledook.example.spark.repository.InMemoryAccountRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

typealias Result = Triple<Either<ServiceError, Account>, Either<ServiceError, Account>, Int>

class InMemoryAccountServiceImplConcurrencyTest : AmountFixture, UserFixture {

    private val first: Account = Account(1, HUNDRED, johnDoe)
    private val second: Account = Account(2, ZERO, johnDoe)

    private val accountMap = mapOf(first.id to first, second.id to second)

    @Test
    fun `Test two concurrent money transfers happens consequently`() {
        val (firstOperation, secondOperation, lastOperationIndex) = doTest(
                { service -> service.transfer(first.id, second.id, SIXTY) },
                { service -> service.transfer(second.id, first.id, SIXTY) }
        )

        when (lastOperationIndex) {
            FIRST_OPERATION -> {
                Assertions.assertEquals(FORTY, firstOperation.right().balance)
                Assertions.assertEquals(AccountCode.INSUFFICIENT_FUNDS, secondOperation.left().code)
            }
            SECOND_OPERATION -> {
                Assertions.assertEquals(FORTY, firstOperation.right().balance)
                Assertions.assertEquals(ZERO, secondOperation.right().balance)
            }
        }
    }

    @Test
    fun `Test two concurrent withdrawals happens consequently`() {
        val (firstOperation, secondOperation, lastIndex) = doTest(
                { service -> service.withdraw(first.id, SIXTY) },
                { service -> service.withdraw(first.id, SIXTY) }
        )

        when (lastIndex) {
            FIRST_OPERATION -> {
                Assertions.assertEquals(FORTY, secondOperation.right().balance)
                Assertions.assertEquals(AccountCode.INSUFFICIENT_FUNDS, firstOperation.left().code)
            }
            SECOND_OPERATION -> {
                Assertions.assertEquals(FORTY, firstOperation.right().balance)
                Assertions.assertEquals(AccountCode.INSUFFICIENT_FUNDS, secondOperation.left().code)
            }
        }
    }

    @Test
    fun `Test two concurrent deposits happens consequently`() {
        val (firstOperation, secondOperation, lastIndex) = doTest(
                { service -> service.deposit(second.id, SIXTY) },
                { service -> service.deposit(second.id, FORTY) }
        )

        when (lastIndex) {
            FIRST_OPERATION -> {
                Assertions.assertEquals(FORTY, secondOperation.right().balance)
                Assertions.assertEquals(HUNDRED, firstOperation.right().balance)
            }
            SECOND_OPERATION -> {
                Assertions.assertEquals(SIXTY, firstOperation.right().balance)
                Assertions.assertEquals(HUNDRED, secondOperation.right().balance)
            }
        }
    }


    private fun doTest(op1: (AccountService) -> Either<ServiceError, Account>, op2: (AccountService) -> Either<ServiceError, Account>): Result {
        val repo = object : InMemoryAccountRepository(accountMap) {
            override fun save(account: Account): Account {
                Thread.sleep(500)
                return super.save(account)
            }
        }

        val lock = AccountRWLock()
        val service = InMemoryAccountServiceImpl(AtomicLong(0), repo, lock)

        val startWorkerBarrier = CyclicBarrier(2)
        val testCompletionLatch = CountDownLatch(2)
        val lastWorkerId = AtomicInteger(0)

        lateinit var firstOperation: Either<ServiceError, Account>
        val t1 = Thread {
            startWorkerBarrier.await()
            firstOperation = op1(service)
            lastWorkerId.set(FIRST_OPERATION)
            testCompletionLatch.countDown()
        }

        lateinit var secondOperation: Either<ServiceError, Account>
        val t2 = Thread {
            startWorkerBarrier.await()
            secondOperation = op2(service)
            lastWorkerId.set(SECOND_OPERATION)
            testCompletionLatch.countDown()
        }

        t1.start()
        t2.start()

        Assertions.assertTrue(testCompletionLatch.await(30, TimeUnit.SECONDS))
        return Triple(firstOperation, secondOperation, lastWorkerId.get())
    }

    companion object {
        const val FIRST_OPERATION = 1
        const val SECOND_OPERATION = 2
    }

}