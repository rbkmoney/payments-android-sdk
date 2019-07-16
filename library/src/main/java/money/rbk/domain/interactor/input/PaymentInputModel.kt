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

sealed class PaymentInputModel(private val email: String) : BaseInputModel() {

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
            val paymentData = PaymentData.getFromIntent(intent)
            val paymentMethodToken = paymentData?.paymentMethodToken
                ?: throw GpayException.GpayPaymentMethodTokenException

            return PaymentTool.TokenizedCardData(
                gatewayMerchantID = gatewayMerchantID,
                paymentToken = PaymentToken(
                    cardInfo = CardInfo(
                        paymentData.cardInfo.cardNetwork,
                        paymentData.cardInfo.cardDetails,
                        paymentData.cardInfo.cardDescription,
                        when (paymentData.cardInfo.cardClass) {
                            WalletConstants.CARD_CLASS_DEBIT -> CardInfo.CardClass.DEBIT
                            WalletConstants.CARD_CLASS_PREPAID -> CardInfo.CardClass.PREPAID
                            else -> CardInfo.CardClass.CREDIT
                        }
                    ),
                    paymentMethodToken = PaymentMethodToken(
                        tokenizationType = "PAYMENT_GATEWAY",
                        token = paymentMethodToken.token
                    )
                ),
                provider = "GooglePay"
            )
        }
    }

}
