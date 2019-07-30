//package com.github.grambbledook.example.spark.handler
//
//import arrow.core.Left
//import arrow.core.Right
//import com.github.grambbledook.example.spark.domain.Account
//import com.github.grambbledook.example.spark.domain.WorkflowFailure
//import com.github.grambbledook.example.spark.domain.Success
//import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.FIRST
//import com.github.grambbledook.example.spark.handler.HandlerFixture.Companion.SECOND
//import com.github.grambbledook.example.spark.domain.AccountServiceError
//import com.github.grambbledook.example.spark.service.AccountService
//import com.github.grambbledook.example.spark.domain.error.ErrorCode
//import io.mockk.every
//import io.mockk.mockk
//import org.junit.Assert.assertEquals
//import org.junit.Test
//import java.math.BigDecimal
//
//internal typealias TransferRequest = AccountTransferHandler.AccountTransferRequest
//
//@Suppress("UNCHECKED_CAST")
//class AccountTransferHandlerTest : HandlerFixture {
//
//    private val service = mockk<AccountService>()
//    private val handler = AccountTransferHandler(service, mockk())
//
//    @Test
//    fun testTransferResultsInSuccessResult() {
//        every {
//            service.transfer(FIRST, SECOND, BigDecimal(1000.00))
//        }.returns(
//                Right(Account(FIRST, BigDecimal.ZERO, "John doe"))
//        )
//
//        val result = handler.process(TransferRequest(FIRST, SECOND, BigDecimal(1000.00))) as Success<Account>
//
//        assertEquals(FIRST, result.payload.id)
//        assertEquals(BigDecimal.ZERO, result.payload.amount)
//
//    }
//
//    @Test
//    fun testTransferNoMoneyCode() {
//        every {
//            service.transfer(FIRST, SECOND, BigDecimal(1000.00))
//        }.returns(
//                Left(AccountServiceError(ErrorCode.INSUFFICIENT_FUNDS))
//        )
//
//        val result = handler.process(TransferRequest(FIRST, SECOND, BigDecimal(1000.00))) as WorkflowFailure
//
//        assertEquals(ErrorCode.INSUFFICIENT_FUNDS, result.code)
//        assertEquals(null, result.message)
//    }
//
//    @Test
//    fun testAccountNotFound() {
//        every {
//            service.transfer(FIRST, SECOND, BigDecimal(1000.00))
//        }.returns(
//                Left(AccountServiceError(ErrorCode.ACCOUNT_NOT_FOUND))
//        )
//
//        val result = handler.process(TransferRequest(FIRST, SECOND, BigDecimal(1000.00))) as WorkflowFailure
//
//        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, result.code)
//        assertEquals(null, result.message)
//    }
//
//}