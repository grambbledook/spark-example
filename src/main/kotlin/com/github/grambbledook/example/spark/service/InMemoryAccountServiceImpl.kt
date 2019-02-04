package com.github.grambbledook.example.spark.service

import arrow.core.*
import com.github.grambbledook.example.spark.dto.Account
import com.github.grambbledook.example.spark.dto.BusinessCode
import com.github.grambbledook.example.spark.lock.AccountRWLock
import com.github.grambbledook.example.spark.repository.InMemoryAccountRepository
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicLong

class InMemoryAccountServiceImpl(startId: Long, private val accountRepo: InMemoryAccountRepository, private val lock: AccountRWLock) : AccountService {

    private val idGenerator = AtomicLong(startId)

    override fun create(amount: BigDecimal, owner: String): Either<ServiceError, Account> {
        val id = idGenerator.getAndIncrement()

        return lock.lockWrite(id) {
            accountRepo.save(Account(id, amount, owner))
        }.toEither { UnknownError(it) }
    }

    override fun getInfo(id: Long): Either<ServiceError, Account> {
        return lock.lockRead(id) {
            accountRepo.findById(id).toEither {
                UnknownError(it)
            }.flatMap {
                when (it) {
                    is Some -> Right(it.t)
                    is None -> Left(AccountError(BusinessCode.ACCOUNT_NOT_FOUND))
                }
            }
        }
    }

    override fun transfer(from: Long, to: Long, amount: BigDecimal): Either<ServiceError, Account> {
        return lock.lockWrite(minOf(from, to)) {
            lock.lockWrite(maxOf(from, to)) {
                getInfo(from).flatMap { acc1 ->
                    getInfo(to).flatMap { acc2 ->
                        withdraw0(acc1, amount).map {
                            deposit0(acc2, amount)
                            it
                        }
                    }
                }
            }
        }
    }

    override fun withdraw(id: Long, amount: BigDecimal): Either<ServiceError, Account> {
        return lock.lockWrite(id) {
            getInfo(id).flatMap {
                withdraw0(it, amount)
            }
        }
    }

    private fun withdraw0(it: Account, amount: BigDecimal): Either<ServiceError, Account> {
        val new = it.copy(amount = it.amount - amount)

        return if (new.amount < BigDecimal.ZERO) Left(AccountError(BusinessCode.INSUFFICIENT_FUNDS))
        else accountRepo.save(new).toEither { UnknownError(it) }
    }

    override fun deposit(id: Long, amount: BigDecimal): Either<ServiceError, Account> {
        return lock.lockWrite(id) {
            getInfo(id).flatMap {
                deposit0(it, amount)
            }
        }
    }

    private fun deposit0(it: Account, amount: BigDecimal): Either<ServiceError, Account> {
        val new = it.copy(amount = it.amount + amount)
        return accountRepo.save(new).toEither { UnknownError(it) }
    }

}