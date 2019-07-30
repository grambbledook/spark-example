package com.github.grambbledook.example.spark.handler.traits

import arrow.core.Either
import arrow.core.Success
import arrow.core.Try
import com.github.grambbledook.example.spark.dto.error.BadRequest.BAD_REQUEST
import com.github.grambbledook.example.spark.dto.Result
import com.github.grambbledook.example.spark.dto.error.ServiceErrorCode.INTERNAL_ERROR
import com.github.grambbledook.example.spark.dto.ServiceFailure
import com.github.grambbledook.example.spark.dto.WorkflowFailure
import com.github.grambbledook.example.spark.dto.WorkflowSuccess
import com.github.grambbledook.example.spark.dto.ServiceError
import spark.Request
import spark.Response
import spark.Route

interface HandlerMixin<I, O> : Route, Logging {

    override fun handle(request: Request, response: Response): Any {
        val value = getValue(request)

        val result = when (value) {
            is Success -> process(value.value)
            else -> WorkflowFailure(BAD_REQUEST, "Unable to parse request.")
        }

        response.apply {
            when (result) {
                is ServiceFailure -> status(500)
                is WorkflowFailure -> status(400)
                else -> status(200)
            }
        }

        return result
    }

    fun getValue(request: Request): Try<I>

    fun process(request: I): Result

    fun performAction(action: () -> Either<ServiceError, O>): Result {
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