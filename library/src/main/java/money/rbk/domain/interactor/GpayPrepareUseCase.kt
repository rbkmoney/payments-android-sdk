/*
 *
 * Copyright 2019 RBKmoney
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package money.rbk.domain.interactor

import com.google.android.gms.common.api.ApiException
import money.rbk.data.exception.GpayException
import money.rbk.di.Injector
import money.rbk.domain.interactor.base.UseCase
import money.rbk.domain.interactor.input.CheckoutStateInputModel
import money.rbk.domain.repository.GpayRepository
import money.rbk.presentation.model.CheckoutInfoModel
import money.rbk.presentation.model.GpayPrepareInfoModel

internal class GpayPrepareUseCase(
    private val gpayRepository: GpayRepository = Injector.gpayRepository,
    private val checkoutStateUseCase: UseCase<CheckoutStateInputModel, CheckoutInfoModel> = CheckoutStateUseCase()
) : UseCase<CheckoutStateInputModel, GpayPrepareInfoModel>() {

    override fun invoke(inputModel: CheckoutStateInputModel,
        onResultCallback: (GpayPrepareInfoModel) -> Unit,
        onErrorCallback: (Throwable) -> Unit) {

        val onCheckoutInfoCallback = { checkoutInfoModel: CheckoutInfoModel ->
            onResultCallback(GpayPrepareInfoModel(checkoutInfoModel))
        }

        gpayRepository.checkReadyToPay()
            ?.addOnCompleteListener { task ->
                try {
                    val result = task.getResult(ApiException::class.java)
                    if (result == true) {
                        checkoutStateUseCase(inputModel, onCheckoutInfoCallback, onErrorCallback)
                    } else {
                        onErrorCallback(GpayException.GpayNotReadyException)
                    }
                } catch (e: ApiException) {
                    onErrorCallback(e)
                }
            }

    }
}
