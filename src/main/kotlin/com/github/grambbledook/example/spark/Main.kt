package com.github.grambbledook.example.spark

import spark.Spark.get
import spark.Spark.port

fun main(args: Array<String>) {

    port(8080)

    get("/hello") { _, _ -> "Hello World" }

    get("/hello/:name") { request, _ -> "Hello ${request.params(":name")}" }

    get("/say/*/to/*") { request, _ -> "Number of splat params: ${request.splat().size}" }

}