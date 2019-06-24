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

import money.rbk.data.exception.NetworkServiceException.*
import money.rbk.data.exception.ParseException
import money.rbk.data.methods.base.ApiRequest
import money.rbk.data.methods.base.PostRequest
import money.rbk.data.network.Constants
import money.rbk.data.serialization.Serializable
import money.rbk.data.utils.ClientInfoUtils
import money.rbk.data.utils.log
import money.rbk.domain.entity.ApiError
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.Locale
import java.util.UUID

internal fun <T> OkHttpClient.execute(
    apiRequest: ApiRequest<T>
): T {
    val url = "${Constants.BASE_URL}${apiRequest.endpoint}"

    val userAgent = ClientInfoUtils.userAgent

    val requestBuilder = Request.Builder()
        .url(url)
        .addHeader("Accept", "application/json")
        .addHeader("Content-Type", "application/json; charset=utf-8")
        .addHeader("Accept-Language", Locale.getDefault().language)
        .addHeader("Authorization", "Bearer ${apiRequest.invoiceAccessToken}")
        .addHeader("X-Request-ID", UUID.randomUUID().toString())
        .addHeader("User-Agent", userAgent)

    apiRequest.headers
        .forEach {
            requestBuilder.addHeader(it.first, it.second)
        }

    if (apiRequest is PostRequest<T>) {
        requestBuilder.post(RequestBody.create(null, createRequestBody(apiRequest.payload)))
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
        response.body()!!.string()
    } catch (e: IOException) {
        throw ResponseReadingException(response, e)

    }

    log(javaClass.name, "[${request.method()}] ${request.url()}", stringBody)

    when (val code = response.code()) {
        in 500..599 -> throw InternalServerException(code)
        //TODO: Parse another type of errors
        in 400..499 -> throw ApiException(code, ApiError.fromJson(stringBody.toJsonObject()))
    }

    try {
        return apiRequest.convertJsonToResponse(stringBody)
    } catch (e: JSONException) {
        throw ParseException.ResponseParsingException(stringBody, e)
    }
}

internal fun String.toJsonObject(): JSONObject =
    try {
        JSONObject(this)
    } catch (e: JSONException) {
        throw ParseException.ResponseParsingException(this, e)
    }

internal fun String.toJsonArray(): JSONArray =
    try {
        JSONArray(this)
    } catch (e: JSONException) {
        throw ParseException.ResponseParsingException(this, e)
    }

internal fun createRequestBody(body: List<Pair<String, Any>>): String {
    val jsonObject = JSONObject()
    body.forEach { (key, value) ->
        if (value is Serializable) {
            jsonObject.put(key, value.toJson())
        } else {
            jsonObject.put(key, value)
        }
    }
    return jsonObject.toString()
}
