package money.rbk.presentation.screen.base

import androidx.annotation.CallSuper
import androidx.appcompat.app.AlertDialog
import money.rbk.R
import money.rbk.data.exception.NetworkException
import money.rbk.data.utils.log
import money.rbk.domain.exception.UseCaseException
import money.rbk.presentation.model.CheckoutInfoModel
import money.rbk.presentation.model.CheckoutStateModel
import money.rbk.presentation.navigation.Navigator

internal abstract class BasePaymentPresenter<T : BasePaymentView>(navigator: Navigator) :
    BasePresenter<T>(navigator) {

    private var alert: AlertDialog? = null
    abstract val canUseAnotherCard: Boolean

    @CallSuper
    open fun onCheckoutUpdated(checkoutInfo: CheckoutInfoModel) {
        val view = view ?: return
        view.hideProgress()

        when (val checkoutState = checkoutInfo.checkoutState) {
            is CheckoutStateModel.Success ->
                navigator.openSuccessFragment(R.string.rbk_label_payed_by_card_f,
                    checkoutState.paymentToolName,
                    checkoutState.email)

            is CheckoutStateModel.PaymentFailed ->
                navigator.openErrorFragment(
                    messageRes = checkoutState.reasonResId,
                    useAnotherCard = canUseAnotherCard,
                    allPaymentMethods = true,
                    repeatAction = checkoutState.canRetry
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
                    messageRes = R.string.rbk_error_polling_time_exceeded,
                    repeatAction = true,
                    allPaymentMethods = true
                )

            is CheckoutStateModel.BrowserRedirectInteraction ->
                view.showRedirect(checkoutState.request)
        }
    }

    @CallSuper
    open fun onPaymentError(error: Throwable, retryPayment: () -> Unit) {
        log(error)
        (view ?: return).hideProgress()

        return when (error) {
            is UseCaseException.UnableRepeatPaymentException ->
                navigator.openErrorFragment(
                    messageRes = R.string.rbk_error_busines_logic,
                    useAnotherCard = canUseAnotherCard,
                    allPaymentMethods = true)

            is NetworkException -> {
                alert = navigator.showAlert(R.string.rbk_label_error,
                    R.string.rbk_error_connection,
                    R.string.rbk_label_retry to { retryPayment() },
                    R.string.rbk_label_cancel to {
                        navigator.openErrorFragment(
                            messageRes = R.string.rbk_error_connection,
                            repeatAction = true,
                            useAnotherCard = canUseAnotherCard,
                            allPaymentMethods = true)
                    }
                )
            }

            else ->
                navigator.openErrorFragment(
                    messageRes = R.string.rbk_error_busines_logic,
                    repeatAction = true,
                    useAnotherCard = canUseAnotherCard,
                    allPaymentMethods = true)
        }
    }

    @CallSuper
    open fun onCheckoutUpdateError(error: Throwable, retryAction: () -> Unit) {
        log(error)
        (view ?: return).hideProgress()

        return when (error) {
            is UseCaseException.PollingTimeExceededException ->
                navigator.openErrorFragment(
                    messageRes = R.string.rbk_error_polling_time_exceeded,
                    repeatAction = true,
                    allPaymentMethods = true
                )
            is NetworkException ->
                alert = navigator.showAlert(R.string.rbk_label_error,
                    R.string.rbk_error_connection,
                    R.string.rbk_label_retry to {
                        retryAction()
                    },
                    R.string.rbk_label_cancel to {
                        navigator.openErrorFragment(
                            messageRes = R.string.rbk_error_connection,
                            repeatAction = true,
                            useAnotherCard = canUseAnotherCard,
                            allPaymentMethods = true)
                    }
                )
            else ->
                navigator.openErrorFragment(
                    messageRes = R.string.rbk_error_busines_logic,
                    repeatAction = true,
                    useAnotherCard = canUseAnotherCard,
                    allPaymentMethods = true)
        }
    }

    override fun onViewDetached() {
        alert?.dismiss()
        alert = null
        super.onViewDetached()
    }

}
