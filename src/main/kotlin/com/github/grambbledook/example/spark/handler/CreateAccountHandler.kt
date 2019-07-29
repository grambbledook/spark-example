package com.github.grambbledook.example.spark.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.grambbledook.example.spark.domain.error.AccountCode.INVALID_AMOUNT
import com.github.grambbledook.example.spark.domain.Result
import com.github.grambbledook.example.spark.domain.WorkflowFailure
import com.github.grambbledook.example.spark.domain.request.CreateAccountRequest
import com.github.grambbledook.example.spark.service.AccountService
import java.math.BigDecimal

class CreateAccountHandler(private val service: AccountService, mapper: ObjectMapper) : AbstractJsonHandler<CreateAccountRequest>(mapper, CreateAccountRequest::class.java) {

    override fun process(request: CreateAccountRequest): Result {
        logger.info("Create account request received")

        return if (request.amount <= BigDecimal.ZERO) {
            WorkflowFailure(INVALID_AMOUNT, "Starting amount must be greater than zero.")
        } else performAction {
            service.create(request.amount, request.owner)
        }
    }

}