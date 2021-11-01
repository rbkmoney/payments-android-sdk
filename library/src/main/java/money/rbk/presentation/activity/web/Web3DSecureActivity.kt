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
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.rbk_ac_web_3ds.*
import money.rbk.R
import money.rbk.di.Injector
import money.rbk.presentation.model.BrowserRequestModel
import money.rbk.presentation.navigation.Navigator
import money.rbk.presentation.utils.adjustSize
import money.rbk.presentation.utils.getExtraBooleanOrError
import money.rbk.presentation.utils.getExtraStringOrError
import money.rbk.presentation.utils.isTablet

internal class Web3DSecureActivity : AppCompatActivity(), Web3DSecureView {

    companion object {
        const val REQUEST_CODE = 0x287

        const val RESULT_NETWORK_ERROR = 2

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

    private var isPost: Boolean = false
    private var body: ByteArray? = null
    private lateinit var redirectUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!isTablet) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rbk_ac_web_3ds)
        adjustSize()

        isPost = getExtraBooleanOrError(EXTRA_KEY_POST)
        redirectUrl = getExtraStringOrError(EXTRA_KEY_URL)
        body = intent?.getByteArrayExtra(EXTRA_KEY_BODY)

        webView.settings.domStorageEnabled = true
        webView.settings.databaseEnabled = true
        webView.settings.javaScriptEnabled = true

        webView.webViewClient =
            WebViewClientFactory.build({ presenter.onRequest(it) },
                { presenter.onError(it) },
                { showProgress() },
                { hideProgress() })
        loadPage()

        tvShopName.text = Injector.shopName
        ibtnClose.setOnClickListener { presenter.onCancel() }

        presenter.attachView(this)
    }

    override fun loadPage() {
        showProgress()

        if (isPost) {
            body?.let { webView.postUrl(redirectUrl, it) }
        } else {
            webView.loadUrl(redirectUrl)
        }
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    override fun showProgress() {
        lLoader.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        lLoader.visibility = View.GONE
    }

}
