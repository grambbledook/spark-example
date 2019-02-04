package com.github.grambbledook.example.spark.service

import com.github.grambbledook.example.spark.dto.BusinessCode

sealed class ServiceError

data class UnknownError(val e: Throwable): ServiceError()
data class AccountError(val code: BusinessCode) : ServiceError()

