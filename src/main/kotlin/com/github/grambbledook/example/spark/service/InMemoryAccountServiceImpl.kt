package com.github.grambbledook.example.spark.service

import com.github.grambbledook.example.spark.dto.Account
import com.github.grambbledook.example.spark.lock.AccountRWLockKotlin
import com.github.grambbledook.example.spark.repository.InMemoryAccountRepository
import io.vavr.control.Try
import io.vavr.kotlin.Try
import java.util.concurrent.atomic.AtomicLong

class InMemoryAccountServiceImpl(startId: Long, private val accountRepo: InMemoryAccountRepository, private val lock: AccountRWLockKotlin) : AccountService {

    private val idGenerator = AtomicLong(startId)

    override fun create(amount: Double, owner: String): Try<Account> {
        val id = idGenerator.getAndIncrement()

        return lock.lockWrite(id) {
            Try {
                accountRepo.save(Account(id, amount, owner))
            }
        }
    }

    override fun getInfo(id: Long): Try<Account> {
        return lock.lockRead(id) {
            Try { accountRepo.findById(id)!! }.recover {
                throw AccountError("Account [$id] not found")
            }
        }
    }

    override fun transfer(from: Long, to: Long, amount: Double): Try<Account> {
        return lock.lockWrite(minOf(from, to)) {
            lock.lockWrite(maxOf(from, to)) {
                Try {
                    val acc1 = getInfo(from).get()
                    val acc2 = getInfo(to).get()

                    withdraw0(acc1, amount).also { deposit0(acc2, amount) }
                }
            }
        }
    }

    override fun withdraw(id: Long, amount: Double): Try<Account> {
        return lock.lockWrite(id) {
            getInfo(id).mapTry {
                withdraw0(it, amount)
            }
        }
    }

    private fun withdraw0(it: Account, amount: Double): Account {
        val new = it.copy(amount = it.amount - amount)

        return if (new.amount < 0) throw AccountError("Not enough money on account [${it.id}]")
        else accountRepo.save(new)
    }

    override fun deposit(id: Long, amount: Double): Try<Account> {
        return lock.lockWrite(id) {
            getInfo(id).mapTry {
                deposit0(it, amount)
            }
        }
    }

    private fun deposit0(it: Account, amount: Double): Account {
        val new = it.copy(amount = it.amount + amount)
        return accountRepo.save(new)
    }

}