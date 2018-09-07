package com.github.grambbledook.example.spark.repository

import com.github.grambbledook.example.spark.dto.Account

interface AccountRepository {
    fun findById(id: Long): Account?
    fun save(account: Account): Account
}

