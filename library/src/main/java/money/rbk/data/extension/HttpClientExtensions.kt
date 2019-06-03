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

import android.util.Log
import money.rbk.data.exception.RequestExecutionException
import money.rbk.data.exception.ResponseParsingException
import money.rbk.data.exception.ResponseReadingException
import money.rbk.data.methods.base.ApiRequest
import money.rbk.data.methods.base.MimeType
import money.rbk.data.methods.base.PostRequest
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import money.rbk.data.serialization.Serializable
import java.io.IOException

internal fun <T> OkHttpClient.execute(
    apiRequest: ApiRequest<T>
): T {
    val url = apiRequest.getUrl()
    val requestBuilder = Request.Builder()
        .url(url)
        .addHeader("accept", "application/json")

    apiRequest.getHeaders()
        .forEach {
            requestBuilder.addHeader(it.first, it.second)
        }

    if (apiRequest is PostRequest<T>) {
        requestBuilder.addHeader("Content-Type", apiRequest.getMimeType().type)
        val body = createRequestBody(apiRequest.getMimeType(), apiRequest.getPayload())
        requestBuilder.post(RequestBody.create(null, body))
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

    Log.d(javaClass.name, "-> execute ->$stringBody")


    try {
        return apiRequest.convertJsonToResponse(stringBody)
    } catch (e: JSONException) {
        throw ResponseParsingException(stringBody, e)
    }
}

internal fun String.toJsonObject(): JSONObject =
    try {
        JSONObject(this)
    } catch (e: JSONException) {
        throw ResponseParsingException(this, e)
    }

internal fun String.toJsonArray(): JSONArray =
    try {
        JSONArray(this)
    } catch (e: JSONException) {
        throw ResponseParsingException(this, e)
    }

internal fun createRequestBody(mimeType: MimeType,
    body: List<Pair<String, Any>>): String {

    return when (mimeType) {
        MimeType.JSON -> {
            val jsonObject = JSONObject()
            body.forEach { (key, value) ->

                if (value is Serializable) {
                    jsonObject.put(key, value.toJson())
                } else {
                    jsonObject.put(key, value)
                }
            }
            jsonObject.toString()
        }
    }
}
