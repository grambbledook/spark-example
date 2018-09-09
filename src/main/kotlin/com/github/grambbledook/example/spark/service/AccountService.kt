package com.github.grambbledook.example.spark.service

import com.github.grambbledook.example.spark.dto.Account
import io.vavr.control.Try

interface AccountService {
    fun create(amount: Double, owner: String): Try<Account>
    fun getInfo(id: Long): Try<Account>
    fun transfer(from: Long, to: Long, amount: Double): Try<Account>
    fun deposit(id: Long, amount: Double): Try<Account>
    fun withdraw(id: Long, amount: Double): Try<Account>
}