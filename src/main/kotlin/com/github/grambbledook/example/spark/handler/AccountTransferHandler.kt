package com.github.grambbledook.example.spark.handler

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.grambbledook.example.spark.dto.Result
import com.github.grambbledook.example.spark.dto.ServiceError
import com.github.grambbledook.example.spark.dto.WorkflowFailure
import com.github.grambbledook.example.spark.dto.error.AccountCode.INVALID_AMOUNT
import com.github.grambbledook.example.spark.dto.request.AccountTransferRequest
import com.github.grambbledook.example.spark.dto.response.AccountTransferDetails
import com.github.grambbledook.example.spark.dto.response.Receipt
import com.github.grambbledook.example.spark.dto.response.TransactionType
import com.github.grambbledook.example.spark.service.AccountService
import java.math.BigDecimal

class AccountTransferHandler(private val service: AccountService, mapper: ObjectMapper) : AbstractJsonHandler<AccountTransferRequest, Receipt<AccountTransferDetails>>(mapper) {

    override fun process(request: AccountTransferRequest): Result {
        logger.info("Transfer money request received for accounts [${request.from} -> ${request.to}]")

        return if (request.amount <= BigDecimal.ZERO) {
            WorkflowFailure(INVALID_AMOUNT, "Transfer amount must be greater than zero.")
        } else performAction {
            execute(request)
        }
    }

    private fun execute(request: AccountTransferRequest): Either<ServiceError, Receipt<AccountTransferDetails>> {
        return service.transfer(request.from, request.to, request.amount).map {

            val details = AccountTransferDetails(
                    sourceAccountId = request.from,
                    destinationAccountId = request.to,
                    amount = request.amount,
                    available = it.amount
            )

            Receipt(TransactionType.TRANSFER, details)
        }
    }

}