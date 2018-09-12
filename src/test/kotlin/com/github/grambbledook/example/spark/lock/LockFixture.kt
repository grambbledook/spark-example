package com.github.grambbledook.example.spark.lock

const val ID: Long = 10
const val HELLO_WORLD = "Hello World"

interface LockFixture {
    fun timeConsumingPart() {
        Thread.sleep(1000)
    }
}
