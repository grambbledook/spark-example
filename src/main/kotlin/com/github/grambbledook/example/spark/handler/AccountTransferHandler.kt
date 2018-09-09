package com.github.grambbledook.example.spark.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.grambbledook.example.spark.handler.traits.HandlerMixin
import com.github.grambbledook.example.spark.dto.Result
import com.github.grambbledook.example.spark.service.AccountService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.Request

class AccountTransferHandler(private val accountService: AccountService, override val mapper: ObjectMapper) : HandlerMixin<AccountTransferHandler.AccountTransferRequest> {

    override val logger: Logger = LoggerFactory.getLogger(AccountTransferHandler::class.java)

    override fun getValue(request: Request): AccountTransferRequest = mapper.readValue(request.body(), AccountTransferRequest::class.java)

    override fun process(request: AccountTransferRequest): Result {
        logger.trace("Transfer money request received for accounts [${request.from} -> ${request.to}]")
        return performAction {
            accountService.transfer(request.from, request.to, request.amount)
        }.onFailure {
            logger.error("An error occurred on money transfer from account [${request.from}] to account [${request.to}]", it)
        }.recover { generateErrorResponse(it) }.get()
    }

    data class AccountTransferRequest(val from: Long, val to: Long, val amount: Double)
}