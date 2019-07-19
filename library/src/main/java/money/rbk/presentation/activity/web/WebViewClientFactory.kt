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

package money.rbk.presentation.activity.web

import android.graphics.Bitmap
import android.os.Build
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient

internal object WebViewClientFactory {

    private const val blankPage = "about:blank"

    private abstract class InterceptableWebViewClient(
        val onRequest: (url: String?) -> Unit,
        val onPageStarted: () -> Unit,
        val onPageFinished: () -> Unit) :
        WebViewClient() {
        override fun shouldInterceptRequest(view: WebView?,
            request: WebResourceRequest?): WebResourceResponse? {
            onRequest(request?.url?.toString())
            return super.shouldInterceptRequest(view, request)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            onPageStarted()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            onPageFinished()
        }
    }

    fun build(
        onRequest: (url: String?) -> Unit,
        onError: (url: String?) -> Unit,
        onPageStarted: () -> Unit,
        onPageFinished: () -> Unit): WebViewClient = when {

        Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> object :
            InterceptableWebViewClient(onRequest, onPageStarted, onPageFinished) {
            override fun onReceivedError(view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                onError(failingUrl)

                view?.loadUrl(blankPage)
                view?.invalidate()
            }
        }

        else -> object : InterceptableWebViewClient(onRequest, onPageStarted, onPageFinished) {
            override fun onReceivedError(view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                onError(request?.url?.toString())

                view?.loadUrl(blankPage)
                view?.invalidate()
            }

            override fun onReceivedHttpError(view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?) {
                super.onReceivedHttpError(view, request, errorResponse)
                onError(request?.url?.toString())

                view?.loadUrl(blankPage)
                view?.invalidate()
            }
        }
    }
}
