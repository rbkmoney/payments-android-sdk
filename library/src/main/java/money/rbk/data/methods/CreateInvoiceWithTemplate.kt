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
import money.rbk.data.response.CreateInvoiceWithTemplateResponse
import java.util.UUID


internal data class CreateInvoiceWithTemplate(
    private val invoiceTemplateAccessToken: String,
    private val invoiceTemplateId: String,
    private val shopId: String
) : PostRequest<CreateInvoiceWithTemplateResponse> {

    private val externalID = UUID.randomUUID()
        .toString()

    override fun getUrl(): String =
        Constants.BASE_URL + "/processing/invoice-templates/$invoiceTemplateId/invoices"

    override fun getHeaders(): List<Pair<String, String>> =
        listOf(
            "Authorization" to "Bearer $invoiceTemplateAccessToken",
            "Content-Type" to "application/json; charset=utf-8",
            "X-Request-ID" to System.currentTimeMillis().toString())

    override fun convertJsonToResponse(jsonString: String): CreateInvoiceWithTemplateResponse =
        CreateInvoiceWithTemplateResponse.fromJson(jsonString.toJsonObject())

    override fun getPayload(): List<Pair<String, Any>> = listOf("externalID" to externalID)

    override fun getMimeType(): MimeType = MimeType.JSON

}