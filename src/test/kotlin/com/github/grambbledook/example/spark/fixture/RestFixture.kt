package com.github.grambbledook.example.spark.fixture

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.github.grambbledook.example.spark.dto.request.AccountDepositRequest
import com.github.grambbledook.example.spark.dto.request.AccountTransferRequest
import com.github.grambbledook.example.spark.dto.request.AccountWithdrawRequest
import com.github.grambbledook.example.spark.dto.request.CreateAccountRequest
import com.github.grambbledook.example.spark.dto.response.*
import com.jayway.restassured.RestAssured
import com.jayway.restassured.http.ContentType
import com.jayway.restassured.response.Response
import java.math.BigDecimal

interface RestFixture : SparkFixture, JacksonFixture {

    fun createAccount(owner: String, amount: BigDecimal = BigDecimal.ZERO): Either<ErrorResponse, Receipt<AccountCreatedDetails>> {
        val response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(CreateAccountRequest(amount, owner).toJsonString())
                .post("/accounts")

        return parseResponse(response)
    }

    fun getAccountInfo(id: Long): Either<ErrorResponse, Receipt<AccountDetails>> {
        val response = RestAssured.given()
                .contentType(ContentType.JSON)
                .get("/accounts/$id")

        return parseResponse(response)
    }

    fun deposit(id: Long, amount: BigDecimal): Either<ErrorResponse, Receipt<AccountDepositDetails>> {
        val response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(AccountDepositRequest(id, amount).toJsonString())
                .post("/accounts/deposit")

        return parseResponse(response)
    }

    fun withdraw(id: Long, amount: BigDecimal): Either<ErrorResponse, Receipt<AccountWithdrawDetails>> {
        val response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(AccountWithdrawRequest(id, amount).toJsonString())
                .post("/accounts/withdraw")

        return parseResponse(response)
    }

    fun transfer(from: Long, to: Long, amount: BigDecimal): Either<ErrorResponse, Receipt<AccountTransferDetails>> {
        val response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(AccountTransferRequest(from, to, amount).toJsonString())
                .post("/accounts/withdraw")

        return parseResponse(response)
    }

    private inline fun <reified T : Any> parseResponse(response: Response): Either<ErrorResponse, T> {
        return when (response.statusCode()) {
            200 -> Right(fromJsonString(response.body().asString(), T::class.java))
            else -> Left(fromJsonString(response.body().asString(), ErrorResponse::class.java))
        }
    }

}