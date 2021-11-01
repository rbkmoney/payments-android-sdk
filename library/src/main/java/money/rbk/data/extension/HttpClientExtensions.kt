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

package money.rbk.data.extension

import com.google.gson.Gson
import money.rbk.data.exception.ClientError
import money.rbk.data.exception.InternalServerError
import money.rbk.data.exception.NetworkException.RequestExecutionException
import money.rbk.data.exception.NetworkException.ResponseReadingException
import money.rbk.data.exception.ResponseParsingException
import money.rbk.data.extension.ServerConstants.AUTHORIZATION_BEARER
import money.rbk.data.extension.ServerConstants.HEADER_ACCEPT
import money.rbk.data.extension.ServerConstants.HEADER_ACCEPT_LANGUAGE
import money.rbk.data.extension.ServerConstants.HEADER_AUTHORIZATION
import money.rbk.data.extension.ServerConstants.HEADER_CONTENT_TYPE
import money.rbk.data.extension.ServerConstants.HEADER_USER_AGENT
import money.rbk.data.extension.ServerConstants.HEADER_X_REQUEST_ID
import money.rbk.data.extension.ServerConstants.MIME_APPLICATION_JSON
import money.rbk.data.extension.ServerConstants.MIME_APPLICATION_JSON_UTF8
import money.rbk.data.extension.ServerConstants.clientErrorCodes
import money.rbk.data.extension.ServerConstants.serverErrorCodes
import money.rbk.data.methods.base.ApiRequest
import money.rbk.data.methods.base.PostRequest
import money.rbk.data.network.Constants
import money.rbk.data.utils.log
import money.rbk.di.Injector
import money.rbk.domain.entity.ApiError
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.text.ParseException
import java.util.Locale
import java.util.UUID

private object ServerConstants {
    const val HEADER_ACCEPT = "Accept"
    const val HEADER_CONTENT_TYPE = "Content-Type"
    const val HEADER_ACCEPT_LANGUAGE = "Accept-Language"
    const val HEADER_AUTHORIZATION = "Authorization"
    const val HEADER_X_REQUEST_ID = "X-Request-ID"
    const val HEADER_USER_AGENT = "User-Agent"

    const val MIME_APPLICATION_JSON = "application/json"
    const val MIME_APPLICATION_JSON_UTF8 = "application/json; charset=utf-8"

    const val AUTHORIZATION_BEARER = "Bearer"

    val serverErrorCodes = 500..599
    val clientErrorCodes = 400..499
}

internal fun <T> OkHttpClient.execute(apiRequest: ApiRequest<T>): T {
    val gson = Injector.gson

    val url = "${Constants.BASE_URL}${apiRequest.endpoint}"

    val userAgent = Injector.clientInfoUtils.userAgent

    val requestBuilder = Request.Builder()
        .url(url)
        .addHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
        .addHeader(HEADER_CONTENT_TYPE, MIME_APPLICATION_JSON_UTF8)
        .addHeader(HEADER_ACCEPT_LANGUAGE, getLanguage())
        .addHeader(HEADER_AUTHORIZATION, "$AUTHORIZATION_BEARER ${apiRequest.invoiceAccessToken}")
        .addHeader(HEADER_X_REQUEST_ID, generateRequestId())
        .addHeader(HEADER_USER_AGENT, userAgent)

    apiRequest.headers
        .forEach {
            requestBuilder.addHeader(it.first, it.second)
        }

    if (apiRequest is PostRequest<T>) {
        requestBuilder.post(RequestBody.create(null, createRequestBody(apiRequest.payload, gson)))
    } else {
        requestBuilder.get()
    }

    val request: Request = requestBuilder.build()

    val response: Response = try {
        newCall(request).execute()
    } catch (e: IOException) {
        throw RequestExecutionException(request, e)
    }

    val stringBody: String = try {
        response.body?.string()
    } catch (e: IOException) {
        throw ResponseReadingException(response, e)
    } ?: throw ResponseReadingException(response)

    log(javaClass.name, "[${request.method}] ${request.url}", stringBody)

    when (val code = response.code) {
        in serverErrorCodes -> throw InternalServerError(code)
        in clientErrorCodes -> throw ClientError(code, gson.fromJson(stringBody, ApiError::class.java))
    }

    try {
        val type =
            (apiRequest.javaClass.genericInterfaces[0] as ParameterizedType).actualTypeArguments[0]
        return gson.fromJson(stringBody, type)
    } catch (e: JSONException) {
        throw ResponseParsingException(stringBody, e)
    } catch (e: ParseException) {
        throw ResponseParsingException(stringBody, e)
    }
}

private fun getLanguage() = Locale.getDefault().language

private fun generateRequestId() = UUID.randomUUID().toString().take(10)

internal fun createRequestBody(body: List<Pair<String, Any>>, gson: Gson): String {
    val jsonObject = JSONObject()
    body.forEach { (key, value) ->
        val jsonValue = when (value) {
            is String -> value
            else -> JSONObject(gson.toJson(value))
        }
        jsonObject.put(key, jsonValue)
    }
    return jsonObject.toString()
}
