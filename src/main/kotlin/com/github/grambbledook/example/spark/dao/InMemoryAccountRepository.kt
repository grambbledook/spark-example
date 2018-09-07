package com.github.grambbledook.example.spark.dao

import com.github.grambbledook.example.spark.dto.Account
import io.vavr.control.Try
import io.vavr.kotlin.Try
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class InMemoryAccountRepository(private val accounts: ConcurrentMap<Long, Account> = ConcurrentHashMap()) : AccountRepository {
    override fun findById(id: Long): Account? = accounts[id]

    override fun save(account: Account): Try<Account> = Try {
        accounts.compute(account.id) { k, v ->
            if (v == null) throw Exception("Account [$k] does not exist")
            else {
                v.amount = account.amount
                v
            }
        }!!
    }

    override fun save(vararg account: Account): Try<List<Account>> {
        return Try { account.map { save(it) }.map { it.get() } }
    }

    override fun create(id: Long, amount: Double, owner: String): Try<Account> = Try {
        val result = accounts.putIfAbsent(id, Account(id, amount, owner))

        if (result != null) throw Exception("Account with id [$id] already exists")
        else accounts[id]!!
    }
}