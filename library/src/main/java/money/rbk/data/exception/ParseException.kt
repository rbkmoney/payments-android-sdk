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

package money.rbk.data.exception

import org.json.JSONException

sealed class ParseException(message: String, cause: Throwable? = null) : Exception(message) {

    internal data class ResponseParsingException(val stringBody: String, val e: JSONException) :
        ParseException(stringBody, e)

    internal class UnknownFlowTypeException(private val flowType: String) :
        ParseException("Unsupported flow type:$flowType")

    internal class UnknownPayerTypeException(private val payerType: String) :
        ParseException("Unsupported payer type:$payerType")

    internal class UnsupportedPaymentToolDetails(private val detailsType: String) :
        ParseException("Unsupported payment tool details:$detailsType")

    internal class UnsupportedUserInteractionTypeException(private val interactionType: String) :
        ParseException("Unsupported user interaction type:$interactionType")

}