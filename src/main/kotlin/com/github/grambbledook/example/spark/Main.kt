package com.github.grambbledook.example.spark

import com.github.grambbledook.example.spark.dto.domain.Account
import com.github.grambbledook.example.spark.dto.request.*
import com.github.grambbledook.example.spark.lock.AccountRWLock
import com.github.grambbledook.example.spark.repository.InMemoryAccountRepository
import com.github.grambbledook.example.spark.service.AccountService
import com.github.grambbledook.example.spark.service.InMemoryAccountServiceImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.Service
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicLong

val logger: Logger = LoggerFactory.getLogger("Main")

fun main(args: Array<String>) {
    val service = start(8080)

    Runtime.getRuntime().addShutdownHook(
            Thread { service.stop() }
    )
}

fun start(port: Int): Service {
    val service = initAccountService()

    return startSparkInstance(port, service)
}

private fun startSparkInstance(port: Int, service: AccountService): Service {
    return Service.ignite().apply {
        port(port)
        path("/accounts") {
            before("/*") { req, _ ->
                logger.info("Received ${req.uri()} call with params: [${req.params()}], body [${req.body()}}")
            }

            get("/:id") { req, res ->
                val getAccount = GetAccountRequest(req.params("id").toLong())
                getAccount.process(res) { service.getInfo(it.id) }
            }

            post("") { req, res ->
                req.json<CreateRequest>().process(res) { service.create(it.amount, it.owner) }
            }

            post("/deposit") { req, res ->
                req.json<DepositRequest>().process(res) { service.deposit(it.id, it.amount) }
            }

            post("/withdraw") { req, res ->
                req.json<WithdrawRequest>().process(res) { service.withdraw(it.id, it.amount) }
            }

            post("/transfer") { req, res ->
                req.json<TransferRequest>().process(res) { service.transfer(it.from, it.to, it.amount) }
            }
        }
    }

}

fun initAccountService(): InMemoryAccountServiceImpl {
    val initialData = loadInitialData()
    val idGenerator = AtomicLong(initialData.keys.max() ?: 0)

    return InMemoryAccountServiceImpl(
            idGenerator = idGenerator,
            accountRepo = InMemoryAccountRepository(initialData),
            lock = AccountRWLock()
    )
}

fun loadInitialData(): Map<Long, Account> {
    return mapOf(
            1L to Account(1, BigDecimal(500), "John Doe"),
            10L to Account(10, BigDecimal(500), "John Doe"),
            2L to Account(2, BigDecimal(0), "Jane Doe"),
            20L to Account(20, BigDecimal(1000), "Jane Doe"),
            3L to Account(3, BigDecimal(0), "John Johnson"),
            4L to Account(4, BigDecimal(0), "John Johnson")
    )
}

