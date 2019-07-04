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

import money.rbk.data.extension.findEnumOrNull
import money.rbk.data.extension.parseList
import money.rbk.data.serialization.Deserializer
import money.rbk.data.serialization.SealedDistributor
import money.rbk.data.serialization.SealedDistributorValue
import org.json.JSONObject
import kotlin.reflect.KClass

sealed class BrowserRequest(open val uriTemplate: String) {

    companion object : Deserializer<JSONObject, BrowserRequest> {
        val DISTRIBUTOR = SealedDistributor("requestType", BrowserRequestType.values())

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
    ) : BrowserRequest(uriTemplate)

    data class BrowserPostRequest(
        override val uriTemplate: String,
        val form: List<UserInteractionForm>
    ) : BrowserRequest(uriTemplate)

    private enum class BrowserRequestType(override val kClass: KClass<out BrowserRequest>) :
        SealedDistributorValue<BrowserRequest> {
        BrowserGetRequest(BrowserRequest.BrowserGetRequest::class),
        BrowserPostRequest(BrowserRequest.BrowserPostRequest::class)
    }
}

class UnknownBrowserRequestTypeException(val type: String) :
    Throwable("Unknown browser request type: $type")
