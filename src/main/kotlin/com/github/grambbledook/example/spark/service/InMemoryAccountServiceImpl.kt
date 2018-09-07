package com.github.grambbledook.example.spark.service

import com.github.grambbledook.example.spark.dto.Account
import com.github.grambbledook.example.spark.lock.AccountRWLockKotlin
import com.github.grambbledook.example.spark.repository.InMemoryAccountRepository
import io.vavr.control.Try
import io.vavr.kotlin.Try

class InMemoryAccountServiceImpl(private val accountDao: InMemoryAccountRepository, private val lock: AccountRWLockKotlin) : AccountService {

    override fun getInfo(id: Long): Try<Account> {
        return lock.lockRead(id) {
            Try { accountDao.findById(id)!! }.recover {
                throw AccountError("Account [$id] not found")
            }
        }
    }

    override fun create(id: Long, amount: Double, owner: String): Try<Account> {
        return lock.lockWrite(id) {
            Try { accountDao.save(Account(id, amount, owner)) }
        }
    }

    override fun transfer(from: Long, to: Long, amount: Double): Try<Account> {
        return lock.lockWrite(minOf(from, to)) {
            lock.lockWrite(maxOf(from, to)) {
                withdraw(from, amount).onSuccess { deposit(to, amount) }
            }
        }
    }

    override fun deposit(id: Long, amount: Double): Try<Account> {
        return lock.lockWrite(id) {
            getInfo(id).mapTry {
                val new = it.copy(amount = it.amount + amount)
                accountDao.save(new)
            }
        }
    }

    override fun withdraw(id: Long, amount: Double): Try<Account> {
        return lock.lockWrite(id) {
            getInfo(id).mapTry {
                val new = it.copy(amount = it.amount - amount)

                if (new.amount < 0) throw AccountError("Not enough money on account [$id]")
                else accountDao.save(new)
            }
        }
    }

}