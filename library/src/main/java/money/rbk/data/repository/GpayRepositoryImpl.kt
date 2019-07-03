package money.rbk.data.repository

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.CardRequirements
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.TransactionInfo
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import money.rbk.domain.entity.Currency
import money.rbk.domain.repository.GpayRepository

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 02.07.19
 */
internal class GpayRepositoryImpl(
    private val applicationContext: Context,
    private val useTestEnvironment: Boolean = false
) : GpayRepository {

    companion object Constants {

        const val GATEWAY = "rbkmoney"
        const val TEST_GATEWAY_MERCHANT_ID = "rbkmoney-test"

        val SUPPORTED_PAYMENT_METHODS = listOf(
            WalletConstants.PAYMENT_METHOD_CARD,
            WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD
        )

        val SUPPORTED_NETWORKS = listOf(
            WalletConstants.CARD_NETWORK_VISA,
            WalletConstants.CARD_NETWORK_AMEX,
            WalletConstants.CARD_NETWORK_MASTERCARD,
            WalletConstants.CARD_NETWORK_DISCOVER,
            WalletConstants.CARD_NETWORK_INTERAC,
            WalletConstants.CARD_NETWORK_JCB
        )

        private const val KEY_GATEWAY = "gateway"
        private const val KEY_GATEWAY_MERCHANT_ID = "gatewayMerchantId"
    }

    override lateinit var gatewayMerchantId: String

    private val environment: Int =
        if (useTestEnvironment) WalletConstants.ENVIRONMENT_TEST else WalletConstants.ENVIRONMENT_PRODUCTION

    private val paymentsClient: PaymentsClient by lazy {
        Wallet.getPaymentsClient(
            applicationContext,
            Wallet.WalletOptions.Builder()
                .setEnvironment(environment)
                .build())
    }

    override fun init(shopId: String) {
        gatewayMerchantId = if (useTestEnvironment) TEST_GATEWAY_MERCHANT_ID else shopId
    }

    override fun checkReadyToPay(): Task<Boolean> =
        paymentsClient.isReadyToPay(buildReadyRequest())

    override fun loadPaymentData(price: String, currency: Currency): Task<PaymentData> {

        val transactionInfo = TransactionInfo.newBuilder()
            .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
            .setTotalPrice(price)
            .setCurrencyCode(currency.name)
            .build()

        val tokenParams = PaymentMethodTokenizationParameters.newBuilder()
            .setPaymentMethodTokenizationType(WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_PAYMENT_GATEWAY)
            .addParameter(KEY_GATEWAY, GATEWAY)
            .addParameter(KEY_GATEWAY_MERCHANT_ID, gatewayMerchantId)
            .build()

        val request = PaymentDataRequest.newBuilder()
            .setPhoneNumberRequired(false)
            .setTransactionInfo(transactionInfo)
            .addAllowedPaymentMethods(SUPPORTED_PAYMENT_METHODS)
            .setCardRequirements(
                CardRequirements.newBuilder()
                    .addAllowedCardNetworks(SUPPORTED_NETWORKS)
                    .setAllowPrepaidCards(false)
                    .setBillingAddressRequired(true)
                    .setBillingAddressFormat(WalletConstants.BILLING_ADDRESS_FORMAT_FULL)
                    .build())
            .setPaymentMethodTokenizationParameters(tokenParams)
            .setUiRequired(true)
            .build()

        return paymentsClient.loadPaymentData(request)
    }

    /* Private test methods */

    private fun buildReadyRequest() =
        IsReadyToPayRequest.newBuilder()
            .addAllowedPaymentMethods(SUPPORTED_PAYMENT_METHODS)
            .addAllowedCardNetworks(SUPPORTED_NETWORKS)
            .build()

}
