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
import android.webkit.WebView
import android.webkit.WebViewClient
import money.rbk.R
import money.rbk.domain.converter.TERMINATION_URI
import money.rbk.presentation.model.BrowserRequestModel
import money.rbk.presentation.utils.adjustSize
import money.rbk.presentation.utils.extra
import money.rbk.presentation.utils.extraNullable
import money.rbk.presentation.utils.isTablet

class WebViewActivity : Activity() {

    companion object {
        const val REQUEST_CODE = 0x287

        private const val EXTRA_KEY_POST = "post"
        private const val EXTRA_KEY_URL = "url"
        private const val EXTRA_KEY_BODY = "body"

        fun buildIntent(activity: Activity, browserRequest: BrowserRequestModel) =
            Intent(activity, WebViewActivity::class.java)
                .apply {
                    putExtra(EXTRA_KEY_POST, browserRequest.isPost)
                    putExtra(EXTRA_KEY_URL, browserRequest.requestUrl)
                    putExtra(EXTRA_KEY_BODY, browserRequest.body)
                }
    }

    private val isPost: Boolean by extra(EXTRA_KEY_POST)
    private val redirectUrl: String by extra(EXTRA_KEY_URL)
    private val body: ByteArray? by extraNullable(EXTRA_KEY_BODY)

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!isTablet) {
            setTheme(R.style.Theme_RBKMoney)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        super.onCreate(savedInstanceState)
        WebView(this).apply {
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean =
                    if (url == TERMINATION_URI) {
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