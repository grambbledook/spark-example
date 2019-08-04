package com.github.grambbledook.example.spark.dto.request

import java.math.BigDecimal


interface AccountRequest

interface MoneyOperation {
    val amount: BigDecimal
}

data class GetAccountRequest(val id: Long) : AccountRequest
data class TransferRequest(val from: Long, val to: Long, override val amount: BigDecimal): AccountRequest, MoneyOperation
data class DepositRequest(val id: Long, override val amount: BigDecimal): AccountRequest, MoneyOperation
data class CreateRequest(override val amount: BigDecimal, val owner: String): AccountRequest, MoneyOperation
data class WithdrawRequest(val id: Long, override val amount: BigDecimal): AccountRequest, MoneyOperation