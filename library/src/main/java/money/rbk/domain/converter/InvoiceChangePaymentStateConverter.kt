///*
// *
// * Copyright 2019 RBKmoney
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// *
// */
//
//package money.rbk.domain.converter
//
//import androidx.annotation.StringRes
//import money.rbk.R
//import money.rbk.domain.entity.InvoiceChange
//import money.rbk.domain.entity.PaymentError
//import money.rbk.domain.entity.PaymentStatus
//import money.rbk.domain.entity.UserInteraction
//import money.rbk.presentation.model.BrowserRequestModel
//import money.rbk.presentation.model.PaymentStateModel
//
//internal class InvoiceChangePaymentStateConverter(
//    private val redirectConverter: EntityConverter<UserInteraction.Redirect, BrowserRequestModel> =
//        RedirectBrowserRequestConverter
//) :
//    EntityConverter<InvoiceChange, PaymentStateModel?> {
//
//    override fun invoke(entity: InvoiceChange): PaymentStateModel? = when (entity) {
//
//        is InvoiceChange.PaymentStarted -> PaymentStateModel.Pending
//        is InvoiceChange.PaymentStatusChanged -> entity.process()
//        is InvoiceChange.PaymentInteractionRequested -> entity.process()
//
//        is InvoiceChange.InvoiceCreated,
//        is InvoiceChange.InvoiceStatusChanged,
//        InvoiceChange.Refund -> null
//    }
//
//    private fun InvoiceChange.PaymentInteractionRequested.process(): PaymentStateModel =
//        when (userInteraction) {
//            is UserInteraction.Redirect ->
//                PaymentStateModel.BrowserRedirectInteraction(redirectConverter(userInteraction))
//        }
//
//    private fun InvoiceChange.PaymentStatusChanged.process(): PaymentStateModel =
//        when (status) {
//
//            PaymentStatus.pending -> PaymentStateModel.Pending
//            PaymentStatus.processed -> PaymentStateModel.Pending
//
//            PaymentStatus.captured -> PaymentStateModel.Success
//            PaymentStatus.refunded -> PaymentStateModel.Success
//
//            PaymentStatus.cancelled -> PaymentStateModel.Cancelled
//
//            PaymentStatus.failed -> PaymentStateModel.Failed(error.errorText())
//
//            PaymentStatus.unknown -> PaymentStateModel.Unknown
//        }
//
//    @StringRes
//    private fun PaymentError?.errorText(): Int =
//        when (this?.code) {
//            PaymentError.Code.InvalidPaymentTool -> R.string.error_invalid_payment_tool
//            PaymentError.Code.AccountLimitsExceeded -> R.string.error_account_limits_exceeded
//            PaymentError.Code.InsufficientFunds -> R.string.error_insufficient_funds
//            PaymentError.Code.PreauthorizationFailed -> R.string.error_preauthorization_failed
//            PaymentError.Code.RejectedByIssuer -> R.string.error_rejected_by_issuer
//            PaymentError.Code.PaymentRejected -> R.string.error_payment_rejected
//            PaymentError.Code.Unknown -> R.string.error_payment_failed
//            null -> R.string.error_payment_failed
//        }
//}
