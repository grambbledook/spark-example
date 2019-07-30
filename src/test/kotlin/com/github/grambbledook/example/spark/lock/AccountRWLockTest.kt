package com.github.grambbledook.example.spark.lock

import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier

val rwLock = AccountRWLock()

class AccountRWLockTest{

    @Test
    fun testReaderIsBlockedOnActiveWrite() {
        val latch = CountDownLatch(1)
        val readerBarrier = CyclicBarrier(2)
        val writerBarrier = CyclicBarrier(2)

        val buffer = ArrayDeque<String>()

        val r1 = Reader1(buffer, readerBarrier).apply { start() }
        val r2 = Reader2(buffer, latch).apply { start() }
        val w1 = Writer(buffer, writerBarrier, latch).apply { start() }


        readerBarrier.await()
        assert(0 == r1.result)
        writerBarrier.await()

        r1.join()
        r2.join()
        w1.join()

        assert(1 == r2.result)
        assert(HELLO_WORLD == buffer.peek())
    }

    class Reader1(private val buffer: Queue<String>, private val barrier: CyclicBarrier) : Thread(), LockFixture {
        var result: Int = Int.MIN_VALUE

        override fun run() {
            rwLock.lockRead(ID) {
                println("Reader1 acquire")
                result = buffer.size
                barrier.await()
                timeConsumingPart()
                println("Reader1 release")
            }
        }
    }

    class Reader2(private val buffer: Queue<String>, private val latch: CountDownLatch) : Thread(), LockFixture {
        var result: Int = Int.MIN_VALUE

        override fun run() {
            latch.await()

            rwLock.lockRead(ID) {
                println("Reader2 acquire")
                timeConsumingPart()
                result = buffer.size
                println("Reader2 release")
            }
        }
    }

    class Writer(private val buffer: Queue<String>, private val barrier: CyclicBarrier, private val latch: CountDownLatch) : Thread(), LockFixture {
        override fun run() {
            barrier.await()

            rwLock.lockWrite(ID) {
                println("Writer acquire")

                latch.countDown()
                println("Writer countdown")

                timeConsumingPart()
                buffer.add(HELLO_WORLD)

                println("Writer release")
            }
        }
    }

}
