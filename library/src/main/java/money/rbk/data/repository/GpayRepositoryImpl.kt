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

package money.rbk.data.repository

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import money.rbk.domain.entity.Currency
import money.rbk.domain.repository.GpayRepository
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

internal class GpayRepositoryImpl(
    private val applicationContext: Context,
    private val useTestEnvironment: Boolean = false
) : GpayRepository {

    companion object Constants {

        const val GATEWAY = "rbkmoney"
        const val TEST_GATEWAY_MERCHANT_ID = "rbkmoney-test"

        private val baseRequest = JSONObject().apply {
            put("apiVersion", 2)
            put("apiVersionMinor", 0)
        }

        private val allowedCardAuthMethods = JSONArray(
            listOf(
                "PAN_ONLY",
                "CRYPTOGRAM_3DS"
            )
        )

        private val allowedCardNetworks = JSONArray(
            listOf(
                "AMEX",
                "DISCOVER",
                "INTERAC",
                "JCB",
                "MASTERCARD",
                "VISA"
            )
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
                .build()
        )
    }

    override fun init(shopId: String) {
        gatewayMerchantId = if (useTestEnvironment) TEST_GATEWAY_MERCHANT_ID else shopId
    }

    private fun gatewayTokenizationSpecification() = JSONObject().apply {
        put("type", "PAYMENT_GATEWAY")
        put("parameters", JSONObject(mapOf(
                    KEY_GATEWAY to GATEWAY,
                    KEY_GATEWAY_MERCHANT_ID to gatewayMerchantId)))
    }

    private fun baseCardPaymentMethod() = JSONObject().apply {

        val parameters = JSONObject().apply {
            put("allowedAuthMethods", allowedCardAuthMethods)
            put("allowedCardNetworks", allowedCardNetworks)
            put("allowPrepaidCards", false)
            put("billingAddressRequired", true)
            put("billingAddressParameters", buildBillingAddressParameters())
        }

        put("type", "CARD")
        put("parameters", parameters)
    }

    private fun buildBillingAddressParameters() = JSONObject().apply {
        put("format", "FULL")
        put("phoneNumberRequired", true)
    }

    private fun cardPaymentMethod(): JSONObject {
        val cardPaymentMethod = baseCardPaymentMethod()
        cardPaymentMethod.put("tokenizationSpecification", gatewayTokenizationSpecification())

        return cardPaymentMethod
    }

    private fun buildReadyRequest(): JSONObject? {
        return try {
            baseRequest.apply {
                put("allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod()))
            }
        } catch (e: JSONException) {
            null
        }
    }

    override fun checkReadyToPay(): Task<Boolean>? {
        val isReadyToPayJson = buildReadyRequest() ?: return null
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString()) ?: return null

        return paymentsClient.isReadyToPay(request)
    }

    private fun getTransactionInfo(price: String, currency: Currency): JSONObject {
        return JSONObject().apply {
            put("totalPrice", price)
            put("totalPriceStatus", "FINAL")
            put("currencyCode", currency.name)
            put("currencyCode", currency.name)
        }
    }

    private fun getPaymentDataRequest(price: String, currency: Currency): JSONObject? {
        return try {
            baseRequest.apply {
                put("allowedPaymentMethods", JSONArray().put(cardPaymentMethod()))
                put("transactionInfo", getTransactionInfo(price, currency))
            }
        } catch (e: JSONException) {
            null
        }
    }

    override fun loadPaymentData(price: String, currency: Currency): Task<PaymentData>? {
        val paymentDataRequest = getPaymentDataRequest(price, currency) ?: return null
        val request = PaymentDataRequest.fromJson(paymentDataRequest.toString()) ?: return null

        return paymentsClient.loadPaymentData(request)
    }
}
