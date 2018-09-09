package com.github.grambbledook.example.spark.repository

import com.github.grambbledook.example.spark.dto.Account
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class InMemoryAccountRepository(initialData: Map<Long, Account> = mapOf()) : AccountRepository {
    private val accounts: ConcurrentMap<Long, Account> = ConcurrentHashMap()

    init {
        accounts.putAll(initialData)
    }

    override fun findById(id: Long): Account? = accounts[id]

    override fun save(account: Account): Account {
        return accounts.compute(account.id) { _, _ ->
            account
        }!!
    }

}