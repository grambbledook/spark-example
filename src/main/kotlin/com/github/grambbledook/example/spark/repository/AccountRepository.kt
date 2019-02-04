package com.github.grambbledook.example.spark.repository

import arrow.core.Option
import arrow.core.Try
import com.github.grambbledook.example.spark.dto.Account

interface AccountRepository {
    fun findById(id: Long): Try<Option<Account>>
    fun save(account: Account): Try<Account>
}

