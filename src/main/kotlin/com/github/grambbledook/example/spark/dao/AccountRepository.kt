package com.github.grambbledook.example.spark.dao

import com.github.grambbledook.example.spark.dto.Account
import io.vavr.control.Option
import io.vavr.control.Try

interface AccountRepository {
    fun findById(id: Long): Account?
    fun save(account: Account): Try<Account>
    fun save(vararg account: Account): Try<List<Account>>
    fun create(id: Long, amount: Double, owner: String): Try<Account>

}

