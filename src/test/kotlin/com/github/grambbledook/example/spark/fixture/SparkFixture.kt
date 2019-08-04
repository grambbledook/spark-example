package com.github.grambbledook.example.spark.fixture

import com.github.grambbledook.example.spark.start
import io.restassured.RestAssured
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import spark.Service
import java.util.*


interface SparkFixture {

    companion object {
        private val rnd = Random()
        private lateinit var service: Service

        @BeforeAll
        @JvmStatic
        fun startSpark() {
            val port = 10000 + rnd.nextInt(40000)
            RestAssured.port = port
            service = start(port)
        }

        @AfterAll
        @JvmStatic
        fun stop() {
            service.stop()
            service.awaitStop()
        }
    }

}
