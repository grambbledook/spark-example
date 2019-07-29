package com.github.grambbledook.example.spark.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.grambbledook.example.spark.domain.error.AccountCode.INVALID_AMOUNT
import com.github.grambbledook.example.spark.domain.Result
import com.github.grambbledook.example.spark.domain.WorkflowFailure
import com.github.grambbledook.example.spark.domain.request.AccountTransferRequest
import com.github.grambbledook.example.spark.service.AccountService
import java.math.BigDecimal

class AccountTransferHandler(private val service: AccountService, mapper: ObjectMapper) : AbstractJsonHandler<AccountTransferRequest>(mapper, AccountTransferRequest::class.java) {

    override fun process(request: AccountTransferRequest): Result {
        logger.info("Transfer money request received for accounts [${request.from} -> ${request.to}]")

        return if (request.amount <= BigDecimal.ZERO) {
            WorkflowFailure(INVALID_AMOUNT, "Transfer amount must be greater than zero.")
        } else performAction {
            service.transfer(request.from, request.to, request.amount)
        }
    }

}