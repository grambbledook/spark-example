package com.github.grambbledook.example.spark.handler

import arrow.core.Try
import com.github.grambbledook.example.spark.dto.Result
import com.github.grambbledook.example.spark.dto.response.AccountDetails
import com.github.grambbledook.example.spark.dto.response.Receipt
import com.github.grambbledook.example.spark.dto.response.TransactionType
import com.github.grambbledook.example.spark.service.AccountService
import spark.Request

class GetAccountInfoHandler(private val accountService: AccountService) : AbstractHandler<Long, Receipt<AccountDetails>>() {

    override fun getValue(request: Request): Try<Long> = Try.invoke { request.params("id").toLong() }

    override fun process(request: Long): Result {
        logger.info("Get Account Info request for id [$request]")

        return performAction {
            accountService.getInfo(request).map {
                val details = AccountDetails(accountId = it.id, owner = it.owner, available = it.amount)

                Receipt(TransactionType.ACCOUNT_INFO, details)
            }
        }
    }

}