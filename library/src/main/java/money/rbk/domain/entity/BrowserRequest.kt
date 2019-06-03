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

package money.rbk.domain.entity

import org.json.JSONObject
import money.rbk.data.extension.findEnumOrNull
import money.rbk.data.extension.parseList
import money.rbk.data.serialization.Deserializer

sealed class BrowserRequest(private val requestType: BrowserRequestType,  open val uriTemplate: String) {

    companion object : Deserializer<JSONObject, BrowserRequest> {
        override fun fromJson(json: JSONObject): BrowserRequest {
            val type = json.getString("requestType")
            return when (findEnumOrNull<BrowserRequestType>(type)) {
                BrowserRequestType.BrowserGetRequest -> BrowserGetRequest(
                    uriTemplate = json.getString("uriTemplate")
                )
                BrowserRequestType.BrowserPostRequest -> BrowserPostRequest(
                    uriTemplate = json.getString("uriTemplate"),
                    form = json.parseList("form", UserInteractionForm)
                )
                null -> throw UnknownBrowserRequestTypeException(type)
            }
        }
    }

    data class BrowserGetRequest(
        override val uriTemplate: String
    ) : BrowserRequest(BrowserRequestType.BrowserGetRequest, uriTemplate)

    data class BrowserPostRequest(
        override val uriTemplate: String,
        val form: List<UserInteractionForm>
    ) : BrowserRequest(BrowserRequestType.BrowserPostRequest, uriTemplate)

    private enum class BrowserRequestType {
        BrowserGetRequest,
        BrowserPostRequest
    }
}

class UnknownBrowserRequestTypeException(val type: String) :
    Throwable("Unknown browser request type: $type")