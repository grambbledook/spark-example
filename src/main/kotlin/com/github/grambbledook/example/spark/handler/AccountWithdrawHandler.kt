package com.github.grambbledook.example.spark.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.grambbledook.example.spark.domain.error.AccountCode.INVALID_AMOUNT
import com.github.grambbledook.example.spark.domain.Result
import com.github.grambbledook.example.spark.domain.WorkflowFailure
import com.github.grambbledook.example.spark.domain.request.AccountWithdrawRequest
import com.github.grambbledook.example.spark.service.AccountService
import java.math.BigDecimal

class AccountWithdrawHandler(private val service: AccountService, override val mapper: ObjectMapper) : AbstractJsonHandler<AccountWithdrawRequest>(mapper, AccountWithdrawRequest::class.java) {

    override fun process(request: AccountWithdrawRequest): Result {
        logger.info("Withdraw money request received for account [${request.id}]")

        return if (request.amount <= BigDecimal.ZERO) {
            WorkflowFailure(INVALID_AMOUNT, "Withdraw amount must be greater than zero.")
        } else performAction {
            service.withdraw(request.id,  request.amount)
        }
    }


}