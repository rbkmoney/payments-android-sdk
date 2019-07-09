package money.rbk.presentation.screen.base

import androidx.annotation.CallSuper
import money.rbk.R
import money.rbk.domain.exception.UseCaseException
import money.rbk.presentation.model.CheckoutInfoModel
import money.rbk.presentation.model.CheckoutStateModel
import money.rbk.presentation.navigation.Navigator
import money.rbk.presentation.screen.result.RepeatAction

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 09.07.19
 */
abstract class BasePaymentPresenter<T : BasePaymentView>(navigator: Navigator) :
    BasePresenter<T>(navigator) {

    @CallSuper
    open fun onCheckoutUpdated(checkoutInfo: CheckoutInfoModel) {
        view?.hideProgress()

        when (val checkoutState = checkoutInfo.checkoutState) {
            is CheckoutStateModel.Success ->
                navigator.openSuccessFragment(R.string.label_payed_by_card_f,
                    checkoutState.paymentToolName)

            is CheckoutStateModel.PaymentFailed ->
                navigator.openErrorFragment(
                    messageRes = checkoutState.reasonResId,
                    repeatAction = RepeatAction.CHECKOUT,
                    useAnotherCard = true,
                    allPaymentMethods = true
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
                    messageRes = R.string.error_polling_time_exceeded,
                    repeatAction = RepeatAction.CHECKOUT,
                    allPaymentMethods = true
                )

            is CheckoutStateModel.BrowserRedirectInteraction ->
                view?.showRedirect(checkoutState.request)
        }
    }

    @CallSuper
    open fun onPaymentError(error: Throwable) {
        if (error is UseCaseException.UnableRepeatPaymentException) {
            onError(error)
        } else {
            onError(error, RepeatAction.PAYMENT)
        }
    }

    @CallSuper
    open fun onCheckoutUpdateError(error: Throwable) =
        when (error) {
            is UseCaseException.PollingTimeExceededException ->
                navigator.openErrorFragment(
                    messageRes = R.string.error_polling_time_exceeded,
                    repeatAction = RepeatAction.CHECKOUT,
                    allPaymentMethods = true
                )
            else -> onError(error, RepeatAction.CHECKOUT)
        }

}
