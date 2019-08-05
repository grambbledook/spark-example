package com.github.grambbledook.example.spark.service

import com.github.grambbledook.example.spark.dto.domain.Account
import com.github.grambbledook.example.spark.fixture.AmountFixture
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.HUNDRED
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.SIXTY
import com.github.grambbledook.example.spark.fixture.AmountFixture.Companion.ZERO
import com.github.grambbledook.example.spark.fixture.LockFixture
import com.github.grambbledook.example.spark.fixture.UserFixture
import com.github.grambbledook.example.spark.fixture.UserFixture.Companion.johnDoe
import com.github.grambbledook.example.spark.lock.AccountRWLockImpl
import com.github.grambbledook.example.spark.repository.InMemoryAccountRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong


class InMemoryAccountServiceDeadlockDetectionTest : AmountFixture, UserFixture, LockFixture {

    private val first: Account = Account(1, HUNDRED, johnDoe)
    private val second: Account = Account(2, ZERO, johnDoe)

    private val accountMap = mapOf(first.id to first, second.id to second)

    @Test
    fun `Test deadlock avoided on concurrent transfer from acc A to acc B and from acc B to acc A`() {
        val barrier = CyclicBarrier(2)
        val testCompletionLatch = CountDownLatch(2)

        val lock = object : AccountRWLockImpl() {
            override fun <T> lockWrite(id: Long, executeCriticalSection: () -> T): T {
                val name = Thread.currentThread().name

                if (id == second.id && name == FIRST_THREAD) {
                    barrier.await()
                    testCompletionLatch.countDown()
                }

                if (id == first.id && name == FIRST_THREAD) {
                    barrier.await()
                }

                if (id == first.id && name == SECOND_THREAD) {
                    barrier.await()
                    testCompletionLatch.countDown()
                }

                if (id == second.id && name == SECOND_THREAD) {
                    barrier.await()
                }

                return super.lockWrite(id, executeCriticalSection)
            }
        }

        val service = InMemoryAccountServiceImpl(AtomicLong(0), InMemoryAccountRepository(accountMap), lock)

        val t1 = Thread({
            service.transfer(first.id, second.id, SIXTY)
        }, FIRST_THREAD)

        val t2 = Thread({
            service.transfer(second.id, first.id, SIXTY)
        }, SECOND_THREAD)

        t1.start()
        t2.start()

        val deadlocked = testCompletionLatch.await(5, TimeUnit.SECONDS)
        Assertions.assertFalse(deadlocked)
    }

    companion object {
        const val FIRST_THREAD = "FIRST"
        const val SECOND_THREAD = "SECOND"
    }

}