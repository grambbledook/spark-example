package com.github.grambbledook.example.spark.handler

import arrow.core.Try
import com.github.grambbledook.example.spark.domain.Result
import com.github.grambbledook.example.spark.service.AccountService
import spark.Request

class GetAccountInfoHandler(private val accountService: AccountService) : AbstractHandler<Long>() {

    override fun getValue(request: Request): Try<Long> = Try.invoke { request.params("id").toLong() }

    override fun process(request: Long): Result {
        logger.info("Get Account Info request for id [$request]")

        return performAction {
            accountService.getInfo(request)
        }
    }

}