package com.github.grambbledook.example.spark.fixture

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.fasterxml.jackson.core.type.TypeReference
import com.github.grambbledook.example.spark.dto.request.DepositRequest
import com.github.grambbledook.example.spark.dto.request.TransferRequest
import com.github.grambbledook.example.spark.dto.request.WithdrawRequest
import com.github.grambbledook.example.spark.dto.request.CreateRequest
import com.github.grambbledook.example.spark.dto.response.*
import com.jayway.restassured.RestAssured
import com.jayway.restassured.http.ContentType
import com.jayway.restassured.response.Response
import java.math.BigDecimal

interface RestFixture : SparkFixture, JacksonFixture {

    fun createAccount(owner: String, amount: BigDecimal = BigDecimal.ZERO): Either<ErrorResponse, Receipt> {
        val response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(CreateRequest(amount, owner).toJsonString())
                .post("/accounts")

        return parseResponse(response)
    }

    fun getAccountInfo(id: Long): Either<ErrorResponse, Receipt> {
        val response = RestAssured.given()
                .contentType(ContentType.JSON)
                .get("/accounts/$id")

        return parseResponse(response)
    }

    fun deposit(id: Long, amount: BigDecimal): Either<ErrorResponse, Receipt> {
        val response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(DepositRequest(id, amount).toJsonString())
                .post("/accounts/deposit")

        return parseResponse(response)
    }

    fun withdraw(id: Long, amount: BigDecimal): Either<ErrorResponse, Receipt> {
        val response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(WithdrawRequest(id, amount).toJsonString())
                .post("/accounts/withdraw")

        return parseResponse(response)
    }

    fun transfer(from: Long, to: Long, amount: BigDecimal): Either<ErrorResponse, Receipt> {
        val response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(TransferRequest(from, to, amount).toJsonString())
                .post("/accounts/transfer")

        return parseResponse(response)
    }

    private inline fun <reified T : Any> parseResponse(response: Response): Either<ErrorResponse, T> {
        val typeRef = object: TypeReference<T>(){}

        return when (response.statusCode()) {
            200 -> Right(fromJsonString(response.body().asString(), typeRef))
            else -> Left(fromJsonString(response.body().asString(), ErrorResponse::class.java))
        }
    }

}