package com.github.grambbledook.example.spark.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.grambbledook.example.spark.handler.traits.HandlerMixin
import com.github.grambbledook.example.spark.dto.Result
import com.github.grambbledook.example.spark.service.AccountService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.Request
import java.math.BigDecimal

class AccountDepositHandler(private val accountService: AccountService, override val mapper: ObjectMapper) : HandlerMixin<AccountDepositHandler.AccountDepositRequest> {

    override val logger: Logger = LoggerFactory.getLogger(AccountDepositHandler::class.java)

    override fun getValue(request: Request): AccountDepositRequest = mapper.readValue(request.body(), AccountDepositRequest::class.java)

    override fun process(request: AccountDepositRequest): Result {
        logger.trace("Deposit money request received for account [${request.id}]")
        return performAction {
            accountService.deposit(request.id, request.amount)
        }
    }

    data class AccountDepositRequest(val id: Long, val amount: BigDecimal)
}