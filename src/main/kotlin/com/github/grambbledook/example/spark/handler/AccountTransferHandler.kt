package com.github.grambbledook.example.spark.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.grambbledook.example.spark.handler.traits.HandlerMixin
import com.github.grambbledook.example.spark.dto.Result
import com.github.grambbledook.example.spark.service.AccountService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.Request
import java.math.BigDecimal

class AccountTransferHandler(private val accountService: AccountService, override val mapper: ObjectMapper) : HandlerMixin<AccountTransferHandler.AccountTransferRequest> {

    override val logger: Logger = LoggerFactory.getLogger(AccountTransferHandler::class.java)

    override fun getValue(request: Request): AccountTransferRequest = mapper.readValue(request.body(), AccountTransferRequest::class.java)

    override fun process(request: AccountTransferRequest): Result {
        logger.trace("Transfer money request received for accounts [${request.from} -> ${request.to}]")
        return performAction {
            accountService.transfer(request.from, request.to, request.amount)
        }
    }

    data class AccountTransferRequest(val from: Long, val to: Long, val amount: BigDecimal)
}