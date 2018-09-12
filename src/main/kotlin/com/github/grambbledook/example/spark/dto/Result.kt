package com.github.grambbledook.example.spark.dto

sealed class Result
data class Success<T>(val payload: T) : Result()
data class Failure(val businessCode: BusinessCode?, val reason: Any?) : Result()
data class Error(val reason: Any?) : Result()
