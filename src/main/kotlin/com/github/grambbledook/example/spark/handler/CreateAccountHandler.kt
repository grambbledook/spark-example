package com.github.grambbledook.example.spark.handler

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.grambbledook.example.spark.dto.Result
import com.github.grambbledook.example.spark.dto.ServiceError
import com.github.grambbledook.example.spark.dto.WorkflowFailure
import com.github.grambbledook.example.spark.dto.error.AccountCode.INVALID_AMOUNT
import com.github.grambbledook.example.spark.dto.request.CreateAccountRequest
import com.github.grambbledook.example.spark.dto.response.AccountCreatedDetails
import com.github.grambbledook.example.spark.dto.response.Receipt
import com.github.grambbledook.example.spark.dto.response.TransactionType
import com.github.grambbledook.example.spark.service.AccountService
import java.math.BigDecimal

class CreateAccountHandler(private val service: AccountService, mapper: ObjectMapper) : AbstractJsonHandler<CreateAccountRequest, Receipt<AccountCreatedDetails>>(mapper) {

    override fun process(request: CreateAccountRequest): Result {
        logger.info("Create account request received")

        return if (request.amount < BigDecimal.ZERO) {
            WorkflowFailure(INVALID_AMOUNT, "Starting amount must not be negative.")
        } else performAction {
            execute(request)
        }
    }

    private fun execute(request: CreateAccountRequest): Either<ServiceError, Receipt<AccountCreatedDetails>> {
        return service.create(request.amount, request.owner).map {

            val details = AccountCreatedDetails(
                    accountId = it.id,
                    available = it.amount
            )

            Receipt(TransactionType.ACCOUNT_CREATED, details)
        }
    }

}