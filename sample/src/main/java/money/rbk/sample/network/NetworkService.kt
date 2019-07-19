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

import android.app.Application
import com.squareup.moshi.Moshi
import io.reactivex.Observable
import io.reactivex.Single
import money.rbk.sample.BuildConfig
import money.rbk.sample.network.model.InvoiceModel
import money.rbk.sample.network.model.InvoiceTemplateResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class NetworkService(private val app: Application) {

    companion object {
        private const val DEFAULT_TIMEOUT: Long = 180
    }

    private val apiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().build()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(newHttpClient())
            .build()
            .create(ApiService::class.java)
    }

    fun createInvoiceWithTemplate(invoiceTemplateId: String): Single<InvoiceModel> =
        getBuiltInInvoiceTemplates()
            .map { templates -> templates.first { it.id == invoiceTemplateId } }
            .flatMap { invoiceTemplate ->
                apiService.createInvoiceWithTemplate(
                    authorization = "Bearer ${invoiceTemplate.accessToken}",
                    invoiceTemplateId = invoiceTemplateId)
                    .map { response ->
                        InvoiceModel(
                            id = response.invoice.id,
                            shopId = response.invoice.shopID,
                            shopName = invoiceTemplate.shopName,
                            invoiceAccessToken = response.invoiceAccessToken.payload,
                            description = response.invoice.description
                        )
                    }
            }

    fun getInvoices(): Single<List<InvoiceModel>> = apiService.getInvoices()

    fun getInvoiceTemplates(): Single<List<InvoiceTemplateResponse>> =
        getBuiltInInvoiceTemplates()
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMapSingle { invoiceTemplate ->
                apiService.getInvoiceTemplateByID(
                    authorization = "Bearer ${invoiceTemplate.accessToken}",
                    invoiceTemplateId = invoiceTemplate.id)
            }
            .toList()

    private fun newHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor(MockInterceptor(app.assets))
        .build()

    private fun getBuiltInInvoiceTemplates() =
        apiService.getInvoiceTemplates()

}
