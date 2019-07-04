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

package money.rbk.domain.entity

import com.google.gson.annotations.SerializedName
import money.rbk.data.exception.ParseException
import money.rbk.data.extension.findEnumOrNull
import money.rbk.data.extension.getNullable
import money.rbk.data.extension.parseNullableString
import money.rbk.data.serialization.Deserializer
import money.rbk.data.serialization.SealedDistributor
import money.rbk.data.serialization.SealedDistributorValue
import org.json.JSONObject
import kotlin.reflect.KClass

sealed class PaymentToolDetails {

    abstract val paymentInfo: String

    data class BankCard(

        val cardNumberMask: String,

        @SerializedName("first6", alternate = ["bin"])
        val first6: String?,
        @SerializedName("last4", alternate = ["lastDigits"])
        val last4: String?,

        val paymentSystem: String,
        val tokenProvider: TokenProvider?
    ) : PaymentToolDetails() {

        override val paymentInfo: String = "$paymentSystem ${last4.orEmpty()}"

        companion object : Deserializer<JSONObject, BankCard> {

            override fun fromJson(json: JSONObject): BankCard =
                BankCard(
                    cardNumberMask = json.getString("cardNumberMask"),
                    first6 = json.getNullable("first6") ?: json.getFirst6Legacy(),
                    last4 = json.getNullable("last4") ?: json.getLast4Legacy(),
                    paymentSystem = json.getString("paymentSystem"),
                    tokenProvider = json.parseNullableString("tokenProvider",
                        TokenProvider)
                )

            @Deprecated(message = "Should be removed after API update",
                replaceWith = ReplaceWith(""))
            private fun JSONObject.getFirst6Legacy() = getNullable<String>("bin")

            @Deprecated(message = "Should be removed after API update",
                replaceWith = ReplaceWith(""))
            private fun JSONObject.getLast4Legacy() = getNullable<String>("lastDigits")
        }
    }

    companion object : Deserializer<JSONObject, PaymentToolDetails> {
        val DISTRIBUTOR = SealedDistributor("detailsType", DetailsType.values())

        override fun fromJson(json: JSONObject): PaymentToolDetails {
            val detailsType = json.getString("detailsType")
            return when (findEnumOrNull<DetailsType>(detailsType)) {
                DetailsType.PaymentToolDetailsBankCard -> BankCard.fromJson(json)
                null -> throw ParseException.UnsupportedPaymentToolDetails(detailsType)
            }
        }
    }

    private enum class DetailsType(override val kClass: KClass<out PaymentToolDetails>) :
        SealedDistributorValue<PaymentToolDetails> {
        PaymentToolDetailsBankCard(BankCard::class)
    }
}
