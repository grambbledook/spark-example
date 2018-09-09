package com.github.grambbledook.example.spark.dto

sealed class Result
data class Success(val payload: Any) : Result()
data class Failure(val code: Int, val reason: Any?) : Result()
