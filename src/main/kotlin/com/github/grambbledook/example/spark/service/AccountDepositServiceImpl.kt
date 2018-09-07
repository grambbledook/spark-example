package com.github.grambbledook.example.spark.service

import com.github.grambbledook.example.spark.dao.AccountRepository
import com.github.grambbledook.example.spark.lock.AccountRWLockKotlin
import io.vavr.control.Either

class AccountDepositServiceImpl(private val accountDao: AccountRepository) : AccountDepositService {
    private val accountLock = AccountRWLockKotlin()
    override fun transfer(from: Long, to: Long, amount: Double): Either<String, String> {
        return accountLock.lockWrite(minOf(from, to)) {
            accountLock.lockWrite(maxOf(from, to)) {

                val fromAccount = accountDao.findById(from)
                val toAccount = accountDao.findById(to)

                when {

                    fromAccount == null -> Either.left<String, String>("Account [$from] not found")
                    toAccount == null -> Either.left<String, String>("Account [$to] not found")
                    fromAccount.amount - amount < 0 -> Either.left<String, String>("Not enough money on account [$from]")

                    else -> {
                        fromAccount.amount -= amount
                        toAccount.amount += amount
                        accountDao.save(fromAccount)
                        accountDao.save(toAccount)
                        Either.right<String, String>("")
                    }
                }
            }
        }
    }
}