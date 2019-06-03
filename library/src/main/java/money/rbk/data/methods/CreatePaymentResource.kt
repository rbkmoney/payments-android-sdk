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

package money.rbk.data.methods

import money.rbk.data.extension.toJsonObject
import money.rbk.data.methods.base.MimeType
import money.rbk.data.methods.base.PostRequest
import money.rbk.data.network.Constants
import money.rbk.data.response.CreatePaymentResourceResponse
import money.rbk.domain.entity.ClientInfo
import money.rbk.domain.entity.PaymentTool
import java.util.UUID

internal class CreatePaymentResource(
    private val invoiceAccessToken: String,
    private val paymentTool: PaymentTool
) : PostRequest<CreatePaymentResourceResponse> {

    private val externalID = UUID.randomUUID()
        .toString()

    private val clientInfo: ClientInfo =
        ClientInfo(fingerprint = "123", ip = null) // TODO: Generate Fingerprint

    override fun getUrl(): String =
        Constants.BASE_URL + "/processing/payment-resources"

    override fun getHeaders(): List<Pair<String, String>> =
        listOf(
            "Authorization" to "Bearer $invoiceAccessToken",
            "Content-Type" to "application/json; charset=utf-8",
            "X-Request-ID" to System.currentTimeMillis().toString())

    override fun convertJsonToResponse(jsonString: String): CreatePaymentResourceResponse =
        CreatePaymentResourceResponse.fromJson(jsonString.toJsonObject())

    override fun getPayload(): List<Pair<String, Any>> = listOf(
        "externalID" to externalID,
        "paymentTool" to paymentTool,
        "clientInfo" to clientInfo)

    override fun getMimeType(): MimeType = MimeType.JSON

}