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
import money.rbk.data.repository.CheckoutRepositoryImpl
import money.rbk.domain.interactor.BaseUseCase
import money.rbk.domain.interactor.InvoiceUseCase
import money.rbk.domain.interactor.PaymentMethodsUseCase
import money.rbk.domain.interactor.PaymentUseCase
import money.rbk.domain.repository.CheckoutRepository
import money.rbk.presentation.exception.UnknownUseCaseException
import money.rbk.presentation.model.BaseIUModel
import money.rbk.presentation.model.InvoiceModel
import money.rbk.presentation.model.PaymentMethodsModel
import money.rbk.presentation.model.PaymentModel
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object Injector {

    private const val DEFAULT_TIMEOUT: Long = 30

    internal lateinit var checkoutRepository: CheckoutRepository

    private lateinit var okHttpClient: OkHttpClient

    fun init(context: Context, invoiceId: String, invoiceAccessToken: String, shopName: String) {
        okHttpClient = newHttpClient(context)
        checkoutRepository = CheckoutRepositoryImpl(invoiceId,
            invoiceAccessToken,
            shopName,
            okHttpClient)
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
        // .applySsl(context)
        // .addUserAgent(context)
        // .applyLogging()
        .build()

    @Suppress("UNCHECKED_CAST")
    internal inline fun <reified T : BaseIUModel> resolveUseCase(): BaseUseCase<T> =
        when (T::class) {
            InvoiceModel::class -> InvoiceUseCase()
            PaymentMethodsModel::class -> PaymentMethodsUseCase()
            PaymentModel::class -> PaymentUseCase()
            else -> throw UnknownUseCaseException(T::class)
        } as BaseUseCase<T>

}