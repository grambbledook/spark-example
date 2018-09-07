package com.github.grambbledook.example.spark.service

import io.vavr.control.Either

interface AccountDepositService {
    fun transfer(from: Long, to: Long, amount: Double): Either<String, String>
}