package com.github.grambbledook.example.spark.fixture

import com.github.grambbledook.example.spark.AppConfig
import com.github.grambbledook.example.spark.start
import com.jayway.restassured.RestAssured
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import spark.Service
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

interface SparkFixture {


    companion object {

        private val started = AtomicBoolean(false)
        private val rnd = Random()
        private lateinit var service: Service

        @BeforeAll
        @JvmStatic
        fun startSpark() {
            if (!started.get()) {
                val port = 10000 + rnd.nextInt(55535)
                RestAssured.port = port
                service = start(AppConfig(port = port, data = null))

                started.set(true)
            }
        }

        @AfterAll
        @JvmStatic
        fun stop() {
            service.stop()

            started.set(false)
        }
    }

}
