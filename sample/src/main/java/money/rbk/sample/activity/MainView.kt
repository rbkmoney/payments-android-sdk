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

package money.rbk.sample.activity

import money.rbk.sample.network.model.InvoiceResponse
import money.rbk.sample.network.model.InvoiceTemplateResponse

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 04.06.19
 */
interface MainView {

    fun initTemplate(templateResponse: InvoiceTemplateResponse)

    fun showInvoiceTemplateError()

    fun startCheckout(invoiceResponse: InvoiceResponse)

    fun showInvoiceCreateError()

    fun hideProgress()

    fun showProgress()

}