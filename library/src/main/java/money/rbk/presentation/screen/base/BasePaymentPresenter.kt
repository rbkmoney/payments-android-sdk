package money.rbk.presentation.screen.base

import androidx.annotation.CallSuper
import money.rbk.R
import money.rbk.data.exception.NetworkException
import money.rbk.data.utils.log
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

    abstract val canUseAnotherCard: Boolean

    @CallSuper
    open fun onCheckoutUpdated(checkoutInfo: CheckoutInfoModel, action: RepeatAction) {
        val view = view ?: return
        view.hideProgress()
        
        when (val checkoutState = checkoutInfo.checkoutState) {
            is CheckoutStateModel.Success ->
                navigator.openSuccessFragment(R.string.label_payed_by_card_f,
                    checkoutState.paymentToolName,
                    checkoutState.email)

            is CheckoutStateModel.PaymentFailed ->
                navigator.openErrorFragment(
                    messageRes = checkoutState.reasonResId,
                    useAnotherCard = canUseAnotherCard,
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
                    repeatAction = action,
                    allPaymentMethods = true
                )

            is CheckoutStateModel.BrowserRedirectInteraction ->
                view.showRedirect(checkoutState.request)
        }
    }

    @CallSuper
    open fun onPaymentError(error: Throwable) {
        log(error)
        (view ?: return).hideProgress()

        return when (error) {
            is UseCaseException.UnableRepeatPaymentException -> // TODO: Why error_connection?
                navigator.openErrorFragment(
                    messageRes = R.string.error_connection,
                    useAnotherCard = canUseAnotherCard,
                    allPaymentMethods = true)

            is NetworkException -> {
                alert = navigator.showAlert(R.string.label_error,
                    R.string.error_connection,
                    R.string.label_retry to { retryPayment() },
                    R.string.label_cancel to {
                        navigator.openErrorFragment(
                            messageRes = R.string.error_connection,
                            repeatAction = RepeatAction.PAYMENT,
                            useAnotherCard = canUseAnotherCard,
                            allPaymentMethods = true)
                    }
                )
            }

            else ->
                navigator.openErrorFragment(
                    messageRes = R.string.error_busines_logic,
                    repeatAction = RepeatAction.PAYMENT,
                    useAnotherCard = canUseAnotherCard,
                    allPaymentMethods = true)
        }
    }

    @CallSuper
    open fun onCheckoutUpdateError(error: Throwable) {
        log(error)
        (view ?: return).hideProgress()

        return when (error) {
            is UseCaseException.PollingTimeExceededException ->
                navigator.openErrorFragment(
                    messageRes = R.string.error_polling_time_exceeded,
                    repeatAction = RepeatAction.CHECKOUT,
                    allPaymentMethods = true
                )
            is NetworkException ->
                alert = navigator.showAlert(R.string.label_error,
                    R.string.error_connection,
                    R.string.label_retry to {
                        updateCheckout()
                    },
                    R.string.label_cancel to {
                        navigator.openErrorFragment(
                            messageRes = R.string.error_connection,
                            repeatAction = RepeatAction.PAYMENT,
                            useAnotherCard = canUseAnotherCard,
                            allPaymentMethods = true)
                    }
                )
            else ->
                navigator.openErrorFragment(
                    messageRes = R.string.error_busines_logic,
                    repeatAction = RepeatAction.CHECKOUT,
                    useAnotherCard = canUseAnotherCard, // TODO: А проводился ли платёж вообще?
                    allPaymentMethods = true)
        }
    }

    override fun onViewDetached() {
        alert?.dismiss()
        alert = null
        super.onViewDetached()
    }

}
