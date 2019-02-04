package com.github.grambbledook.example.spark.handler.traits

import arrow.core.Either
import com.github.grambbledook.example.spark.dto.*
import com.github.grambbledook.example.spark.service.AccountError
import com.github.grambbledook.example.spark.service.ServiceError
import com.github.grambbledook.example.spark.service.UnknownError
import spark.Request
import spark.Response
import spark.Route

interface HandlerMixin<T> : Route, Jackson<T>, Logging {

    override fun handle(request: Request, response: Response): Any {
        val value = getValue(request)
        val result: Result = process(value)

        return response.apply {
            when (result) {
                is Error -> {
                    status(500)
                    body(toString(result))
                }
                else -> {
                    status(200)
                    body(toString(result))
                }
            }
        }.body()
    }

    fun getValue(request: Request): T

    fun process(request: T): Result

    fun performAction(action: () -> Either<ServiceError, Account>): Result {
        return action().fold(
                { l -> generateErrorResponse(l) },
                { r -> Success(r) }
        )
    }

    fun generateErrorResponse(it: ServiceError): Result {
        return when (it) {
            is UnknownError -> Error(it.e)
            is AccountError -> Failure(it.code, null)
        }
    }

}