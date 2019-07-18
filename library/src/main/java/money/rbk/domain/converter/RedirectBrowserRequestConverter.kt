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

/* TODO: make private */

internal const val TERMINATION_URI = "RBKmoney://success/"
private const val KEY_TERMINATION_URI = "termination_uri"
private const val FORM_KEY_TERM_URL = "TermUrl"

internal object RedirectBrowserRequestConverter :
    EntityConverter<UserInteraction.Redirect, BrowserRequestModel> {

    override fun invoke(entity: UserInteraction.Redirect): BrowserRequestModel =
        when (entity.request) {
            is BrowserRequest.BrowserGetRequest ->
                BrowserRequestModel(false, entity.request.uriTemplate)
            is BrowserRequest.BrowserPostRequest ->
                BrowserRequestModel(true, entity.request.uriTemplate,
                    buildPostData(entity.request.form))
        }

    private fun buildPostData(entity: List<UserInteractionForm>): ByteArray =
        entity
            .joinToString(separator = "&", transform = { form ->
                val template =
                    if (form.key == FORM_KEY_TERM_URL) {
                        form.template.replace("{?$KEY_TERMINATION_URI}",
                            "?$KEY_TERMINATION_URI=${TERMINATION_URI.encode()}")
                    } else {
                        form.template
                    }
                "${form.key}=${template.encode()}"
            })
            .toByteArray()
}
