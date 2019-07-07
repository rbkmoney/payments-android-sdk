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

import money.rbk.data.serialization.SealedDistributor
import money.rbk.data.serialization.SealedDistributorValue
import kotlin.reflect.KClass

sealed class BrowserRequest(val uriTemplate: String) {

    companion object {
        val DISTRIBUTOR = SealedDistributor("requestType", BrowserRequestType.values())
    }

    class BrowserGetRequest(
        uriTemplate: String
    ) : BrowserRequest(uriTemplate)

    class BrowserPostRequest(
        uriTemplate: String,
        val form: List<UserInteractionForm>
    ) : BrowserRequest(uriTemplate)

    private enum class BrowserRequestType(override val kClass: KClass<out BrowserRequest>) :
        SealedDistributorValue<BrowserRequest> {
        BrowserGetRequest(BrowserRequest.BrowserGetRequest::class),
        BrowserPostRequest(BrowserRequest.BrowserPostRequest::class)
    }
}
