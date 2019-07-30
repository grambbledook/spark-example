package com.github.grambbledook.example.spark.dto

import com.github.grambbledook.example.spark.dto.error.ErrorCode

interface ServiceError {
    val error: ErrorCode
    val message: String
}


