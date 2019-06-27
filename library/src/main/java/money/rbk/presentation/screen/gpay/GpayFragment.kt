package money.rbk.presentation.screen.gpay

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentDataRequest
import kotlinx.android.synthetic.main.fmt_google_pay.*
import money.rbk.R
import money.rbk.di.Injector
import money.rbk.presentation.screen.base.BaseFragment

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 27.06.19
 */

class GpayFragment : BaseFragment<GpayView>(), GpayView {

    override val presenter: GpayPresenter by lazy { GpayPresenter(navigator) }

    private val userEmail = Injector.email

    companion object {
        fun newInstance() = GpayFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fmt_google_pay, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edEmail.setText(userEmail)
        btnPay.setOnClickListener { performPayment() }
    }

    private fun performPayment() {
        val paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(price)
        if (paymentDataRequestJson == null) {
            Log.e("RequestPayment", "Can't fetch payment data request")
            return
        }
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        if (request != null) {
            AutoResolveHelper.resolveTask(
                paymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE)
        }
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
