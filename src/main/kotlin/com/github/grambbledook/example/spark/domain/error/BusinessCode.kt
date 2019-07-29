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
    INVALID_AMOUNT(1000),
    ACCOUNT_NOT_FOUND(1000),
    INSUFFICIENT_FUNDS(2000)
}
