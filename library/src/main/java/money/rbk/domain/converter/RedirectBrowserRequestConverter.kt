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

package money.rbk.domain.converter

import money.rbk.data.extension.encode
import money.rbk.domain.entity.BrowserRequest
import money.rbk.domain.entity.UserInteraction
import money.rbk.domain.entity.UserInteractionForm
import money.rbk.presentation.model.BrowserRequestModel

internal const val TERM_URI = "internal://termination.url"

private const val KEY_TERM_URI = "termination_uri"

internal object RedirectBrowserRequestConverter :
    EntityConverter<UserInteraction.Redirect, BrowserRequestModel> {

    override fun invoke(entity: UserInteraction.Redirect): BrowserRequestModel =
        when (entity.request) {
            is BrowserRequest.BrowserGetRequest ->
                BrowserRequestModel(false, processedString(entity.request.uriTemplate, TERM_URI.encode()))
            is BrowserRequest.BrowserPostRequest ->
                BrowserRequestModel(true, processedString(entity.request.uriTemplate, TERM_URI.encode()),
                    buildPostData(entity.request.form))
        }

    private fun buildPostData(entity: List<UserInteractionForm>): ByteArray =
        entity
            .joinToString(separator = "&", transform = { form ->
                val template = processedString(form.template, TERM_URI)
                "${form.key.encode()}=${template.encode()}"
            })
            .toByteArray()

    private fun processedString(template: String, terminationURI: String): String =
        template
            .replace("{$KEY_TERM_URI}", terminationURI)
            .replace("{+$KEY_TERM_URI}", terminationURI)
            .replace("{#$KEY_TERM_URI}", "#${terminationURI}")
            .replace("{.$KEY_TERM_URI}", ".${terminationURI}")
            .replace("{/$KEY_TERM_URI}", "/${terminationURI}")
            .replace("{?$KEY_TERM_URI}", "?$KEY_TERM_URI=${terminationURI}")
            .replace("{&$KEY_TERM_URI}", "&$KEY_TERM_URI=${terminationURI}")
            .replace("{;$KEY_TERM_URI}", ";$KEY_TERM_URI=${terminationURI}")

}
