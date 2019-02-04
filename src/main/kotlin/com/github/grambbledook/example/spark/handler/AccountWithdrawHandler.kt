package com.github.grambbledook.example.spark.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.grambbledook.example.spark.handler.traits.HandlerMixin
import com.github.grambbledook.example.spark.dto.Result
import com.github.grambbledook.example.spark.service.AccountService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.Request
import java.math.BigDecimal

class AccountWithdrawHandler(private val accountService: AccountService, override val mapper: ObjectMapper) : HandlerMixin<AccountWithdrawHandler.AccountWithdrawRequest> {

    override val logger: Logger = LoggerFactory.getLogger(AccountWithdrawHandler::class.java)

    override fun getValue(request: Request): AccountWithdrawRequest = mapper.readValue(request.body(), AccountWithdrawRequest::class.java)

    override fun process(request: AccountWithdrawRequest): Result {
        logger.trace("Withdraw money request received for account [${request.id}]")
        return performAction {
            accountService.withdraw(request.id, request.amount)
        }
    }

    data class AccountWithdrawRequest(val id: Long, val amount: BigDecimal)
}