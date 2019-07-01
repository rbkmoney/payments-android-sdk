package money.rbk.presentation.screen.gpay

import android.content.Context
import android.content.Intent
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.CardRequirements
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.TransactionInfo
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import money.rbk.R
import money.rbk.data.network.Constants
import money.rbk.domain.exception.UseCaseException
import money.rbk.domain.interactor.CheckoutStateUseCase
import money.rbk.domain.interactor.CreatePaymentUseCase
import money.rbk.domain.interactor.base.UseCase
import money.rbk.domain.interactor.input.EmptyInputModel
import money.rbk.domain.interactor.input.PaymentInputModel
import money.rbk.presentation.model.CheckoutInfoModel
import money.rbk.presentation.model.CheckoutStateModel
import money.rbk.presentation.navigation.Navigator
import money.rbk.presentation.screen.base.BasePresenter
import money.rbk.presentation.screen.card.ACTION_RETRY_PAYMENT
import money.rbk.presentation.screen.card.ACTION_UPDATE_CHECKOUT
import money.rbk.presentation.screen.card.ACTION_USE_ANOTHER_CARD
import money.rbk.presentation.screen.card.BankCardFragment

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 27.06.19
 */
class GpayPresenter(
    navigator: Navigator,
    private val createPaymentUseCase: UseCase<PaymentInputModel, CheckoutInfoModel> = CreatePaymentUseCase(),
    private val invoiceEventsUseCase: UseCase<EmptyInputModel, CheckoutInfoModel> = CheckoutStateUseCase()
) : BasePresenter<GpayView>(navigator) {

    private lateinit var paymentsClient: PaymentsClient

    override fun onViewAttached(view: GpayView) {
        updateCheckout()
    }

    fun init(context: Context, view: GpayView) {
        view.showProgress()
        paymentsClient = Wallet.getPaymentsClient(
            context,
            Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                .build())

        val request = IsReadyToPayRequest.newBuilder()
            .addAllowedPaymentMethods(GpayFragment.SUPPORTED_PAYMENT_METHODS)
            .addAllowedCardNetworks(GpayFragment.SUPPORTED_NETWORKS)
            .build()

        paymentsClient.isReadyToPay(request)
            .addOnCompleteListener { task ->
                try {
                    val result = task.getResult(ApiException::class.java)
                    if (result == true) {
                        view.onReadyToPay()
                    } else {
                        //TODO: Show Error
                    }
                } catch (e: ApiException) {
                    //TODO: Show Error
                    e.printStackTrace()
                }
                view.hideProgress()
            }
    }

    private fun updateCheckout() {
        //        view?.showProgress()
        invoiceEventsUseCase(EmptyInputModel, ::onCheckoutUpdated, ::onCheckoutUpdateError)
    }

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
                    //                    , positiveButtonPair = retryPaymentButton
                )
            is CheckoutStateModel.InvoiceFailed ->
                navigator.openErrorFragment(
                    messageRes = checkoutState.reasonResId)

            is CheckoutStateModel.Warning ->
                navigator.openWarningFragment(checkoutState.titleId,
                    checkoutState.messageResId)

            CheckoutStateModel.Pending -> Unit

            CheckoutStateModel.PaymentProcessing ->
                navigator.openErrorFragment(messageRes = R.string.error_polling_time_exceeded)

            is CheckoutStateModel.BrowserRedirectInteraction ->
                view.showRedirect(checkoutState.request)
        }

        // view.setCost(checkoutInfo.cost)
    }

    fun onGpayPaymentError(data: Intent?) {
        AutoResolveHelper.getStatusFromIntent(data)
            ?.let {
                Log.d(javaClass.name,
                    "-> onGpayPaymentError ->" +
                        "message: ${it.statusMessage} " +
                        "statusCode=${it.statusCode}")
            }
    }

    fun onGpayPaymentSuccess(data: Intent?, email: String) {
        view?.showProgress()
        createPaymentUseCase(PaymentInputModel.buildForGpay(data, email),
            ::onCheckoutUpdated,
            ::onPaymentError)
    }

    private fun onPaymentError(error: Throwable) {

        if (error is UseCaseException.UnableRepeatPaymentException) {
            onError(error) // TODO: Add use another card
        } else {
            onError(error, ACTION_RETRY_PAYMENT) // TODO: Add use another card
        }
    }

    fun performPayment() {
        view?.showProgress()

        val transactionInfo = TransactionInfo.newBuilder()
            .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
            .setTotalPrice("1.11")
            .setCurrencyCode("RUB")
            .build()

        val tokenParams = PaymentMethodTokenizationParameters.newBuilder()
            .setPaymentMethodTokenizationType(WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_PAYMENT_GATEWAY)
            .addParameter("gateway", Constants.GATEWAY)
            .addParameter("gatewayMerchantId", Constants.GATEWAY_MERCHANT_ID)
            .build()

        val request = PaymentDataRequest.newBuilder()
            .setPhoneNumberRequired(false)
            .setTransactionInfo(transactionInfo)
            .addAllowedPaymentMethods(GpayFragment.SUPPORTED_PAYMENT_METHODS)
            .setCardRequirements(
                CardRequirements.newBuilder()
                    .addAllowedCardNetworks(GpayFragment.SUPPORTED_NETWORKS)
                    .setAllowPrepaidCards(false)
                    .setBillingAddressRequired(true)
                    .setBillingAddressFormat(WalletConstants.BILLING_ADDRESS_FORMAT_FULL)
                    .build())
            .setPaymentMethodTokenizationParameters(tokenParams)
            .setUiRequired(true)
            .build()

        navigator.resolveTask(paymentsClient.loadPaymentData(request),
            GpayFragment.LOAD_PAYMENT_DATA_REQUEST_CODE)

    }

}
