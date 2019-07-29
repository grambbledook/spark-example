package com.github.grambbledook.example.spark.domain

import com.github.grambbledook.example.spark.domain.error.BusinessCode

interface ServiceError {
    val error: BusinessCode
    val message: String
}

data class AccountError(override val error: BusinessCode, override val message: String) : ServiceError

