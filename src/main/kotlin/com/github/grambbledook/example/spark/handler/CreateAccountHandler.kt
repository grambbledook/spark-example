package com.github.grambbledook.example.spark.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.grambbledook.example.spark.dto.Result
import com.github.grambbledook.example.spark.dto.WorkflowFailure
import com.github.grambbledook.example.spark.dto.error.AccountCode.INVALID_AMOUNT
import com.github.grambbledook.example.spark.dto.request.CreateAccountRequest
import com.github.grambbledook.example.spark.dto.response.AccountCreatedDetails
import com.github.grambbledook.example.spark.dto.response.Receipt
import com.github.grambbledook.example.spark.dto.response.TransactionType
import com.github.grambbledook.example.spark.service.AccountService
import java.math.BigDecimal

class CreateAccountHandler(private val service: AccountService, mapper: ObjectMapper) : AbstractJsonHandler<CreateAccountRequest, Receipt<AccountCreatedDetails>>(mapper, CreateAccountRequest::class.java) {

    override fun process(request: CreateAccountRequest): Result {
        logger.info("Create account request received")

        return if (request.amount < BigDecimal.ZERO) {
            WorkflowFailure(INVALID_AMOUNT, "Starting amount must not be negativex.")
        } else performAction {
            service.create(request.amount, request.owner).map {
                val details = AccountCreatedDetails(accountId = it.id, available = it.amount)

                Receipt(TransactionType.ACCOUNT_CREATION, details)
            }
        }
    }

}