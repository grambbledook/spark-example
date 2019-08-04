package com.github.grambbledook.example.spark.lock

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.atomic.AtomicLong


class ReadReadLockTest : LockFixture {

    @Test
    fun `Test Reader is not blocked by other active Readers`() {
        val latch = CountDownLatch(2)
        val readerBarrier = CyclicBarrier(2)

        val firstReaderResult = AtomicLong(-1)
        val r1 = Thread {

            locks().lockRead(resourceId()) {
                println("Read lock acquired by first reader")

                readerBarrier.await()
                firstReaderResult.set(resourceId())
                readerBarrier.await()
            }
            println("Read lock released by first reader")
            latch.countDown()
        }

        val secondReaderResult = AtomicLong(-1)
        val r2 = Thread {

            locks().lockRead(resourceId()) {
                println("Read lock acquired by second reader")

                readerBarrier.await()
                secondReaderResult.set(resourceId())
                readerBarrier.await()
            }
            println("Read lock released by second reader")
            latch.countDown()
        }


        r1.start()
        r2.start()

        latch.await()
        Assertions.assertEquals(resourceId(), firstReaderResult.get())
        Assertions.assertEquals(resourceId(), secondReaderResult.get())
    }

}
