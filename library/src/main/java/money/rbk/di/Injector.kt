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

package money.rbk.di

import android.content.Context
import com.google.android.gms.security.ProviderInstaller
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import money.rbk.data.network.Constants
import money.rbk.data.repository.CheckoutRepositoryImpl
import money.rbk.data.repository.GpayRepositoryImpl
import money.rbk.data.serialization.SealedJsonDeserializer
import money.rbk.data.utils.ClientInfoUtils
import money.rbk.data.utils.log
import money.rbk.domain.entity.BrowserRequest
import money.rbk.domain.entity.Flow
import money.rbk.domain.entity.InvoiceChange
import money.rbk.domain.entity.Payer
import money.rbk.domain.entity.PaymentMethod
import money.rbk.domain.entity.PaymentTool
import money.rbk.domain.entity.PaymentToolDetails
import money.rbk.domain.entity.UserInteraction
import money.rbk.domain.repository.CheckoutRepository
import money.rbk.domain.repository.GpayRepository
import okhttp3.CertificatePinner
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

internal object Injector {

    private const val DEFAULT_TIMEOUT: Long = 30

    internal lateinit var checkoutRepository: CheckoutRepository
    internal lateinit var gpayRepository: GpayRepository

    private lateinit var okHttpClient: OkHttpClient

    val gson: Gson by lazy {
        GsonBuilder()

            .registerTypeAdapter(PaymentMethod::class.java,
                SealedJsonDeserializer(PaymentMethod.DISTRIBUTOR))

            .registerTypeAdapter(BrowserRequest::class.java,
                SealedJsonDeserializer(BrowserRequest.DISTRIBUTOR))

            .registerTypeAdapter(PaymentToolDetails::class.java,
                SealedJsonDeserializer(PaymentToolDetails.DISTRIBUTOR))

            .registerTypeAdapter(PaymentTool::class.java,
                SealedJsonDeserializer(PaymentTool.DISTRIBUTOR))

            .registerTypeAdapter(Payer::class.java,
                SealedJsonDeserializer(Payer.DISTRIBUTOR))

            .registerTypeAdapter(Flow::class.java,
                SealedJsonDeserializer(Flow.DISTRIBUTOR))

            .registerTypeAdapter(UserInteraction::class.java,
                SealedJsonDeserializer(UserInteraction.DISTRIBUTOR))

            .registerTypeAdapter(InvoiceChange::class.java,
                SealedJsonDeserializer(InvoiceChange.DISTRIBUTOR))

            .create()
    }

    var email: String? = null

    val shopName: String
        get() = checkoutRepository.shopName

    fun init(applicationContext: Context,
        invoiceId: String,
        invoiceAccessToken: String,
        shopName: String,
        useTestEnvironment: Boolean,
        email: String?) {
        this.email = email
        try {
            ProviderInstaller.installIfNeeded(applicationContext)
        } catch (e: Exception) {
            log(e)
        }

        ClientInfoUtils.initialize(applicationContext)
        okHttpClient = newHttpClient(applicationContext)
        checkoutRepository =
            CheckoutRepositoryImpl(okHttpClient, invoiceId, invoiceAccessToken, shopName)
        gpayRepository = GpayRepositoryImpl(applicationContext, useTestEnvironment)
    }

    private fun newHttpClient(context: Context): OkHttpClient = OkHttpClient.Builder()
        .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .connectionPool(ConnectionPool(4, 10L, TimeUnit.MINUTES))
        .followSslRedirects(false)
        .followRedirects(false)
        .addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        })
        .certificatePinner(
            CertificatePinner.Builder()
                .add(Constants.HOST, *Constants.CERTS)
                .build()
        )
        // .addUserAgent(context)
        // .applyLogging()
        .build()

}
