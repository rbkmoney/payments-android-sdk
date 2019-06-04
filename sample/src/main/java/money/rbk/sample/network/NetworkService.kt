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

import com.squareup.moshi.Moshi
import money.rbk.sample.BuildConfig
import money.rbk.sample.network.model.InvoiceResponse
import money.rbk.sample.network.model.InvoiceTemplateResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 04.06.19
 */
object NetworkService {

    private const val DEFAULT_TIMEOUT: Long = 180

    private val apiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().build()))
            .client(newHttpClient())
            .build()
            .create(ApiService::class.java)
    }

    private fun newHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    fun createInvoiceWithTemplate(
        invoiceTemplateId: String,
        invoiceTemplateAccessToken: String): Call<InvoiceResponse> =
        apiService.createInvoiceWithTemplate(
            authorization = "Bearer $invoiceTemplateAccessToken",
            invoiceTemplateId = invoiceTemplateId)

    fun getInvoiceTemplateByID(
        invoiceTemplateId: String,
        invoiceTemplateAccessToken: String): Call<InvoiceTemplateResponse> =
        apiService.getInvoiceTemplateByID(
            authorization = "Bearer $invoiceTemplateAccessToken",
            invoiceTemplateId = invoiceTemplateId)

}