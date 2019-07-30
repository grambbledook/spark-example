package com.github.grambbledook.example.spark

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.grambbledook.example.spark.dto.domain.Account
import com.github.grambbledook.example.spark.handler.*
import com.github.grambbledook.example.spark.lock.AccountRWLock
import com.github.grambbledook.example.spark.repository.InMemoryAccountRepository
import com.github.grambbledook.example.spark.service.InMemoryAccountServiceImpl
import spark.Service
import java.io.File
import java.net.URI
import java.util.concurrent.atomic.AtomicLong

val mapper = ObjectMapper().apply { registerModule(KotlinModule()) }

fun main(args: Array<String>) {

    val config = mapper.convertValue<AppConfig>(
            args.map { it.split("=") }.map { it[0] to it[1] }.toMap(),
            AppConfig::class.java
    )


    val service = start(config)
    Runtime.getRuntime().addShutdownHook(Thread { service.stop() })
}

fun start(config: AppConfig): Service {
    val service = initAccountService(config)
    val transformer = JsonTransformer(mapper)

    return startSparkInstance(config, service, transformer, mapper)
}

private fun startSparkInstance(config: AppConfig, service: InMemoryAccountServiceImpl, transformer: JsonTransformer, mapper: ObjectMapper): Service {
    return Service.ignite().apply {
        port(config.port)

        get("/accounts/:id", GetAccountInfoHandler(service), transformer)
        post("/accounts", CreateAccountHandler(service, mapper), transformer)
        post("/accounts/deposit", AccountDepositHandler(service, mapper), transformer)
        post("/accounts/withdraw", AccountWithdrawHandler(service, mapper), transformer)
        post("/accounts/transfer", AccountTransferHandler(service, mapper), transformer)
    }

}

fun initAccountService(config: AppConfig): InMemoryAccountServiceImpl {
    val initialData = if (config.data != null) loadData(config.data) else mapOf()
    val idGenerator = AtomicLong(initialData.keys.max() ?: 0)

    return InMemoryAccountServiceImpl(
            idGenerator = idGenerator,
            accountRepo = InMemoryAccountRepository(initialData),
            lock = AccountRWLock()
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

data class AppConfig(val port: Int = 8080, val data: String? = "/data.csv")