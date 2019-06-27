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
//import money.rbk.domain.entity.InvoiceChange
//import money.rbk.domain.entity.InvoiceStatus
//import money.rbk.presentation.model.InvoiceStateModel
//
//internal class InvoiceChangeInvoiceStateConverter :
//    EntityConverter<InvoiceChange, InvoiceStateModel?> {
//
//    override fun invoke(entity: InvoiceChange): InvoiceStateModel? = when (entity) {
//        is InvoiceChange.InvoiceCreated -> InvoiceStateModel.Pending
//        is InvoiceChange.InvoiceStatusChanged -> entity.process()
//        is InvoiceChange.Refund -> InvoiceStateModel.Refund
//
//        is InvoiceChange.PaymentStarted,
//        is InvoiceChange.PaymentStatusChanged,
//        is InvoiceChange.PaymentInteractionRequested -> null
//    }
//
//    private fun InvoiceChange.InvoiceStatusChanged.process(): InvoiceStateModel? =
//        when (status) {
//            InvoiceStatus.unpaid -> InvoiceStateModel.Pending
//            InvoiceStatus.cancelled -> InvoiceStateModel.Cancelled(reason)
//            InvoiceStatus.paid -> InvoiceStateModel.Success(false)
//            InvoiceStatus.fulfilled -> InvoiceStateModel.Success(false)
//            InvoiceStatus.unknown -> InvoiceStateModel.Unknown
//        }
//
//}
