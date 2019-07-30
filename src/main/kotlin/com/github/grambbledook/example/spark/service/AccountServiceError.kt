package com.github.grambbledook.example.spark.service

import com.github.grambbledook.example.spark.dto.ServiceError
import com.github.grambbledook.example.spark.dto.error.ErrorCode

data class AccountServiceError(override val error: ErrorCode, override val message: String) : ServiceError
