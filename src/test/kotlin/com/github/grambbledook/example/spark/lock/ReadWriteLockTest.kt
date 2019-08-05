package com.github.grambbledook.example.spark.lock

import com.github.grambbledook.example.spark.fixture.HELLO_WORLD
import com.github.grambbledook.example.spark.fixture.LockFixture
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.atomic.AtomicInteger


class ReadWriteLockTest : LockFixture {

    @Test
    fun `Test Reader is blocked on active Writer`() {
        val latch = CountDownLatch(1)
        val testExecutionLatch = CountDownLatch(3)

        val readerBarrier = CyclicBarrier(2)
        val writerBarrier = CyclicBarrier(2)

        val buffer = ArrayDeque<String>()

        val firstReaderResult = AtomicInteger(-1)
        val r1 = Thread {
            locks().lockRead(resourceId()) {

                println("Read lock acquired by first reader")
                firstReaderResult.set(buffer.size)
                readerBarrier.await()
            }
            println("Read lock released by first reader")
            testExecutionLatch.countDown()
        }

        val secondReaderResult = AtomicInteger(-1)
        val r2 = Thread {
            latch.await()

            locks().lockRead(resourceId()) {
                println("Read lock acquired by second reader")
                secondReaderResult.set(buffer.size)
            }
            println("Read lock released by second reader")
            testExecutionLatch.countDown()
        }

        val w1 = Thread {
            writerBarrier.await()

            locks().lockWrite(resourceId()) {
                println("Write lock acquired")

                latch.countDown()

                buffer.add(HELLO_WORLD)
            }
            println("Write lock released")
            testExecutionLatch.countDown()
        }

        r1.start()
        r2.start()
        w1.start()

        readerBarrier.await()
        Assertions.assertEquals(0, firstReaderResult.get())

        writerBarrier.await()

        testExecutionLatch.await()

        Assertions.assertEquals(1, secondReaderResult.get())
        Assertions.assertEquals(HELLO_WORLD, buffer.peek())
    }

}
