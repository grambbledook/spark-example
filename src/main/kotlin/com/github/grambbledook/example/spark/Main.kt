package com.github.grambbledook.example.spark

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.grambbledook.example.spark.dto.Account
import com.github.grambbledook.example.spark.handler.AccountTransferHandler
import com.github.grambbledook.example.spark.handler.AccountDepositHandler
import com.github.grambbledook.example.spark.handler.AccountWithdrawHandler
import com.github.grambbledook.example.spark.handler.CreateAccountHandler
import com.github.grambbledook.example.spark.handler.GetAccountInfoHandler
import com.github.grambbledook.example.spark.lock.AccountRWLockKotlin
import com.github.grambbledook.example.spark.repository.InMemoryAccountRepository
import com.github.grambbledook.example.spark.service.InMemoryAccountServiceImpl
import spark.Spark.get
import spark.Spark.post
import spark.Spark.port
import java.io.File
import java.net.URI

fun main(args: Array<String>) {
    val mapper = ObjectMapper().apply { registerModule(KotlinModule()) }

    val config = mapper.convertValue<AppConfig>(
            args.map { it.split("=") }.map { it[0] to it[1] }.toMap(),
            AppConfig::class.java
    )

    port(config.port!!)

    val service = initAccountService(config)

    get("/accounts/:id", GetAccountInfoHandler(service, mapper))
    post("/accounts", CreateAccountHandler(service, mapper))
    post("/accounts/deposit", AccountDepositHandler(service, mapper))
    post("/accounts/withdraw", AccountWithdrawHandler(service, mapper))
    post("/accounts/transfer", AccountTransferHandler(service, mapper))

}

fun initAccountService(config: AppConfig): InMemoryAccountServiceImpl {
    val initialData = loadData(config.storage!!)
    return InMemoryAccountServiceImpl(
            startId = initialData.keys.max() ?: 1,
            accountRepo = InMemoryAccountRepository(initialData),
            lock = AccountRWLockKotlin()
    )
}

fun loadData(path: String): Map<Long, Account> {
    val schema = CsvSchema.builder().setUseHeader(true).build()
    val mapper = CsvMapper().apply { registerModule(KotlinModule()) }
    val resource = when {
        path.startsWith("file:/") -> File(URI.create(path)).inputStream()
        else -> String::class.java.getResource(path).openStream()
    }
    return mapper.readerFor(Account::class.java)
            .with(schema)
            .readValues<Account>(resource)
            .asSequence().associate { it.id to it }
}

data class AppConfig(val port: Int? = 8080, val storage: String? = "/data.csv")