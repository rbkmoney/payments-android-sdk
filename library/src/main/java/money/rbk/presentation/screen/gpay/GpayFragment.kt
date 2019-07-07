package money.rbk.presentation.screen.gpay

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.wallet.AutoResolveHelper
import kotlinx.android.synthetic.main.fmt_google_pay.btnPay
import kotlinx.android.synthetic.main.fmt_google_pay.edEmail
import kotlinx.android.synthetic.main.fmt_google_pay.pbLoading
import money.rbk.R
import money.rbk.di.Injector
import money.rbk.presentation.activity.web.WebViewActivity
import money.rbk.presentation.model.BrowserRequestModel
import money.rbk.presentation.screen.base.BaseFragment
import money.rbk.presentation.utils.setValid

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 27.06.19
 */

class GpayFragment : BaseFragment<GpayView>(), GpayView {

    override val presenter: GpayPresenter by lazy { GpayPresenter(navigator) }

    private val userEmail = Injector.email

    companion object {
        fun newInstance() = GpayFragment()

        const val LOAD_PAYMENT_DATA_REQUEST_CODE = 123

    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fmt_google_pay, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edEmail.setText(userEmail)
    }

    override fun onReadyToPay() {
        btnPay.setOnClickListener {
            presenter.onPerformPayment(edEmail.text.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            WebViewActivity.REQUEST_CODE -> presenter.on3DsPerformed()
            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK ->
                        presenter.onGpayPaymentSuccess(data, edEmail.text.toString())

                    Activity.RESULT_CANCELED -> hideProgress()

                    AutoResolveHelper.RESULT_ERROR ->
                        presenter.onGpayPaymentError(data)
                }
                btnPay.isClickable = true
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun showEmailValid(isValid: Boolean) {
        edEmail.setValid(isValid)
    }

    override fun showRedirect(request: BrowserRequestModel) {
        startActivityForResult(WebViewActivity.buildIntent(activity!!, request),
            WebViewActivity.REQUEST_CODE)
    }

    override fun showProgress() {
        btnPay.isClickable = false
        pbLoading.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        btnPay.isClickable = true
        pbLoading.visibility = View.GONE
    }

}
