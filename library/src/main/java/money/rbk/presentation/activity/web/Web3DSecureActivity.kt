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

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentActivity
import money.rbk.R
import money.rbk.domain.converter.TERMINATION_URI
import money.rbk.presentation.model.BrowserRequestModel
import money.rbk.presentation.navigation.Navigator
import money.rbk.presentation.utils.adjustSize
import money.rbk.presentation.utils.isTablet

class Web3DSecureActivity : FragmentActivity() {

    companion object {
        const val REQUEST_CODE = 0x287

        private const val EXTRA_KEY_POST = "post"
        private const val EXTRA_KEY_URL = "url"
        private const val EXTRA_KEY_BODY = "body"

        fun buildIntent(activity: Activity, browserRequest: BrowserRequestModel) =
            Intent(activity, Web3DSecureActivity::class.java)
                .apply {
                    putExtra(EXTRA_KEY_POST, browserRequest.isPost)
                    putExtra(EXTRA_KEY_URL, browserRequest.requestUrl)
                    putExtra(EXTRA_KEY_BODY, browserRequest.body)
                }
    }

    val navigator by lazy { Navigator(this, R.id.container) }

    private val presenter by lazy { Web3DSecurePresenter(navigator) }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!isTablet) {
            setTheme(R.style.Theme_RBKMoney)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        super.onCreate(savedInstanceState)

        val isPost = intent?.getBooleanExtra(EXTRA_KEY_POST, false)!!
        val redirectUrl = intent?.getStringExtra(EXTRA_KEY_URL)!!
        val body = intent?.getByteArrayExtra(EXTRA_KEY_BODY)

        WebView(this).apply {
            webViewClient = object : WebViewClient() {

                // This method was deprecated in API level 23.
                override fun onReceivedError(view: WebView?,
                    errorCode: Int,
                    description: String?,
                    failingUrl: String?) {
                    super.onReceivedError(view, errorCode, description, failingUrl)
                }

                override fun onReceivedError(view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?) {
                    super.onReceivedError(view, request, error)
                }

                override fun onReceivedHttpError(view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?) {
                    super.onReceivedHttpError(view, request, errorResponse)
                }

                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean =
                    if (url.equals(TERMINATION_URI, ignoreCase = true)) {
                        setResult(RESULT_OK)
                        finish()
                        true
                    } else {
                        super.shouldOverrideUrlLoading(view, url)
                    }
            }

            if (isPost) {
                postUrl(redirectUrl, body)
            } else {
                loadUrl(redirectUrl)
            }

            setContentView(this)
        }
        adjustSize()
    }

}
