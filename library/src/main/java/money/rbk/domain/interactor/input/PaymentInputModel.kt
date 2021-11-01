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

package money.rbk.domain.interactor.input

import android.content.Intent
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.WalletConstants
import money.rbk.data.exception.GpayException
import money.rbk.domain.entity.CardInfo
import money.rbk.domain.entity.ContactInfo
import money.rbk.domain.entity.PaymentMethodToken
import money.rbk.domain.entity.PaymentToken
import money.rbk.domain.entity.PaymentTool
import org.json.JSONObject

internal sealed class PaymentInputModel(private val email: String) : BaseInputModel() {

    fun getContactInfo() = ContactInfo(email = email)

    class PaymentCard(
        email: String,
        private val cardNumber: String,
        private val expDate: String,
        private val cvv: String,
        private val cardHolder: String) : PaymentInputModel(email) {

        fun getPaymentTool() = PaymentTool.CardData(
            cardNumber = cardNumber,
            expDate = expDate,
            cvv = cvv,
            cardHolder = cardHolder
        )
    }

    class PaymentGpay(
        email: String,
        private val intent: Intent) : PaymentInputModel(email) {

        fun getPaymentTool(gatewayMerchantID: String): PaymentTool {
            val paymentDataFromIntent = PaymentData.getFromIntent(intent)
            val paymentDataJson = paymentDataFromIntent?.toJson()
            val paymentData = if (paymentDataJson.isNullOrEmpty()) null else JSONObject(paymentDataJson)
            val paymentMethodData = paymentData?.getJSONObject("paymentMethodData")
                ?: throw GpayException.GpayPaymentMethodTokenException

            val cardInfo = paymentMethodData.getJSONObject("info")
            val tokenizationData = paymentMethodData.getJSONObject("tokenizationData")

            return PaymentTool.TokenizedCardData(
                gatewayMerchantID = gatewayMerchantID,
                paymentToken = PaymentToken(
                    cardInfo = CardInfo(
                        cardNetwork = cardInfo.getString("cardNetwork"),
                        cardDetails = cardInfo.getString("cardDetails"),
                        cardDescription = paymentMethodData.getString("description"),
                        cardClass = when (paymentDataFromIntent?.cardInfo?.cardClass) {
                            WalletConstants.CARD_CLASS_DEBIT -> CardInfo.CardClass.DEBIT
                            WalletConstants.CARD_CLASS_PREPAID -> CardInfo.CardClass.PREPAID
                            else -> CardInfo.CardClass.CREDIT
                        }
                    ),
                    paymentMethodToken = PaymentMethodToken(
                        tokenizationType = tokenizationData.getString("type"),
                        token = tokenizationData.getString("token")
                    )
                ),
                provider = "GooglePay"
            )
        }
    }

}
