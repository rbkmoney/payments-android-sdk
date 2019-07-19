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
import money.rbk.data.serialization.SealedDistributor
import money.rbk.data.serialization.SealedDistributorValue
import kotlin.reflect.KClass

internal sealed class PaymentToolDetails {

    companion object {
        val DISTRIBUTOR = SealedDistributor("detailsType", DetailsType.values())
    }

    abstract val paymentInfo: String

    class BankCard(

        val cardNumberMask: String,

        @SerializedName("first6", alternate = ["bin"])
        val first6: String?,
        @SerializedName("last4", alternate = ["lastDigits"])
        val last4: String?,

        val paymentSystem: String,

        val tokenProvider: TokenProvider?

    ) : PaymentToolDetails() {

        override val paymentInfo: String
            get() = "$paymentSystem ••${last4.orEmpty()}"

    }

    private enum class DetailsType(override val kClass: KClass<out PaymentToolDetails>) :
        SealedDistributorValue<PaymentToolDetails> {
        PaymentToolDetailsBankCard(BankCard::class)
    }
}
