package com.github.grambbledook.example.spark.handler

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.grambbledook.example.spark.dto.Result
import com.github.grambbledook.example.spark.dto.ServiceError
import com.github.grambbledook.example.spark.dto.WorkflowFailure
import com.github.grambbledook.example.spark.dto.error.AccountCode.INVALID_AMOUNT
import com.github.grambbledook.example.spark.dto.request.AccountWithdrawRequest
import com.github.grambbledook.example.spark.dto.response.AccountWithdrawDetails
import com.github.grambbledook.example.spark.dto.response.Receipt
import com.github.grambbledook.example.spark.dto.response.TransactionType
import com.github.grambbledook.example.spark.service.AccountService
import java.math.BigDecimal

class AccountWithdrawHandler(private val service: AccountService, override val mapper: ObjectMapper) : AbstractJsonHandler<AccountWithdrawRequest, Receipt<AccountWithdrawDetails>>(mapper) {

    override fun process(request: AccountWithdrawRequest): Result {
        logger.info("Withdraw money request received for account [${request.id}]")

        return if (request.amount <= BigDecimal.ZERO) {
            WorkflowFailure(INVALID_AMOUNT, "Withdraw amount must be greater than zero.")
        } else performAction {
            execute(request)
        }
    }

    private fun execute(request: AccountWithdrawRequest): Either<ServiceError, Receipt<AccountWithdrawDetails>> {
        return service.withdraw(request.id, request.amount).map {

            val details = AccountWithdrawDetails(
                    accountId = it.id,
                    amount = request.amount,
                    available = it.amount
            )

            Receipt(TransactionType.WITHDRAWAL, details)
        }
    }


}