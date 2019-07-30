package com.github.grambbledook.example.spark.dto.response

import java.math.BigDecimal

interface Details

data class AccountDetails(val accountId: Long, val owner: String, val available: BigDecimal)
data class AccountCreatedDetails(val accountId: Long, val available: BigDecimal) : Details
data class AccountDepositDetails(val accountId: Long, val amount: BigDecimal, val available: BigDecimal) : Details
data class AccountWithdrawDetails(val accountId: Long, val amount: BigDecimal, val available: BigDecimal) : Details
data class AccountTransferDetails(val sourceAccountId: Long, val destinationAccountId: Long, val amount: BigDecimal, val available: BigDecimal) : Details