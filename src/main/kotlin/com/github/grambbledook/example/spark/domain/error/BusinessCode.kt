package com.github.grambbledook.example.spark.domain.error

interface BusinessCode {
    val code: Int
}

enum class BadRequest(override val code: Int): BusinessCode {
    BAD_REQUEST(400)
}

enum class ServiceErrorCode(override val code: Int): BusinessCode {
    INTERNAL_ERROR(500)
}

enum class AccountCode(override val code: Int) : BusinessCode {
    ACCOUNT_NOT_FOUND(404),
    INVALID_AMOUNT(1000),
    INSUFFICIENT_FUNDS(2000)
}
