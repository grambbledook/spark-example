package com.github.grambbledook.example.spark.service

open class AccountError(message: String) : Exception(message)
class AccountNotFoundError(message: String) : AccountError(message)
class AccountNotEnoughMoneyError(message: String) : AccountError(message)