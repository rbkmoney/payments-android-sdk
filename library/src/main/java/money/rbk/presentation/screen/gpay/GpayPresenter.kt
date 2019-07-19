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

package money.rbk.presentation.screen.gpay

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.wallet.AutoResolveHelper
import money.rbk.R
import money.rbk.data.exception.GpayException
import money.rbk.data.exception.NetworkException
import money.rbk.data.utils.log
import money.rbk.domain.interactor.CreatePaymentUseCase
import money.rbk.domain.interactor.GpayLoadPaymentDataUseCase
import money.rbk.domain.interactor.GpayPrepareUseCase
import money.rbk.domain.interactor.base.UseCase
import money.rbk.domain.interactor.input.CheckoutStateInputModel
import money.rbk.domain.interactor.input.GpayLoadPaymentDataInputModel
import money.rbk.domain.interactor.input.PaymentInputModel
import money.rbk.presentation.activity.web.Web3DSecureActivity
import money.rbk.presentation.model.CheckoutInfoModel
import money.rbk.presentation.model.GpayPrepareInfoModel
import money.rbk.presentation.model.PaymentDataTaskModel
import money.rbk.presentation.navigation.Navigator
import money.rbk.presentation.screen.base.BasePaymentPresenter
import money.rbk.presentation.utils.isEmailValid

internal class GpayPresenter(
    navigator: Navigator,
    private val createPaymentUseCase: UseCase<PaymentInputModel, CheckoutInfoModel>
    = CreatePaymentUseCase(),
    private val gpayPrepareUseCase: UseCase<CheckoutStateInputModel, GpayPrepareInfoModel>
    = GpayPrepareUseCase(),
    private val gpayLoadPaymentDataUseCase: UseCase<GpayLoadPaymentDataInputModel, PaymentDataTaskModel>
    = GpayLoadPaymentDataUseCase()
) : BasePaymentPresenter<GpayView>(navigator) {

    override val canUseAnotherCard = false

    /* Presenter lifecycle */

    override fun onViewAttached(view: GpayView) {
        updateCheckout(false)
    }

    /* Public methods for view */

    fun onPerformPayment(email: String,
        gpayLoadPaymentDataInputModel: GpayLoadPaymentDataInputModel) {
        if (validateEmail(email)) {
            view?.showProgress()
            gpayLoadPaymentDataUseCase(gpayLoadPaymentDataInputModel,
                { onPaymentDataTaskLoaded(it) },
                { onPaymentDataTaskLoadError(it) })
        }
    }

    fun onGpayPaymentPerformed(resultCode: Int, data: Intent?, email: String) {

        when (resultCode) {
            Activity.RESULT_OK -> {
                if (data != null) {
                    onGpayPaymentSuccess(data, email)
                } else {
                    view?.hideProgress()
                }
            }
            Activity.RESULT_CANCELED -> view?.hideProgress()

            AutoResolveHelper.RESULT_ERROR -> onGpayPaymentError(
                GpayException.GpayCantPerformPaymentException(
                    AutoResolveHelper.getStatusFromIntent(data)
                ))
        }
    }

    fun on3DsPerformed(resultCode: Int) {
        when (resultCode) {
            FragmentActivity.RESULT_OK -> updateCheckout(ignoreBrowserRequest = true)
            FragmentActivity.RESULT_CANCELED -> navigator.finishWithCancel()
            Web3DSecureActivity.RESULT_NETWORK_ERROR ->
                navigator.openErrorFragment(
                    messageRes = R.string.rbk_error_connection,
                    repeatAction = true,
                    useAnotherCard = canUseAnotherCard,
                    allPaymentMethods = true)
        }
    }

    /* Private requests */

    private fun updateCheckout(ignoreBrowserRequest: Boolean) {
        view?.showProgress()
        gpayPrepareUseCase(CheckoutStateInputModel(ignoreBrowserRequest),
            { onGpayPrepared(it) },
            { onCheckoutUpdateError(it) { updateCheckout(ignoreBrowserRequest) } })
    }

    /* Success Callbacks */

    private fun onPaymentDataTaskLoaded(paymentDataTaskModel: PaymentDataTaskModel) {
        navigator.resolveTask(paymentDataTaskModel.paymentDataTask,
            GpayFragment.LOAD_PAYMENT_DATA_REQUEST_CODE)
    }

    private fun onGpayPrepared(gpayPrepareInfo: GpayPrepareInfoModel) {
        val gpayLoadPaymentDataInputModel =
            GpayLoadPaymentDataInputModel(gpayPrepareInfo.checkoutInfoModel.price,
                gpayPrepareInfo.checkoutInfoModel.currency)

        view?.onReadyToPay(gpayLoadPaymentDataInputModel)

        onCheckoutUpdated(gpayPrepareInfo.checkoutInfoModel)
    }

    private fun onGpayPaymentSuccess(data: Intent, email: String) {
        view?.showProgress()
        createPaymentUseCase(PaymentInputModel.PaymentGpay(email, data),
            { onCheckoutUpdated(it) },
            { onPaymentError(it) { onGpayPaymentSuccess(data, email) } })
    }

    /* Errors Handling */
    override fun onCheckoutUpdateError(error: Throwable, retryAction: () -> Unit) =
        when (error) {
            is GpayException.GpayNotReadyException ->
                navigator.openErrorFragment(
                    messageRes = R.string.rbk_error_gpay_initialization,
                    repeatAction = true,
                    allPaymentMethods = true
                )
            else -> super.onCheckoutUpdateError(error, retryAction)
        }

    private fun onGpayPaymentError(gpayException: GpayException.GpayCantPerformPaymentException) {
        log(gpayException)
        navigator.openErrorFragment(
            messageRes = R.string.rbk_error_busines_logic,
            repeatAction = true,
            useAnotherCard = false,
            allPaymentMethods = true)
    }

    private fun onPaymentDataTaskLoadError(throwable: Throwable) {
        log(throwable)
        (view ?: return).hideProgress()
        return when (throwable) {
            is NetworkException -> navigator.openErrorFragment(
                messageRes = R.string.rbk_error_connection,
                repeatAction = true,
                allPaymentMethods = true)

            else -> navigator.openErrorFragment(
                messageRes = R.string.rbk_error_busines_logic,
                repeatAction = true,
                allPaymentMethods = true)
        }
    }

    /* Helper methods */

    private fun validateEmail(email: String): Boolean =
        email.isEmailValid()
            .also { view?.showEmailValid(it) }

}
