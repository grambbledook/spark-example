package com.github.grambbledook.example.spark.dto.response

data class Receipt<T>(val operation: TransactionType,
                      val details: T)