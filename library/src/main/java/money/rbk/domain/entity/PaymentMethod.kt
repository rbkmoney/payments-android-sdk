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

import money.rbk.data.serialization.SealedDistributor
import money.rbk.data.serialization.SealedDistributorValue
import kotlin.reflect.KClass

internal sealed class PaymentMethod {

    companion object {
        val DISTRIBUTOR = SealedDistributor("method", Method.values(), Unknown)
    }

    class PaymentMethodBankCard(
        val paymentSystems: List<CreditCardType>,
        val tokenProviders: List<TokenProvider>?
    ) : PaymentMethod()

    object Unknown : PaymentMethod()

    private enum class Method(override val kClass: KClass<out PaymentMethod>) :
        SealedDistributorValue<PaymentMethod> {
        BankCard(PaymentMethodBankCard::class)
    }
}
