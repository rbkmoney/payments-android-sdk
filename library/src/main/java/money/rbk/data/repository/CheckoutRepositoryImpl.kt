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

import money.rbk.data.extension.execute
import money.rbk.data.methods.CreatePayment
import money.rbk.data.methods.CreatePaymentResource
import money.rbk.data.methods.GetInvoiceByID
import money.rbk.data.methods.GetInvoiceEvents
import money.rbk.data.methods.GetInvoicePaymentMethods
import money.rbk.data.methods.GetPayments
import money.rbk.data.response.CreatePaymentResourceResponse
import money.rbk.data.response.CreatePaymentResponse
import money.rbk.data.utils.ClientInfoUtils
import money.rbk.domain.entity.ContactInfo
import money.rbk.domain.entity.Flow
import money.rbk.domain.entity.Invoice
import money.rbk.domain.entity.InvoiceEvent
import money.rbk.domain.entity.Payer
import money.rbk.domain.entity.Payment
import money.rbk.domain.entity.PaymentMethod
import money.rbk.domain.entity.PaymentTool
import money.rbk.domain.repository.CheckoutRepository
import okhttp3.OkHttpClient
import java.util.UUID

internal class CheckoutRepositoryImpl(
    private val okHttpClient: OkHttpClient,
    private val clientInfoUtils: ClientInfoUtils,
    private val invoiceId: String,
    private val invoiceAccessToken: String,
    override val shopName: String
) : CheckoutRepository {

    @Volatile
    override var contactInfo: ContactInfo? = null
    @Volatile
    override var paymentTool: PaymentTool? = null
    @Volatile
    override var paymentId: String? = null
    @Volatile
    override var externalPaymentId: String? = null

    private var invoiceEvents: MutableList<InvoiceEvent> = mutableListOf()

    private val invoice: Invoice by lazy {
        okHttpClient.execute(GetInvoiceByID(invoiceAccessToken, invoiceId))
    }
    private var paymentMethodsInitialized = false
    private val paymentMethods: List<PaymentMethod> by lazy {
        okHttpClient.execute(GetInvoicePaymentMethods(invoiceAccessToken, invoiceId))
            .also { paymentMethodsInitialized = true }
    }

    override fun loadInvoice(): Invoice = invoice

    override fun loadPaymentMethods(): List<PaymentMethod> = paymentMethods

    override fun loadPayments(): List<Payment> =
        okHttpClient.execute(GetPayments(invoiceAccessToken, invoiceId))

    override fun getPaymentMethodsSync(): List<PaymentMethod>? =
        if (paymentMethodsInitialized) paymentMethods else null

    override fun createPayment(paymentTool: PaymentTool,
        contactInfo: ContactInfo): CreatePaymentResponse {

        this.paymentTool = paymentTool
        this.contactInfo = contactInfo

        val createPaymentResource: CreatePaymentResourceResponse =
            okHttpClient.execute(CreatePaymentResource(invoiceAccessToken, clientInfoUtils, paymentTool))

        val externalID = UUID.randomUUID()
            .toString()
        externalPaymentId = externalID

        return okHttpClient.execute(CreatePayment(invoiceId, invoiceAccessToken, externalID,
            Payer.PaymentResourcePayer(
                createPaymentResource.paymentToolToken,
                createPaymentResource.paymentSession,
                contactInfo), Flow.PaymentFlowInstant))
            .also {
                paymentId = it.id
            }
    }

    override fun loadInvoiceEvents(): List<InvoiceEvent> =
        okHttpClient.execute(GetInvoiceEvents(invoiceAccessToken,
            invoiceId,
            invoiceEvents.lastOrNull()?.id))
            .let {
                invoiceEvents.addAll(it)
                invoiceEvents
            }

}
