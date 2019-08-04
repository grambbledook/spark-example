package com.github.grambbledook.example.spark.service


import arrow.core.*
import arrow.core.extensions.either.monad.binding
import com.github.grambbledook.example.spark.dto.error.ServiceError
import com.github.grambbledook.example.spark.dto.domain.Account
import com.github.grambbledook.example.spark.dto.error.AccountCode.ACCOUNT_NOT_FOUND
import com.github.grambbledook.example.spark.dto.error.AccountCode.INSUFFICIENT_FUNDS
import com.github.grambbledook.example.spark.lock.AccountRWLock
import com.github.grambbledook.example.spark.repository.InMemoryAccountRepository
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicLong

class InMemoryAccountServiceImpl(private val idGenerator: AtomicLong,
                                 private val accountRepo: InMemoryAccountRepository,
                                 private val lock: AccountRWLock) : AccountService {

    override fun create(amount: BigDecimal, owner: String): Either<ServiceError, Account> {
        val id = idGenerator.incrementAndGet()

        return lock.lockWrite(id) {
            Right(accountRepo.save(Account(id, amount, owner)))
        }
    }

    override fun getInfo(id: Long): Either<ServiceError, Account> {
        return lock.lockRead(id) {
            val account = accountRepo.findById(id)

            when (account) {
                is Some -> Right(account.t)
                is None -> Left(ServiceError(ACCOUNT_NOT_FOUND, "Account [$id] not found."))
            }
        }
    }

    override fun transfer(from: Long, to: Long, amount: BigDecimal): Either<ServiceError, Account> {
        return lock.lockWrite(minOf(from, to)) {
            lock.lockWrite(maxOf(from, to)) {
                binding {
                    val (acc1) = getInfo(from)
                    val (acc2) = getInfo(to)

                    val (result) = withdraw0(acc1, amount)
                    val (_) = deposit0(acc2, amount)
                    result
                }
            }
        }
    }

    override fun withdraw(id: Long, amount: BigDecimal): Either<ServiceError, Account> {
        return lock.lockWrite(id) {
            val account = getInfo(id)

            when (account) {
                is Either.Right -> withdraw0(account.b, amount)
                else -> account
            }
        }
    }

    private fun withdraw0(account: Account, amount: BigDecimal): Either<ServiceError, Account> {
        val newAmount = account.balance - amount

        return if (newAmount < BigDecimal.ZERO)
            Left(ServiceError(INSUFFICIENT_FUNDS, "Unable to complete operation. Not enough funds on account [${account.id}]."))
        else
            Right(accountRepo.save(account.copy(balance = newAmount)))
    }

    override fun deposit(id: Long, amount: BigDecimal): Either<ServiceError, Account> {
        return lock.lockWrite(id) {
            val account = getInfo(id)

            when (account) {
                is Either.Right -> deposit0(account.b, amount)
                else -> account
            }
        }
    }

    private fun deposit0(account: Account, amount: BigDecimal): Either<ServiceError, Account> {
        val newAmount = account.balance + amount

        return Right(accountRepo.save(account.copy(balance = newAmount)))
    }

}