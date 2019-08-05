package com.github.grambbledook.example.spark.fixture

import com.github.grambbledook.example.spark.start
import io.restassured.RestAssured
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import spark.Service
import java.util.*


interface SparkFixture {

    companion object {
        private const val portConfig = "test.port"

        private val rnd = Random()

        private lateinit var service: Service

        @BeforeAll
        @JvmStatic
        fun startSpark() {
            val port = testPort()
            RestAssured.port = port
            service = start(port)
        }

        private fun testPort(): Int {
            val port = System.getProperty(portConfig)
            val random = (10000 + rnd.nextInt(55535))

            return if (port.isNullOrEmpty()) random else port.toInt()
        }

        @AfterAll
        @JvmStatic
        fun stop() {
            service.stop()
            service.awaitStop()
        }
    }
}
