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

package money.rbk.sample.network

import android.content.res.AssetManager
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody

class MockInterceptor(private val assets: AssetManager) : Interceptor {

    private fun readAssets(segments: String): ByteArray {
        val json = assets.open("$segments.json")
        val fileBytes = ByteArray(json.available())
        json.read(fileBytes)
        json.close()
        return fileBytes
    }

    override fun intercept(chain: Interceptor.Chain?) =
        chain?.request()?.run {
            val segment = url().pathSegments()
                .last()

            if (segment in listOf("test_invoice_templates", "test_invoices")) {

                Response.Builder()
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .request(this)
                    .message("Success")
                    .body(ResponseBody.create(MediaType.parse("application/json"),
                        readAssets(segment)))
                    .build()

            } else {
                chain.proceed(this)
            }

        }

}
