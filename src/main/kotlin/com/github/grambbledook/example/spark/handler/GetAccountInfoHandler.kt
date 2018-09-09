package com.github.grambbledook.example.spark.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.grambbledook.example.spark.dto.Result
import com.github.grambbledook.example.spark.handler.traits.HandlerMixin
import com.github.grambbledook.example.spark.service.AccountService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.Request

class GetAccountInfoHandler(private val accountService: AccountService, override val mapper: ObjectMapper) : HandlerMixin<Long> {

    override val logger: Logger = LoggerFactory.getLogger(GetAccountInfoHandler::class.java)

    override fun getValue(request: Request): Long = request.params("id").toLong()

    override fun process(id: Long): Result {
        logger.trace("Get Account Info request for id [$id]")
        return performAction {
            accountService.getInfo(id)
        }.onFailure {
            logger.error("An error occurred on querying account [$id] info", it)
        }.recover { generateErrorResponse(it) }.get()
    }
}