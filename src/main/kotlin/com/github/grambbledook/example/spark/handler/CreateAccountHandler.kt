package com.github.grambbledook.example.spark.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.grambbledook.example.spark.handler.traits.HandlerMixin
import com.github.grambbledook.example.spark.dto.Result
import com.github.grambbledook.example.spark.service.AccountService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.Request
import java.math.BigDecimal

class CreateAccountHandler(private val accountService: AccountService, override val mapper: ObjectMapper) : HandlerMixin<CreateAccountHandler.CreateAccountRequest> {

    override val logger: Logger = LoggerFactory.getLogger(CreateAccountHandler::class.java)

    override fun getValue(request: Request): CreateAccountRequest = mapper.readValue(request.body(), CreateAccountRequest::class.java)

    override fun process(request: CreateAccountRequest): Result {
        logger.trace("Create account request received")
        return performAction {
            accountService.create(request.amount, request.owner)
        }
    }

    data class CreateAccountRequest(val amount: BigDecimal, val owner: String)
}