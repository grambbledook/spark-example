@file:Suppress("IMPLICIT_CAST_TO_ANY", "UnnecessaryVariable")

package com.github.grambbledook.example.spark

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.flatMap
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.grambbledook.example.spark.dto.ServiceError
import com.github.grambbledook.example.spark.dto.domain.Account
import com.github.grambbledook.example.spark.dto.error.AccountCode
import com.github.grambbledook.example.spark.dto.request.*
import com.github.grambbledook.example.spark.dto.response.Receipt
import com.github.grambbledook.example.spark.dto.response.TransactionType
import spark.Request
import spark.Response
import java.math.BigDecimal

val mapper = ObjectMapper().apply { registerModule(KotlinModule()) }

inline fun <reified T : AccountRequest> Request.json(): T = mapper.readValue(body(), T::class.java)

fun <T : AccountRequest> T.process(response: Response, apply: (T) -> Either<ServiceError, Account>): String {
    return notNegative()
            .flatMap { request ->
                apply(request).map {

                    val operation = when (request) {
                        is TransferRequest -> TransactionType.TRANSFER
                        is CreateRequest -> TransactionType.CREATED
                        is DepositRequest -> TransactionType.DEPOSIT
                        is WithdrawRequest -> TransactionType.WITHDRAWAL
                        else -> TransactionType.INFO
                    }

                    val amount = when (request) {
                        is MoneyOperation -> request.amount
                        else -> null
                    }

                    Receipt(operation = operation,
                            accountId = it.id,
                            amount = amount,
                            balance = it.balance
                    )
                }
            }
            .statusCode(response)
            .body()
}


fun <T : AccountRequest> T.notNegative(): Either<ServiceError, T> {
    val validated = if (this is MoneyOperation && amount < BigDecimal.ZERO) {
        val error = object : ServiceError {
            override val code = AccountCode.INVALID_AMOUNT
            override val message: String = "Amount should be positive"
        }
        Left(error)
    } else
        Right(this)

    return validated
}


fun <E, T> Either<E, T>.statusCode(response: Response): Either<E, T> {
    when (this) {
        is Either.Left -> response.status(400)
        is Either.Right -> response.status(200)
    }

    return this
}


fun <E, T> Either<E, T>.body(): String {
    val body = when (this) {
        is Either.Left -> a
        is Either.Right -> b
    }

    return mapper.writeValueAsString(body)
}