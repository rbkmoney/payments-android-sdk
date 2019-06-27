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
