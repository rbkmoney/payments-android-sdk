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
import money.rbk.domain.entity.CardInfo
import money.rbk.domain.entity.ContactInfo
import money.rbk.domain.entity.PaymentMethodToken
import money.rbk.domain.entity.PaymentToken
import money.rbk.domain.entity.PaymentTool

class PaymentInputModel(
    val paymentTool: PaymentTool,
    val contactInfo: ContactInfo
) : BaseInputModel() {

    companion object {

        fun buildForCard(
            cardNumber: String,
            expDate: String,
            cvv: String,
            cardHolder: String,
            email: String?
        ) = PaymentInputModel(
            paymentTool = PaymentTool.CardData(
                cardNumber = cardNumber,
                expDate = expDate,
                cvv = cvv,
                cardHolder = cardHolder
            ),
            contactInfo = ContactInfo(
                email = email
            )
        )

        fun buildForGpay(intent: Intent?,
            email: String,
            gatewayMerchantID: String): PaymentInputModel {
            val paymentData =
                PaymentData.getFromIntent(intent!!) ?: throw RuntimeException("???") //TODO!!!
            val paymentMethodToken =
                paymentData.paymentMethodToken ?: throw RuntimeException("???") //TODO!!!

            //TODO: Make Validation            when(paymentMethodToken.paymentMethodTokenizationType){
            //                WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_PAYMENT_GATEWAY
            //            }

            return PaymentInputModel(
                paymentTool = PaymentTool.TokenizedCardData(
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
                ),
                contactInfo = ContactInfo(
                    email = email
                )
            )

        }
    }
}
