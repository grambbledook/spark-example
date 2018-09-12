package com.github.grambbledook.example.spark.handler.traits

import com.github.grambbledook.example.spark.dto.*
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
                is Success<*> -> {
                    status(200)
                    body(toString(result.payload!!))
                }
                is Failure -> {
                    status(200)
                    body(toString(result))
                }
                is Error -> {
                    status(500)
                    body(toString(result))
                }
            }
        }.body()
    }

    fun getValue(request: Request): T

    fun process(value: T): Result

    fun performAction(action: () -> Try<Account>): Try<Result> {
        return action().map { Success<Account>(it) }
    }

    fun generateErrorResponse(it: Throwable): Result {
        return when (it) {
            is AccountError -> Failure(it.code, it.message)
            else -> Error(it.message)
        }
    }

}