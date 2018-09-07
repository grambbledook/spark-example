package com.github.grambbledook.example.spark.lock

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantLock

class AccountLock {
    private val locks: ConcurrentMap<Long, Wrapper> = ConcurrentHashMap()

    fun acquire(id: Long) {
        val container = locks.compute(id) { _, v ->
            v?.apply { counter.incrementAndGet() } ?: Wrapper()
        }!!

        container.lock.lock()
    }

    fun release(id: Long) {
        val lock = locks[id]
        locks.compute(id) { _, v ->
            v!!.counter.decrementAndGet()

            if (v.counter.get() < 1)
                null
            else v
        }

        lock!!.lock.unlock()
    }

    internal data class Wrapper(val counter: AtomicLong = AtomicLong(1), val lock: ReentrantLock = ReentrantLock())
}



