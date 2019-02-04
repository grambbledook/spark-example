package com.github.grambbledook.example.spark.ext

import arrow.core.Either
import java.lang.Exception

fun <L, R> Either<L, R>.right(): R {
    return when (this) {
        is Either.Left -> throw Exception("Either.Right is expected, but [${this}] was found")
        is Either.Right -> this.b
    }
}

fun <L, R> Either<L, R>.left(): L {
    return when (this) {
        is Either.Left -> this.a
        is Either.Right -> throw Exception("Either.Left is expected, but [${this}] was found")
    }
}