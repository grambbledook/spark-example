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

    override fun process(request: Long): Result {
        logger.trace("Get Account Info request for id [$request]")
        return performAction {
            accountService.getInfo(request)
        }
    }

}