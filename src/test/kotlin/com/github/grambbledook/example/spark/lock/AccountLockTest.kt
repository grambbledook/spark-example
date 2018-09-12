package com.github.grambbledook.example.spark.lock

import org.junit.Test
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier

val lock = AccountLock()

class AccountLockTest {

    @Test
    fun testSecondThreadIsBlockedUntilRelease() {
        val latch = CountDownLatch(1)
        val barrier = CyclicBarrier(2)

        val buffer = ArrayDeque<String>()

        val consumer = Consumer(buffer, latch).apply { start() }
        val producer = Producer(buffer, latch, barrier).apply { start() }

        barrier.await()
        assert(HELLO_WORLD == buffer.peek())

        producer.join()
        consumer.join()

        assert(buffer.isEmpty())
        assert(HELLO_WORLD == consumer.helloWorld)
    }

    class Producer(private val buffer: Queue<String>, private val latch: CountDownLatch, private val barrier: CyclicBarrier) : Thread() {
        override fun run() {
            lock.synchronized(ID) {
                println("Producer acquire")
                buffer.offer(HELLO_WORLD)

                latch.countDown()
                barrier.await()
                println("Producer release")
            }
        }
    }

    class Consumer(private val buffer: Queue<String>, private val latch: CountDownLatch) : Thread() {
        lateinit var helloWorld: String

        override fun run() {
            latch.await()

            lock.synchronized(ID) {
                println("Consumer acquire")
                helloWorld = buffer.poll()
                println("Consumer release")
            }
        }
    }

}

