package com.github.grambbledook.example.spark.handler.traits

import com.github.grambbledook.example.spark.dto.Account
import com.github.grambbledook.example.spark.dto.Failure
import com.github.grambbledook.example.spark.dto.Result
import com.github.grambbledook.example.spark.dto.Success
import com.github.grambbledook.example.spark.service.AccountError
import io.vavr.control.Try
import spark.Request
import spark.Response
import spark.Route

interface HandlerMixin<T> : Route, Jackson<T>, Logging {

    override fun handle(request: Request, response: Response): Any {
        val value = getValue(request)
        val result: Result = process(value)

        return response.apply {
            when (result) {
                is Success -> {
                    status(200)
                    body(toString(result.payload))
                }
                is Failure -> {
                    status(result.code)
                    body(toString(result.reason ?: ""))
                }
            }
        }.body()
    }

    fun getValue(request: Request): T

    fun process(value: T): Result

    fun performAction(action: () -> Try<Account>): Try<Result> {
        return action().map { Success(it) }
    }

    fun generateErrorResponse(it: Throwable): Result {
        return when (it) {
            is AccountError -> Failure(400, it.message)
            else -> Failure(500, it.cause?.message)
        }
    }

}