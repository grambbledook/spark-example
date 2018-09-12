package com.github.grambbledook.example.spark.lock

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicInteger

class AccountLock {
    private val locks: ConcurrentMap<Long, AtomicInteger> = ConcurrentHashMap()

    fun synchronized(id: Long, executeCriticalSection: () -> Unit) {
        try {
            synchronized(getLock(id)) { executeCriticalSection() }
        } finally {
            release(id)
        }
    }

    private fun getLock(id: Long): AtomicInteger {
        return locks.compute(id) { _, v ->
            v?.apply { incrementAndGet() } ?: AtomicInteger(1)
        }!!
    }

    private fun release(id: Long) {
        locks.compute(id) { _, v ->
            val i = v!!.decrementAndGet()

            if (i < 1) null
            else v
        }
    }
}