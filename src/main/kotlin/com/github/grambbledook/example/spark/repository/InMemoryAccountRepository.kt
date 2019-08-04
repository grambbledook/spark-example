package com.github.grambbledook.example.spark.repository

import arrow.core.Option
import com.github.grambbledook.example.spark.dto.domain.Account
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

open class InMemoryAccountRepository(initialData: Map<Long, Account> = mapOf()) : AccountRepository {
    private val accounts: ConcurrentMap<Long, Account> = ConcurrentHashMap()

    init {
        accounts.putAll(initialData)
    }

    override fun findById(id: Long): Option<Account> = Option.fromNullable(accounts[id])

    override fun save(account: Account): Account {
        accounts[account.id] = account

        return account
    }

}