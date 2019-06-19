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

package money.rbk.domain.repository

import money.rbk.data.response.CreatePaymentResponse
import money.rbk.domain.entity.ContactInfo
import money.rbk.domain.entity.Invoice
import money.rbk.domain.entity.InvoiceEvent
import money.rbk.domain.entity.PaymentMethod
import money.rbk.domain.entity.PaymentTool

internal interface CheckoutRepository {

    val shopName: String

    fun loadInvoice(): Invoice

    fun loadPaymentMethods(): List<PaymentMethod>

    fun getPaymentMethodsSync(): List<PaymentMethod>?

    fun createPayment(
        paymentTool: PaymentTool,
        contactInfo: ContactInfo): CreatePaymentResponse

    fun loadInvoiceEvents(): List<InvoiceEvent>

}