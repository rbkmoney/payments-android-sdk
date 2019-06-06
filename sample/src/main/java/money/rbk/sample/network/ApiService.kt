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

package money.rbk.sample.network

import io.reactivex.Single
import money.rbk.sample.network.model.EmptyRequest
import money.rbk.sample.network.model.InvoiceResponse
import money.rbk.sample.network.model.InvoiceTemplateResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("processing/invoice-templates/{invoice_template_id}/invoices")
    fun createInvoiceWithTemplate(
        @Header("Authorization") authorization: String,
        @Header("X-Request-ID") requestId: Long = System.currentTimeMillis(),
        @Header("Content-Type") contentType: String = "application/json; charset=utf-8",
        @Path("invoice_template_id") invoiceTemplateId: String,
        @Body request: EmptyRequest = EmptyRequest
    ): Single<InvoiceResponse>

    @GET("processing/invoice-templates/{invoice_template_id}")
    fun getInvoiceTemplateByID(
        @Header("Authorization") authorization: String,
        @Header("X-Request-ID") requestId: Long = System.currentTimeMillis(),
        @Header("Content-Type") contentType: String = "application/json; charset=utf-8",
        @Path("invoice_template_id") invoiceTemplateId: String
    ): Single<InvoiceTemplateResponse>

}