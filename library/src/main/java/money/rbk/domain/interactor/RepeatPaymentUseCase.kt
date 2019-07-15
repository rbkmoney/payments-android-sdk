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
//package money.rbk.domain.interactor
//
//import money.rbk.di.Injector
//import money.rbk.domain.exception.UseCaseException
//import money.rbk.domain.interactor.base.UseCase
//import money.rbk.domain.interactor.input.EmptyInputModel
//import money.rbk.domain.interactor.input.PaymentInputModel
//import money.rbk.domain.repository.CheckoutRepository
//import money.rbk.presentation.model.CheckoutInfoModel
//
//internal class RepeatPaymentUseCase(
//    private val repository: CheckoutRepository = Injector.checkoutRepository,
//    private val checkoutStateUseCase: UseCase<PaymentInputModel, CheckoutInfoModel> = CreatePaymentUseCase()
//) : UseCase<EmptyInputModel, CheckoutInfoModel>() {
//
//    override fun invoke(
//        inputModel: EmptyInputModel,
//        onResultCallback: (CheckoutInfoModel) -> Unit,
//        onErrorCallback: (Throwable) -> Unit) {
//
//        val paymentTool = repository.paymentTool
//            ?: return onErrorCallback(UseCaseException.UnableRepeatPaymentException("paymentTool not found"))
//        val contactInfo = repository.contactInfo
//            ?: return onErrorCallback(UseCaseException.UnableRepeatPaymentException("contactInfo not found"))
//
//        checkoutStateUseCase(
//            PaymentInputModel(
//                paymentTool = paymentTool,
//                contactInfo = contactInfo), onResultCallback, onErrorCallback)
//
//    }
//
//}
