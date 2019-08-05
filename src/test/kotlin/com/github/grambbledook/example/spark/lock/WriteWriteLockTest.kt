package com.github.grambbledook.example.spark.lock

import com.github.grambbledook.example.spark.fixture.LockFixture
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.atomic.AtomicInteger


class WriteWriteLockTest : LockFixture {

    @Test
    fun `Test only one Writer has access to the resource`() {
        val firstTestLatch = CountDownLatch(1)
        val writerLatch = CountDownLatch(1)
        val secondTestLatch = CountDownLatch(2)

        val writerBarrier = CyclicBarrier(2)

        val buffer = AtomicInteger(0)
        val t1 = Thread {
            writerBarrier.await()

            locks().lockWrite(resourceId()) {
                buffer.incrementAndGet()
                firstTestLatch.countDown()
                writerLatch.await()
            }
            secondTestLatch.countDown()
        }


        val t2 = Thread {
            writerBarrier.await()

            locks().lockWrite(resourceId()) {
                buffer.decrementAndGet()
                firstTestLatch.countDown()
                writerLatch.await()
            }
            secondTestLatch.countDown()
        }

        t1.start()
        t2.start()

        firstTestLatch.await()
        Assertions.assertNotEquals(0, buffer.get())

        writerLatch.countDown()
        secondTestLatch.await()
        Assertions.assertEquals(0, buffer.get())
    }

}
