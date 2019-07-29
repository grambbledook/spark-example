package com.github.grambbledook.example.spark.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.grambbledook.example.spark.domain.error.AccountCode.INVALID_AMOUNT
import com.github.grambbledook.example.spark.domain.Result
import com.github.grambbledook.example.spark.domain.WorkflowFailure
import com.github.grambbledook.example.spark.domain.request.AccountDepositRequest
import com.github.grambbledook.example.spark.service.AccountService
import java.math.BigDecimal

class AccountDepositHandler(private val accountService: AccountService, mapper: ObjectMapper) : AbstractJsonHandler<AccountDepositRequest>(mapper, AccountDepositRequest::class.java) {

    override fun process(request: AccountDepositRequest): Result {
        logger.info("Deposit money request received for account [${request.id}]")

        return if (request.amount <= BigDecimal.ZERO) {
            WorkflowFailure(INVALID_AMOUNT, "Deposit amount must be greater than zero.")
        } else performAction {
            accountService.deposit(request.id, request.amount)
        }
    }

}