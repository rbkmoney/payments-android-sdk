package money.rbk.presentation.activity.web

import android.graphics.Bitmap
import android.os.Build
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 10.07.19
 */
object WebViewClientFactory {

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
