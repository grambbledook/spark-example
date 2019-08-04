package com.github.grambbledook.example.spark.lock

const val HELLO_WORLD = "Hello World"


interface LockFixture {
    fun resourceId() = ID
    fun locks() = rwLock

    fun timeConsumingPart() {
        Thread.sleep(1000)
    }

    companion object {
        val rwLock = AccountRWLock()
        const val ID: Long = 10

    }
}
