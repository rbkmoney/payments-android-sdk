package money.rbk.presentation.screen.gpay

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.wallet.AutoResolveHelper
import money.rbk.R
import money.rbk.data.exception.GpayException
import money.rbk.domain.interactor.CreatePaymentUseCase
import money.rbk.domain.interactor.GpayLoadPaymentDataUseCase
import money.rbk.domain.interactor.GpayPrepareUseCase
import money.rbk.domain.interactor.base.UseCase
import money.rbk.domain.interactor.input.CheckoutStateInputModel
import money.rbk.domain.interactor.input.EmptyInputModel
import money.rbk.domain.interactor.input.GpayLoadPaymentDataInputModel
import money.rbk.domain.interactor.input.PaymentInputModel
import money.rbk.presentation.model.CheckoutInfoModel
import money.rbk.presentation.model.GpayPrepareInfoModel
import money.rbk.presentation.model.PaymentDataTaskModel
import money.rbk.presentation.navigation.Navigator
import money.rbk.presentation.screen.base.BasePaymentPresenter
import money.rbk.presentation.screen.result.RepeatAction
import money.rbk.presentation.screen.result.ResultAction
import money.rbk.presentation.utils.isEmailValid

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 27.06.19
 */
class GpayPresenter(
    navigator: Navigator,
    private val createPaymentUseCase: UseCase<PaymentInputModel, CheckoutInfoModel> = CreatePaymentUseCase(),
    private val gpayPrepareUseCase: UseCase<CheckoutStateInputModel, GpayPrepareInfoModel> = GpayPrepareUseCase(),
    private val gpayLoadPaymentDataUseCase: UseCase<GpayLoadPaymentDataInputModel, PaymentDataTaskModel> = GpayLoadPaymentDataUseCase()
) : BasePaymentPresenter<GpayView>(navigator) {

    /* Presenter lifecycle */

    private lateinit var gpayLoadPaymentDataInputModel: GpayLoadPaymentDataInputModel
    private lateinit var email: String

    override fun onViewAttached(view: GpayView) {
        when (navigator.getPendingActionAndClean() ?: ResultAction.UPDATE_CHECKOUT) {
            ResultAction.RETRY_PAYMENT -> onPerformPayment(email)
            ResultAction.USE_ANOTHER_CARD -> view.hideProgress()
            ResultAction.UPDATE_CHECKOUT -> updateCheckout(false)
        }
    }

    /* Public methods for view */

    fun onPerformPayment(email: String) {
        if (validateEmail(email)) {
            this.email = email
            view?.showProgress()
            gpayLoadPaymentDataUseCase(gpayLoadPaymentDataInputModel,
                { onPaymentDataTaskLoaded(it) },
                { onPaymentDataTaskLoadError(it) })
        }
    }

    fun onGpayPaymentPerformed(resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> onGpayPaymentSuccess(data, email)
            Activity.RESULT_CANCELED -> view?.hideProgress()
            AutoResolveHelper.RESULT_ERROR -> onGpayPaymentError(
                GpayException.GpayCantPerformPaymentException(
                    AutoResolveHelper.getStatusFromIntent(data)
                ))
        }
    }

    fun on3DsPerformed(resultCode: Int) {
        if (resultCode == FragmentActivity.RESULT_OK) {
            updateCheckout(ignoreBrowserRequest = true)
        } else {
            navigator.finishWithCancel()
        }
    }

    /* Private requests */

    private fun updateCheckout(ignoreBrowserRequest : Boolean) {
        view?.showProgress()
        gpayPrepareUseCase(CheckoutStateInputModel(ignoreBrowserRequest),
            { onGpayPrepared(it) },
            { onCheckoutUpdateError(it) })
    }

    /* Success Callbacks */

    private fun onPaymentDataTaskLoaded(paymentDataTaskModel: PaymentDataTaskModel) {
        navigator.resolveTask(paymentDataTaskModel.paymentDataTask,
            GpayFragment.LOAD_PAYMENT_DATA_REQUEST_CODE)
    }

    private fun onGpayPrepared(gpayPrepareInfo: GpayPrepareInfoModel) {
        view?.onReadyToPay()
        gpayLoadPaymentDataInputModel =
            GpayLoadPaymentDataInputModel(gpayPrepareInfo.checkoutInfoModel.price,
                gpayPrepareInfo.checkoutInfoModel.currency,
                gpayPrepareInfo.gatewayMerchantId)
        onCheckoutUpdated(gpayPrepareInfo.checkoutInfoModel, RepeatAction.CHECKOUT)
    }

    private fun onGpayPaymentSuccess(data: Intent?, email: String) {
        view?.showProgress()
        createPaymentUseCase(PaymentInputModel.buildForGpay(data,
            email,
            gpayLoadPaymentDataInputModel.gatewayMerchantId),
            { onCheckoutUpdated(it, RepeatAction.PAYMENT) },
            { onPaymentError(it) })
    }

    /* Errors Handling */
    override fun onCheckoutUpdateError(error: Throwable) =
        when (error) {
            is GpayException.GpayNotReadyException ->
                navigator.openErrorFragment(
                    messageRes = R.string.error_gpay_initialization,
                    repeatAction = RepeatAction.CHECKOUT,
                    allPaymentMethods = true
                )
            else -> super.onCheckoutUpdateError(error)
        }

    private fun onGpayPaymentError(gpayException: GpayException.GpayCantPerformPaymentException) {
        @Suppress("ConstantConditionIf")
        //        if (BuildConfig.DEBUG) {
        //            gpayException.printStackTrace()
        //        }

        navigator.openErrorFragment(
            messageRes = R.string.error_busines_logic,
            repeatAction = RepeatAction.PAYMENT,
            useAnotherCard = true,
            allPaymentMethods = true)
    }

    private fun onPaymentDataTaskLoadError(throwable: Throwable) =
        onError(throwable, RepeatAction.PAYMENT)

    /* Helper methods */

    private fun validateEmail(email: String): Boolean =
        email.isEmailValid()
            .also { view?.showEmailValid(it) }

}
