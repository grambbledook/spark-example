package com.github.grambbledook.example.spark.handler

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.grambbledook.example.spark.dto.Result
import com.github.grambbledook.example.spark.dto.ServiceError
import com.github.grambbledook.example.spark.dto.WorkflowFailure
import com.github.grambbledook.example.spark.dto.error.AccountCode.INVALID_AMOUNT
import com.github.grambbledook.example.spark.dto.request.AccountDepositRequest
import com.github.grambbledook.example.spark.dto.response.AccountDepositDetails
import com.github.grambbledook.example.spark.dto.response.Receipt
import com.github.grambbledook.example.spark.dto.response.TransactionType
import com.github.grambbledook.example.spark.service.AccountService
import java.math.BigDecimal

class AccountDepositHandler(private val accountService: AccountService, mapper: ObjectMapper) : AbstractJsonHandler<AccountDepositRequest, Receipt<AccountDepositDetails>>(mapper) {

    override fun process(request: AccountDepositRequest): Result {
        logger.info("Deposit money request received for account [${request.id}]")

        return if (request.amount <= BigDecimal.ZERO) {
            WorkflowFailure(INVALID_AMOUNT, "Deposit amount must be greater than zero.")
        } else performAction {
            execute(request)
        }
    }

    private fun execute(request: AccountDepositRequest): Either<ServiceError, Receipt<AccountDepositDetails>> {
        return accountService.deposit(request.id, request.amount).map {

            val details = AccountDepositDetails(
                    accountId = it.id,
                    amount = request.amount,
                    available = it.amount
            )

            Receipt(TransactionType.DEPOSIT, details)
        }
    }

}