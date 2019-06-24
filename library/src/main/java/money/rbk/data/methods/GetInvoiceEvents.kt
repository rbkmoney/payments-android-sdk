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

import money.rbk.data.extension.toJsonArray
import money.rbk.data.methods.base.GetRequest
import money.rbk.domain.entity.InvoiceEvent

private const val LIMIT = 100

internal class GetInvoiceEvents(
    invoiceAccessToken: String,
    invoiceId: String,
    eventID: Int?
) : GetRequest<List<InvoiceEvent>> {

    override val endpoint = "/processing/invoices/$invoiceId/events?" +
        "limit=$LIMIT" +
        (eventID?.let { "&eventID=$it" } ?: "")

    override val invoiceAccessToken = invoiceAccessToken

    override fun convertJsonToResponse(jsonString: String): List<InvoiceEvent> =
        InvoiceEvent.fromJsonArray(jsonString.toJsonArray())

}
