package com.github.grambbledook.example.spark.lock

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantReadWriteLock

internal typealias LockType = (AccountRWLockKotlin.RWLock) -> Lock

class AccountRWLockKotlin {

    private val locks: ConcurrentMap<Long, RWLock> = ConcurrentHashMap()

    private val READ: LockType = { l: RWLock -> l.readLock }
    private val WRITE: LockType = { l: RWLock -> l.writeLock }

    fun lockRead(id: Long, executeCriticalSection: () -> Unit) {
        try {
            acquire(id, READ)
            executeCriticalSection()
        } finally {
            release(id, READ)
        }
    }

    private fun acquire(id: Long, type: LockType) {
        getLock(id, type).lock()
    }

    private fun release(id: Long, type: LockType) {
        cleanup(id, type).unlock()
    }

    fun lockWrite(id: Long, executeCriticalSection: () -> Unit) {
        try {
            acquire(id, WRITE)
            executeCriticalSection()
        } finally {
            release(id, WRITE)
        }
    }

    private fun getLock(id: Long, type: LockType): Lock {
        return type(
                locks.compute(id) { _, v ->
                    v?.apply { counter.incrementAndGet() } ?: RWLock()
                }!!
        )
    }

    private fun cleanup(id: Long, type: LockType): Lock {
        val lock = locks[id]!!
        locks.compute(id) { _, v ->
            val i = v!!.counter.decrementAndGet()

            if (i < 1) null
            else v
        }
        return type(lock)
    }

    internal data class RWLock(val counter: AtomicInteger = AtomicInteger(1)) {
        val readLock: ReentrantReadWriteLock.ReadLock
        val writeLock: ReentrantReadWriteLock.WriteLock

        init {
            val underling = ReentrantReadWriteLock(true)
            readLock = underling.readLock()
            writeLock = underling.writeLock()
        }
    }

}