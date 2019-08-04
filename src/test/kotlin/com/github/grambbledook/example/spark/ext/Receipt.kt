package com.github.grambbledook.example.spark.ext

import arrow.core.Either
import com.github.grambbledook.example.spark.dto.response.Receipt
import com.github.grambbledook.example.spark.dto.response.TransactionType
import java.math.BigDecimal

fun Either<*, Receipt>.accountId(): Long = right().accountId
fun Either<*, Receipt>.operation(): TransactionType = right().operation
fun Either<*, Receipt>.transactionAmount(): BigDecimal? = right().amount
fun Either<*, Receipt>.balance(): BigDecimal = right().balance
