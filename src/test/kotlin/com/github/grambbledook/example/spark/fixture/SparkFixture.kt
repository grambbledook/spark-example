package com.github.grambbledook.example.spark.fixture

import com.github.grambbledook.example.spark.AppConfig
import com.github.grambbledook.example.spark.start
import com.jayway.restassured.RestAssured
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import spark.Spark

interface SparkFixture {

    companion object {
        @BeforeAll
        fun startSpark() {
            start(AppConfig(port = 10000, storage = null))
            RestAssured.port = 10000
        }

        @AfterAll
        fun stop() {
            Spark.stop()
        }
    }

}
