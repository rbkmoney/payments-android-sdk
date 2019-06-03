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

internal data class Error(
    val errorCode: ErrorCode,
    val id: String? = null,
    val description: String? = null,
    val parameter: String? = null,
    val retryAfter: Int? = null
)

internal enum class ErrorCode {
    INVALID_REQUEST,
    NOT_SUPPORTED,
    INVALID_CREDENTIALS,
    FORBIDDEN,
    INTERNAL_SERVER_ERROR,
    TECHNICAL_ERROR,
    INVALID_SCOPE,
    INVALID_LOGIN,
    INVALID_TOKEN,
    INVALID_SIGNATURE,
    SYNTAX_ERROR,
    ILLEGAL_PARAMETERS,
    ILLEGAL_HEADERS,
    INVALID_CONTEXT,
    CREATE_TIMEOUT_NOT_EXPIRED,
    SESSIONS_EXCEEDED,
    UNSUPPORTED_AUTH_TYPE,
    VERIFY_ATTEMPTS_EXCEEDED,
    INVALID_ANSWER,
    SESSION_DOES_NOT_EXIST,
    SESSION_EXPIRED,
    ACCOUNT_NOT_FOUND,
    AUTH_REQUIRED,
    AUTH_EXPIRED,
    UNKNOWN
}