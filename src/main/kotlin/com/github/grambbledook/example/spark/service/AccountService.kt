package com.github.grambbledook.example.spark.service

import arrow.core.Either
import com.github.grambbledook.example.spark.dto.domain.Account
import com.github.grambbledook.example.spark.dto.ServiceError
import java.math.BigDecimal

interface AccountService {
    fun create(amount: BigDecimal, owner: String):Either<ServiceError, Account>
    fun getInfo(id: Long): Either<ServiceError, Account>
    fun transfer(from: Long, to: Long, amount: BigDecimal): Either<ServiceError, Account>
    fun deposit(id: Long, amount: BigDecimal): Either<ServiceError, Account>
    fun withdraw(id: Long, amount: BigDecimal): Either<ServiceError, Account>
}