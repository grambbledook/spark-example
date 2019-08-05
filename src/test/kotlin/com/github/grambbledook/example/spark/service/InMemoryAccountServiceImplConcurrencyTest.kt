package com.github.grambbledook.example.spark.service

import arrow.core.Either
import arrow.core.Option
import com.github.grambbledook.example.spark.dto.domain.Account
import com.github.grambbledook.example.spark.dto.error.AccountCode
import com.github.grambbledook.example.spark.dto.error.ServiceError
import com.github.grambbledook.example.spark.ext.left
import com.github.grambbledook.example.spark.ext.right
import com.github.grambbledook.example.spark.fixture.AmountFixture
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.FORTY
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.HUNDRED
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.SIXTY
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.ZERO
import com.github.grambbledook.example.spark.fixture.LockFixture
import com.github.grambbledook.example.spark.fixture.UserFixture
import com.github.grambbledook.example.spark.fixture.UserFixture.Companion.johnDoe
import com.github.grambbledook.example.spark.lock.AccountRWLock
import com.github.grambbledook.example.spark.repository.InMemoryAccountRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

typealias Result = Pair<Either<ServiceError, Account>, Either<ServiceError, Account>>

class InMemoryAccountServiceImplConcurrencyTest : AmountFixture, UserFixture, LockFixture {

    private val first: Account = Account(1, HUNDRED, johnDoe)
    private val second: Account = Account(2, ZERO, johnDoe)

    private val accountMap = mapOf(first.id to first, second.id to second)

    @Test
    fun `Test two concurrent transfers from acc A to acc B and from acc B to acc A succeed if enough money available`() {
        val (firstOperation, secondOperation) = doTest(
                { service -> service.transfer(first.id, second.id, SIXTY) },
                { service -> service.transfer(second.id, first.id, SIXTY) }
        )

        Assertions.assertEquals(FORTY, firstOperation.right().balance)
        Assertions.assertEquals(ZERO, secondOperation.right().balance)
    }

    @Test
    fun `Test only one concurrent transfer from acc B to acc A and from acc A to acc B succeed if not enough money on one of accounts`() {
        val (firstOperation, secondOperation) = doTest(
                { service -> service.transfer(second.id, first.id, SIXTY) },
                { service -> service.transfer(first.id, second.id, SIXTY) }
        )

        Assertions.assertEquals(AccountCode.INSUFFICIENT_FUNDS, firstOperation.left().code)
        Assertions.assertEquals(FORTY, secondOperation.right().balance)
    }

    @Test
    fun `Test only one of two concurrent withdrawals succeeded`() {
        val (firstOperation, secondOperation) = doTest(
                { service -> service.withdraw(first.id, SIXTY) },
                { service -> service.withdraw(first.id, SIXTY) }
        )

        Assertions.assertEquals(FORTY, firstOperation.right().balance)
        Assertions.assertEquals(AccountCode.INSUFFICIENT_FUNDS, secondOperation.left().code)
    }

    @Test
    fun `Test two concurrent deposits happens consequently`() {
        val (firstOperation, secondOperation) = doTest(
                { service -> service.deposit(second.id, SIXTY) },
                { service -> service.deposit(second.id, FORTY) }
        )

        Assertions.assertEquals(SIXTY, firstOperation.right().balance)
        Assertions.assertEquals(HUNDRED, secondOperation.right().balance)
    }

    private fun doTest(op1: (AccountService) -> Either<ServiceError, Account>, op2: (AccountService) -> Either<ServiceError, Account>): Result {

        val latch = CountDownLatch(1)
        val testCompletionLatch = CountDownLatch(2)

        val repo = object : InMemoryAccountRepository(accountMap) {
            override fun findById(id: Long): Option<Account> {
                latch.countDown()
                timeConsumingPart()
                return super.findById(id)
            }
        }

        val service = InMemoryAccountServiceImpl(AtomicLong(0), repo, AccountRWLock())

        lateinit var firstOperation: Either<ServiceError, Account>
        val t1 = Thread {
            firstOperation = op1(service)
            testCompletionLatch.countDown()
        }

        lateinit var secondOperation: Either<ServiceError, Account>
        val t2 = Thread {
            latch.await()
            secondOperation = op2(service)
            testCompletionLatch.countDown()
        }

        t1.start()
        t2.start()

        Assertions.assertTrue(testCompletionLatch.await(30, TimeUnit.SECONDS))
        return Pair(firstOperation, secondOperation)
    }

}