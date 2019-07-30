package com.github.grambbledook.example.spark.ext

import arrow.core.Either
import com.github.grambbledook.example.spark.dto.response.AccountCreatedDetails
import com.github.grambbledook.example.spark.dto.response.Receipt

fun Either<*, Receipt<AccountCreatedDetails>>.accountId(): Long = right().details.accountId
