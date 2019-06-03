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

import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import java.io.IOException

internal class NoInternetException : SdkException()

internal data class RequestExecutionException(val request: Request, val e: IOException) : SdkException(e)

internal data class ResponseReadingException(val response: Response, val e: IOException) : SdkException(e)

internal data class ResponseParsingException(val stringBody: String, val e: JSONException) : SdkException(e)

internal data class ApiMethodException(val error: Error) : SdkException() {
    constructor(errorCode: ErrorCode) : this(
        Error(
            errorCode
        )
    )
}

internal class PassphraseCheckFailedException : SdkException()