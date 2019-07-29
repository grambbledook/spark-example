package com.github.grambbledook.example.spark.handler.traits

import arrow.core.Either
import arrow.core.Success
import arrow.core.Try
import com.github.grambbledook.example.spark.domain.Account
import com.github.grambbledook.example.spark.domain.error.BadRequest.BAD_REQUEST
import com.github.grambbledook.example.spark.domain.Result
import com.github.grambbledook.example.spark.domain.error.ServiceErrorCode.INTERNAL_ERROR
import com.github.grambbledook.example.spark.domain.ServiceFailure
import com.github.grambbledook.example.spark.domain.WorkflowFailure
import com.github.grambbledook.example.spark.domain.WorkflowSuccess
import com.github.grambbledook.example.spark.domain.ServiceError
import spark.Request
import spark.Response
import spark.Route

interface HandlerMixin<T> : Route, Jackson, Logging {

    override fun handle(request: Request, response: Response): Any {
        val value = getValue(request)

        val result = when (value) {
            is Success -> process(value.value)
            else -> WorkflowFailure(BAD_REQUEST, "Unable to parse request.")
        }

        return response.apply {
            when (result) {
                is ServiceFailure -> status(500)
                is WorkflowFailure -> status(400)
                else -> status(200)
            }

            body(result.asJsonString())
        }.body()
    }

    fun getValue(request: Request): Try<T>

    fun process(request: T): Result

    fun performAction(action: () -> Either<ServiceError, Account>): Result {
        val result = try {
            action()
        } catch (e: Exception) {
            return ServiceFailure(INTERNAL_ERROR, "Server was unable to process request. Please try again later.")
        }

        return result.fold(
                { l -> WorkflowFailure(l.error, l.message) },
                { r -> WorkflowSuccess(r) }
        )
    }

}