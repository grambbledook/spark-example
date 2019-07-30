package com.github.grambbledook.example.spark.dto.error

interface ErrorCode {
    val code: Int
}

enum class BadRequest(override val code: Int): ErrorCode {
    BAD_REQUEST(400)
}

enum class ServiceErrorCode(override val code: Int): ErrorCode {
    INTERNAL_ERROR(500)
}

enum class AccountCode(override val code: Int) : ErrorCode {
    ACCOUNT_NOT_FOUND(404),
    INVALID_AMOUNT(1000),
    INSUFFICIENT_FUNDS(2000)
}
