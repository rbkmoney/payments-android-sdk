package money.rbk.presentation.screen.gpay

import android.content.Intent
import com.google.android.gms.wallet.AutoResolveHelper
import money.rbk.R
import money.rbk.data.exception.GpayException
import money.rbk.domain.exception.UseCaseException
import money.rbk.domain.interactor.CreatePaymentUseCase
import money.rbk.domain.interactor.GpayLoadPaymentDataUseCase
import money.rbk.domain.interactor.GpayPrepareUseCase
import money.rbk.domain.interactor.base.UseCase
import money.rbk.domain.interactor.input.EmptyInputModel
import money.rbk.domain.interactor.input.GpayLoadPaymentDataInputModel
import money.rbk.domain.interactor.input.PaymentInputModel
import money.rbk.presentation.model.CheckoutInfoModel
import money.rbk.presentation.model.CheckoutStateModel
import money.rbk.presentation.model.GpayPrepareInfoModel
import money.rbk.presentation.model.PaymentDataTaskModel
import money.rbk.presentation.navigation.Navigator
import money.rbk.presentation.screen.base.BasePresenter
import money.rbk.presentation.screen.card.ACTION_RETRY_PAYMENT
import money.rbk.presentation.screen.card.ACTION_UPDATE_CHECKOUT
import money.rbk.presentation.screen.card.ACTION_USE_ANOTHER_CARD
import money.rbk.presentation.screen.card.BankCardFragment
import money.rbk.presentation.utils.isEmailValid

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 27.06.19
 */
class GpayPresenter(
    navigator: Navigator,
    private val createPaymentUseCase: UseCase<PaymentInputModel, CheckoutInfoModel> = CreatePaymentUseCase(),
    private val gpayPrepareUseCase: UseCase<EmptyInputModel, GpayPrepareInfoModel> = GpayPrepareUseCase(),
    private val gpayLoadPaymentDataUseCase: UseCase<GpayLoadPaymentDataInputModel, PaymentDataTaskModel> = GpayLoadPaymentDataUseCase()
) : BasePresenter<GpayView>(navigator) {

    /* Presenter lifecycle */

    private lateinit var gpayLoadPaymentDataInputModel: GpayLoadPaymentDataInputModel

    override fun onViewAttached(view: GpayView) {
        updateCheckout()
    }

    override fun onViewDetached() {
        gpayPrepareUseCase.destroy()
        createPaymentUseCase.destroy()
        gpayLoadPaymentDataUseCase.destroy()
    }

    /* Public methods for view */

    fun onPerformPayment(email: String) {
        if (validateEmail(email)) {
            view?.showProgress()
            gpayLoadPaymentDataUseCase(gpayLoadPaymentDataInputModel,
                ::onPaymentDataTaskLoaded,
                ::onPaymentDataTaskLoadError)
        }
    }

    fun onGpayPaymentError(data: Intent?) {
        onError(GpayException.GpayCantPerformPaymentException(AutoResolveHelper.getStatusFromIntent(data)))
    }

    fun onGpayPaymentSuccess(data: Intent?, email: String) {
        view?.showProgress()
        createPaymentUseCase(PaymentInputModel.buildForGpay(data,
            email,
            gpayLoadPaymentDataInputModel.gatewayMerchantId),
            ::onCheckoutUpdated,
            ::onPaymentError)
    }

    fun on3DsPerformed() {
        updateCheckout()
    }

    private fun updateCheckout() {
        view?.showProgress()
        gpayPrepareUseCase(EmptyInputModel, ::onGpayPrepared, ::onCheckoutUpdateError)
    }

    private fun onPaymentDataTaskLoadError(throwable: Throwable) {
        // TODO: Process this error
        onError(throwable) // TODO: Add actions for retry
    }

    private fun onPaymentDataTaskLoaded(paymentDataTaskModel: PaymentDataTaskModel) {
        navigator.resolveTask(paymentDataTaskModel.PaymentDataTask,
            GpayFragment.LOAD_PAYMENT_DATA_REQUEST_CODE)
    }

    private fun onGpayPrepared(gpayPrepareInfo: GpayPrepareInfoModel) {
        view?.onReadyToPay()
        gpayLoadPaymentDataInputModel =
            GpayLoadPaymentDataInputModel(gpayPrepareInfo.checkoutInfoModel.price,
                gpayPrepareInfo.checkoutInfoModel.currency,
                gpayPrepareInfo.gatewayMerchantId)
        onCheckoutUpdated(gpayPrepareInfo.checkoutInfoModel)
    }

    private fun onCheckoutUpdated(checkoutInfo: CheckoutInfoModel) {
        val view = view ?: return
        view.hideProgress()

        when (val checkoutState = checkoutInfo.checkoutState) {
            is CheckoutStateModel.Success ->
                navigator.openSuccessFragment(R.string.label_payed_by_card_f,
                    checkoutState.paymentToolName)

            is CheckoutStateModel.PaymentFailed ->
                navigator.openErrorFragment(
                    messageRes = checkoutState.reasonResId
                    // TODO: Add retry if can
                )
            is CheckoutStateModel.InvoiceFailed ->
                navigator.openErrorFragment(
                    messageRes = checkoutState.reasonResId)

            is CheckoutStateModel.Warning ->
                navigator.openWarningFragment(checkoutState.titleId,
                    checkoutState.messageResId)

            CheckoutStateModel.Pending -> Unit

            CheckoutStateModel.PaymentProcessing ->
                navigator.openErrorFragment(
                    messageRes = R.string.error_polling_time_exceeded
                    // TODO: Add retry if can
                )

            is CheckoutStateModel.BrowserRedirectInteraction ->
                view.showRedirect(checkoutState.request)
        }

    }

    private fun validateEmail(email: String): Boolean =
        email.isEmailValid()
            .also { view?.showEmailValid(it) }

    private fun onCheckoutUpdateError(error: Throwable) {
        error.printStackTrace()

        if (error is UseCaseException) {
            when (error) {
                is UseCaseException.PollingTimeExceededException ->
                    navigator.openErrorFragment(
                        parent = view as BankCardFragment,
                        messageRes = R.string.error_polling_time_exceeded,
                        positiveAction = ACTION_UPDATE_CHECKOUT,
                        negativeAction = ACTION_USE_ANOTHER_CARD)
            }
        } else {
            onError(error, ACTION_UPDATE_CHECKOUT)
        }
    }

    private fun onPaymentError(error: Throwable) {
        if (error is UseCaseException.UnableRepeatPaymentException) {
            onError(error) // TODO: Add use another card
        } else {
            onError(error, ACTION_RETRY_PAYMENT) // TODO: Add use another card
        }
    }

}
