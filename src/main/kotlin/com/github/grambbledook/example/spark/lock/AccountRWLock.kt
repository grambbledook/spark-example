package com.github.grambbledook.example.spark.lock


interface AccountRWLock {
    fun <T> lockRead(id: Long, executeCriticalSection: () -> T): T
    fun <T> lockWrite(id: Long, executeCriticalSection: () -> T): T
}