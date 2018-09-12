package com.github.grambbledook.example.spark.service

import com.github.grambbledook.example.spark.dto.BusinessCode

class AccountError(message: String, val code: BusinessCode) : Exception(message)

