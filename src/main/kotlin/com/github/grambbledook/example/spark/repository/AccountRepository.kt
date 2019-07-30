package com.github.grambbledook.example.spark.repository

import arrow.core.Option
import com.github.grambbledook.example.spark.dto.domain.Account

interface AccountRepository {
    fun findById(id: Long): Option<Account>
    fun save(account: Account): Account
}

